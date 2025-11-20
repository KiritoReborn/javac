package com.canteen.canteen_system.controller;


import com.canteen.canteen_system.dto.UserDto;
import com.canteen.canteen_system.model.User;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class AuthController {


    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }
    @PostMapping("/register/student")
    public String Register(@RequestBody User user){

        return "Success";
    }





}
