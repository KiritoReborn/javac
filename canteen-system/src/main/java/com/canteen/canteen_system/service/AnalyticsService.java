package com.canteen.canteen_system.service;

import com.canteen.canteen_system.dto.AnalyticsDto;
import com.canteen.canteen_system.model.OrderStatus;
import com.canteen.canteen_system.repository.MenuRepository;
import com.canteen.canteen_system.repository.OrderRepository;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public AnalyticsDto getAnalytics() {
        logger.info("Fetching analytics on thread: {}", Thread.currentThread().getName());
        
        AnalyticsDto analytics = new AnalyticsDto();
        
        try {
            // Execute multiple queries in parallel using CompletableFuture
            CompletableFuture<Long> totalOrdersFuture = CompletableFuture.supplyAsync(
                () -> orderRepository.count()
            );
            
            CompletableFuture<Long> totalUsersFuture = CompletableFuture.supplyAsync(
                () -> userRepository.count()
            );
            
            CompletableFuture<Long> totalMenuItemsFuture = CompletableFuture.supplyAsync(
                () -> menuRepository.count()
            );
            
            CompletableFuture<Long> pendingOrdersFuture = CompletableFuture.supplyAsync(
                () -> (long) orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.PENDING).size()
            );
            
            CompletableFuture<Long> completedOrdersFuture = CompletableFuture.supplyAsync(
                () -> (long) orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.COMPLETED).size()
            );
            
            CompletableFuture<Long> cancelledOrdersFuture = CompletableFuture.supplyAsync(
                () -> (long) orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.CANCELLED).size()
            );
            
            CompletableFuture<Double> totalRevenueFuture = CompletableFuture.supplyAsync(
                () -> orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.COMPLETED).stream()
                    .mapToDouble(order -> order.getTotalPrice())
                    .sum()
            );
            
            // Wait for all futures to complete
            CompletableFuture.allOf(
                totalOrdersFuture, 
                totalUsersFuture, 
                totalMenuItemsFuture,
                pendingOrdersFuture, 
                completedOrdersFuture, 
                cancelledOrdersFuture,
                totalRevenueFuture
            ).join();
            
            // Set all values
            analytics.setTotalOrders(totalOrdersFuture.get());
            analytics.setTotalUsers(totalUsersFuture.get());
            analytics.setTotalMenuItems(totalMenuItemsFuture.get());
            analytics.setPendingOrders(pendingOrdersFuture.get());
            analytics.setCompletedOrders(completedOrdersFuture.get());
            analytics.setCancelledOrders(cancelledOrdersFuture.get());
            analytics.setTotalRevenue(totalRevenueFuture.get());
            
            logger.info("Analytics calculated successfully using parallel processing");
            
        } catch (Exception e) {
            logger.error("Error calculating analytics: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate analytics", e);
        }
        
        return analytics;
    }
    
    @Async("analyticsExecutor")
    public CompletableFuture<AnalyticsDto> getAnalyticsAsync() {
        return CompletableFuture.supplyAsync(this::getAnalytics);
    }
}
