package com.canteen.canteen_system.model;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Entity 
@Table(name = "menu_items") 
public class MenuItem {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String itemname;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private double price;
    
    @Column(nullable = false, length = 50)
    private String category;

    
}