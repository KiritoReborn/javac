package com.canteen.canteen_system.controller;

import com.canteen.canteen_system.model.User;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor

public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id){
        return userRepository.findById(id).get();
    }

    @GetMapping("/users")
    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

}
