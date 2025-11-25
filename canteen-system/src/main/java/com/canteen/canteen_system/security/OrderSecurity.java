package com.canteen.canteen_system.security;

import com.canteen.canteen_system.model.Order;
import com.canteen.canteen_system.model.User;
import com.canteen.canteen_system.repository.OrderRepository;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@AllArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public boolean isOrderOwner(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        
        if (currentUser == null) {
            return false;
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }

        return order.getUser().getId().equals(currentUser.getId());
    }
}
