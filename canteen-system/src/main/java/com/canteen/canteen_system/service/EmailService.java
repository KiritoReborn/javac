package com.canteen.canteen_system.service;

import com.canteen.canteen_system.model.OrderStatus;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Async("emailExecutor")
    public CompletableFuture<Void> sendOrderStatusEmail(String toEmail, Long orderId, OrderStatus status) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Sending email to {} for order {} on thread {}", 
                    toEmail, orderId, Thread.currentThread().getName());
                
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Order Status Update - Order #" + orderId);
                message.setText(buildEmailContent(orderId, status));
                
                mailSender.send(message);
                
                logger.info("Email sent successfully to {} for order {}", toEmail, orderId);
            } catch (Exception e) {
                logger.error("Failed to send email to {} for order {}: {}", 
                    toEmail, orderId, e.getMessage());
            }
        });
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
