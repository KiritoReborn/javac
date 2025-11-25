package com.canteen.canteen_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsDto {
    private Long totalOrders;
    private Double totalRevenue;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Long totalUsers;
    private Long totalMenuItems;
}
