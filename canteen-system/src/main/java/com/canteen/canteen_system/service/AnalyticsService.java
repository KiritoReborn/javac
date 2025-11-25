package com.canteen.canteen_system.service;

import com.canteen.canteen_system.dto.AnalyticsDto;
import com.canteen.canteen_system.model.OrderStatus;
import com.canteen.canteen_system.repository.MenuRepository;
import com.canteen.canteen_system.repository.OrderRepository;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public AnalyticsDto getAnalytics() {
        AnalyticsDto analytics = new AnalyticsDto();
        
        analytics.setTotalOrders(orderRepository.count());
        analytics.setTotalUsers(userRepository.count());
        analytics.setTotalMenuItems(menuRepository.count());
        
        analytics.setPendingOrders((long) orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.PENDING).size());
        analytics.setCompletedOrders((long) orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.COMPLETED).size());
        analytics.setCancelledOrders((long) orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.CANCELLED).size());
        
        // Calculate total revenue from completed orders
        Double totalRevenue = orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.COMPLETED).stream()
                .mapToDouble(order -> order.getTotalPrice())
                .sum();
        analytics.setTotalRevenue(totalRevenue);
        
        return analytics;
    }
}
