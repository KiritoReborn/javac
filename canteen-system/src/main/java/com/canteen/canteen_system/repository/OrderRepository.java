package com.canteen.canteen_system.repository;

import com.canteen.canteen_system.model.Order;
import com.canteen.canteen_system.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserIdOrderByCreatedAtAsc(Long userId);
    List<Order> findByStatusOrderByCreatedAtAsc(OrderStatus status);
    List<Order> findAllByOrderByCreatedAtAsc();
    Page<Order> findAll(Pageable pageable);
    Page<Order> findByUserId(Long userId, Pageable pageable);
}
