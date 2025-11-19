package com.canteen.canteen_system.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

enum Role {
    STUDENT,
    STAFF
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    //private Role role;
    private LocalDateTime createdAt;
}
