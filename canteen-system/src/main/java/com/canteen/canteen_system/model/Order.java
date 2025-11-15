package com.canteen.canteen_system.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

enum OrderStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many orders can belong to one user.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // This creates the 'user_id' foreign key
    private User user;

    // One order can have many order items.
    // 'mappedBy = "order"' tells JPA the 'order' field in OrderItem manages this relationship
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING) // Using an enum is safer than a raw string
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private double totalPrice; // Good to store the calculated total

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper method to add items and maintain consistency
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}