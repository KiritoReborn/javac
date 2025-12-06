package com.canteen.canteen_system.service;

import com.canteen.canteen_system.dto.OrderItemDto;
import com.canteen.canteen_system.dto.OrderRequestDto;
import com.canteen.canteen_system.exception.BadRequestException;
import com.canteen.canteen_system.exception.ResourceNotFoundException;
import com.canteen.canteen_system.model.*;
import com.canteen.canteen_system.repository.MenuRepository;
import com.canteen.canteen_system.repository.OrderRepository;
import com.canteen.canteen_system.repository.OrderTokenRepository;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final OrderTokenRepository orderTokenRepository;
    private final NotificationService notificationService;

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtAsc();
    }
    
    public Page<Order> getAllOrdersPaginated(Pageable pageable) {
        // Override to ensure FCFS ordering (createdAt ascending)
        Pageable sortedPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by("createdAt").ascending()
        );
        return orderRepository.findAll(sortedPageable);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtAsc(userId);
    }
    
    public Page<Order> getOrdersByUserIdPaginated(Long userId, Pageable pageable) {
        // Override to ensure FCFS ordering (createdAt ascending)
        Pageable sortedPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by("createdAt").ascending()
        );
        return orderRepository.findByUserId(userId, sortedPageable);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Transactional
    public Order createOrder(OrderRequestDto orderRequest) {
        logger.info("Creating order for user {} on thread {}", 
            orderRequest.getUserId(), Thread.currentThread().getName());
        
        // Validate user exists
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", orderRequest.getUserId()));

        // Security Check: Ensure user can only order for themselves (unless they're Staff)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Staff"));
        
        // If not staff, user can only create orders for themselves
        if (!isStaff && !currentUser.getId().equals(orderRequest.getUserId())) {
            throw new BadRequestException("You can only create orders for yourself");
        }

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>());

        double totalPrice = 0.0;

        // Process each item in the order
        for (OrderItemDto itemDto : orderRequest.getItems()) {
            // Validate menu item exists and is available
            MenuItem menuItem = menuRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemDto.getMenuItemId()));

            if (!menuItem.isAvailable()) {
                throw new BadRequestException("Menu item '" + menuItem.getItemname() + "' is not available");
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setItemPrice(menuItem.getPrice());

            // Add to order
            order.getOrderItems().add(orderItem);

            // Calculate total
            totalPrice += menuItem.getPrice() * itemDto.getQuantity();
        }

        order.setTotalPrice(totalPrice);

        // Save order (cascade will save order items)
        Order savedOrder = orderRepository.save(order);
        
        // Generate token for the order
        generateTokenForOrder(savedOrder);
        
        // Send notification asynchronously (non-blocking)
        notificationService.sendNewOrderNotification(savedOrder);
        
        logger.info("Order {} created successfully", savedOrder.getId());

        return savedOrder;
    }
    
    private void generateTokenForOrder(Order order) {
        String tokenNumber = "TKN" + String.format("%05d", new Random().nextInt(100000));
        
        OrderToken token = new OrderToken();
        token.setTokenNumber(tokenNumber);
        token.setOrder(order);
        token.setStatus(TokenStatus.ACTIVE);
        token.setIssuedAt(LocalDateTime.now());
        
        orderTokenRepository.save(token);
    }
    
    public OrderToken getTokenByOrderId(Long orderId) {
        return orderTokenRepository.findByOrderId(orderId).orElse(null);
    }
    
    public Order getOrderByToken(String tokenNumber) {
        OrderToken token = orderTokenRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found: " + tokenNumber));
        return token.getOrder();
    }
    
    @Transactional
    public void markOrderAsPickedUp(String tokenNumber) {
        OrderToken token = orderTokenRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found: " + tokenNumber));
        
        token.setStatus(TokenStatus.PICKED_UP);
        token.setPickedUpAt(LocalDateTime.now());
        orderTokenRepository.save(token);
        
        Order order = token.getOrder();
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.info("Updating order {} status to {} on thread {}", 
            orderId, newStatus, Thread.currentThread().getName());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        // Send notifications asynchronously (non-blocking)
        notificationService.sendOrderStatusNotifications(updatedOrder, newStatus);
        
        logger.info("Order {} status updated successfully", orderId);
        
        return updatedOrder;
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Only allow cancellation if order is still PENDING
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            return true;
        }

        throw new BadRequestException("Cannot cancel order. Order is already " + order.getStatus());
    }

    @Transactional
    public boolean deleteOrder(Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        }
        throw new ResourceNotFoundException("Order", "id", orderId);
    }
}
