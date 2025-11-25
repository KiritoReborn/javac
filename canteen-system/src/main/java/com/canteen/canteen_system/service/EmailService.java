package com.canteen.canteen_system.service;

import com.canteen.canteen_system.model.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderStatusEmail(String toEmail, Long orderId, OrderStatus status) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Order Status Update - Order #" + orderId);
            message.setText(buildEmailContent(orderId, status));
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private String buildEmailContent(Long orderId, OrderStatus status) {
        return String.format("""
                Dear Customer,
                
                Your order #%d status has been updated to: %s
                
                %s
                
                Thank you for using our Canteen Service!
                
                Best regards,
                College Canteen Team
                """,
                orderId,
                status.name(),
                getStatusMessage(status)
        );
    }

    private String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Your order has been received and is being processed.";
            case PREPARING -> "Your order is being prepared by our kitchen staff.";
            case READY -> "Your order is ready for pickup! Please collect it from the counter.";
            case COMPLETED -> "Your order has been completed. Thank you!";
            case CANCELLED -> "Your order has been cancelled.";
        };
    }
}
