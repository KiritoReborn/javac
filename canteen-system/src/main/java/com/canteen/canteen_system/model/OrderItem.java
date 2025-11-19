package com.canteen.canteen_system.model;

import jakarta.persistence.*;
import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Entity
@Table(name="order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name="menu_item_id")
    private MenuItem menuItem;
    private int quantity;
    }