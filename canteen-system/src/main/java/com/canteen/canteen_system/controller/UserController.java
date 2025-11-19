package com.canteen.canteen_system.controller;

import com.canteen.canteen_system.dto.UserDto;
import com.canteen.canteen_system.mapper.UserMapper;
import com.canteen.canteen_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@RestController
@AllArgsConstructor

public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/users/id/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        var user= userRepository.findById(id).orElse(null);
        if(user==null){
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(userMapper.userToUserDto(user));
        }
    }

    @GetMapping("/users")
    public Iterable<UserDto> getAllUsers(@RequestParam(required = false,defaultValue = "",name="sort") String sort){
        if(!Set.of("name","email").contains(sort)){
            sort="name";
        }
        return userRepository.findAll(Sort.by(sort))
                .stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    @GetMapping("/users/name/{name}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable String name){
        var user=userRepository.findByName(name);
        if(user==null){
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(userMapper.userToUserDto(user));
        }
    }




}
