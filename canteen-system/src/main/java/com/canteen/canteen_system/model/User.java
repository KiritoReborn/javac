package com.canteen.canteen_system.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

enum Role {
    STUDENT,
    STAFF
}

@Getter // Lombok: Creates all getter methods
@Setter // Lombok: Creates all setter methods
@NoArgsConstructor // Lombok: Creates the required public User() {} constructor
@AllArgsConstructor // Lombok: Creates a constructor with all fields
@Entity // Tells JPA this class is a database entity
@Table(name = "users") // Links this class to the "users" table in MySQL
public class User {

    @Id // Marks this field as the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells MySQL to auto-increment this ID
    private Long id;

    @Column(nullable = false, length = 100) // Maps to a column, must not be null
    private String name;

    @Column(nullable = false, unique = true, length = 100) // Must not be null and must be unique
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Tells JPA to store the ENUM as a String ("STUDENT" or "STAFF")
    @Column(nullable = false)
    private Role role; // Use the Role enum you just created

    @CreationTimestamp // Automatically sets this field to the current time when a user is created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}