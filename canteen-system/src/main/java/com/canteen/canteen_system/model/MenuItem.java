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

@Getter // Lombok: Creates all getter methods
@Setter // Lombok: Creates all setter methods
@NoArgsConstructor // Lombok: Creates the required public User() {} constructor
@AllArgsConstructor // Lombok: Creates a constructor with all fields
@Entity // Tells JPA this class is a database entity
@Table(name = "menu_items") // Links this class to the "menu_items" table in MySQL

public class MenuItem {
    
    @Id // Marks this field as the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells MySQL to auto-increment this ID
    private Long id;
    @Column(nullable = false, length = 100) // Maps to a column, must not be null
    private String itemname;
    @Column(nullable = false, length = 255) // Maps to a column, must not be null
    private String description;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false, length = 50)
    private String category;

    
}