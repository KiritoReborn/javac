package com.canteen.canteen_system.model;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="menu_items")
@Data
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "itemname")
    private String itemname;
    
    private double price;
    private String category;
    private boolean available=true;
}