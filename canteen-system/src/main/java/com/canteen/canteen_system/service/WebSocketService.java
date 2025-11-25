package com.canteen.canteen_system.service;

import com.canteen.canteen_system.dto.OrderResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderUpdate(OrderResponseDto order) {
        messagingTemplate.convertAndSend("/topic/orders", order);
    }

    public void sendOrderStatusUpdate(Long orderId, String status) {
        messagingTemplate.convertAndSend("/topic/order-status/" + orderId, status);
    }
}
