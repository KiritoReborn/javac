package com.canteen.canteen_system.repository;

import com.canteen.canteen_system.model.OrderToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderTokenRepository extends JpaRepository<OrderToken, Long> {
    Optional<OrderToken> findByTokenNumber(String tokenNumber);
    Optional<OrderToken> findByOrderId(Long orderId);
}
