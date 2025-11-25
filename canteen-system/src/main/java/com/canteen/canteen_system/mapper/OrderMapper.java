package com.canteen.canteen_system.mapper;

import com.canteen.canteen_system.dto.OrderItemDto;
import com.canteen.canteen_system.dto.OrderResponseDto;
import com.canteen.canteen_system.model.Order;
import com.canteen.canteen_system.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "orderItems", target = "items")
    OrderResponseDto orderToOrderResponseDto(Order order);
    
    @Mapping(source = "menuItem.id", target = "menuItemId")
    @Mapping(source = "menuItem.itemname", target = "itemName")
    OrderItemDto orderItemToOrderItemDto(OrderItem orderItem);
    
    List<OrderItemDto> orderItemsToOrderItemDtos(List<OrderItem> orderItems);
    
    List<OrderResponseDto> ordersToOrderResponseDtos(List<Order> orders);
}
