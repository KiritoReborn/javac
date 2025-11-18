package com.canteen.canteen_system.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

enum Role {
    STUDENT,
    STAFF
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
}
