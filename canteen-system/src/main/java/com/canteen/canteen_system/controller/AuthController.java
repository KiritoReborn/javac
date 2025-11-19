package com.canteen.canteen_system.controller;


import com.canteen.canteen_system.model.User;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class AuthController {


    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }
    @GetMapping("/register")
    public String Register(){
        return "Enter Details";
    }





}
