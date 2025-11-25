package com.canteen.canteen_system.controller;

import com.canteen.canteen_system.dto.OrderRequestDto;
import com.canteen.canteen_system.dto.OrderResponseDto;
import com.canteen.canteen_system.mapper.OrderMapper;
import com.canteen.canteen_system.model.Order;
import com.canteen.canteen_system.model.OrderStatus;
import com.canteen.canteen_system.model.OrderToken;
import com.canteen.canteen_system.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (page == 0 && size == 10) {
            // Return all without pagination for backward compatibility
            List<Order> orders = orderService.getAllOrders();
            List<OrderResponseDto> response = orders.stream()
                    .map(order -> {
                        OrderResponseDto dto = orderMapper.orderToOrderResponseDto(order);
                        OrderToken token = orderService.getTokenByOrderId(order.getId());
                        if (token != null) {
                            dto.setTokenNumber(token.getTokenNumber());
                        }
                        return dto;
                    }).toList();
            return ResponseEntity.ok(response);
        }
        
        // Pagination with FCFS ordering handled by service
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = orderService.getAllOrdersPaginated(pageable);
        List<OrderResponseDto> response = ordersPage.getContent().stream()
                .map(order -> {
                    OrderResponseDto dto = orderMapper.orderToOrderResponseDto(order);
                    OrderToken token = orderService.getTokenByOrderId(order.getId());
                    if (token != null) {
                        dto.setTokenNumber(token.getTokenNumber());
                    }
                    return dto;
                }).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Staff') or @orderSecurity.isOrderOwner(#id)")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        OrderResponseDto dto = orderMapper.orderToOrderResponseDto(order);
        OrderToken token = orderService.getTokenByOrderId(id);
        if (token != null) {
            dto.setTokenNumber(token.getTokenNumber());
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('Staff') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderResponseDto> response = orders.stream()
                .map(order -> {
                    OrderResponseDto dto = orderMapper.orderToOrderResponseDto(order);
                    OrderToken token = orderService.getTokenByOrderId(order.getId());
                    if (token != null) {
                        dto.setTokenNumber(token.getTokenNumber());
                    }
                    return dto;
                }).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        List<Order> orders = orderService.getOrdersByStatus(orderStatus);
        return ResponseEntity.ok(orderMapper.ordersToOrderResponseDtos(orders));
    }
    
    @GetMapping("/token/{tokenNumber}")
    public ResponseEntity<OrderResponseDto> getOrderByToken(@PathVariable String tokenNumber) {
        Order order = orderService.getOrderByToken(tokenNumber);
        OrderResponseDto dto = orderMapper.orderToOrderResponseDto(order);
        dto.setTokenNumber(tokenNumber);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        OrderResponseDto dto = orderMapper.orderToOrderResponseDto(order);
        OrderToken token = orderService.getTokenByOrderId(order.getId());
        if (token != null) {
            dto.setTokenNumber(token.getTokenNumber());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        Order order = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(orderMapper.orderToOrderResponseDto(order));
    }
    
    @PostMapping("/pickup/{tokenNumber}")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<String> markOrderAsPickedUp(@PathVariable String tokenNumber) {
        orderService.markOrderAsPickedUp(tokenNumber);
        return ResponseEntity.ok("Order picked up successfully");
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('Staff') or @orderSecurity.isOrderOwner(#id)")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
