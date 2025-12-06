package com.canteen.canteen_system.controller;

import com.canteen.canteen_system.dto.ChangePasswordRequest;
import com.canteen.canteen_system.dto.UpdateDto;
import com.canteen.canteen_system.dto.UserDto;
import com.canteen.canteen_system.mapper.UserMapper;
import com.canteen.canteen_system.model.User;
import com.canteen.canteen_system.repository.UserRepository;
import com.canteen.canteen_system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users/id/{id}")
    @PreAuthorize("hasRole('Staff') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(userMapper.userToUserDto(user));
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('Staff')")
    public List<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "", name = "sort") String sort) {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/name/{name}")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<UserDto> getUserByName(@PathVariable String name) {
        User user = userService.getUserByName(name);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }

    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<UserDto> userDtos = users.stream()
                    .map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDtos);
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("@userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UpdateDto updateDto) {
        var user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            // Only update name and email (not role, password, or id)
            userMapper.updateFromDto(updateDto, user);
            userRepository.save(user);
            return ResponseEntity.ok(userMapper.userToUserDto(user));
        }
    }

    @PostMapping("/users/{id}/change-password")
    @PreAuthorize("@userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> changepassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        var user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            // Use password encoder to verify old password
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Old password is incorrect");
            } else {
                // Encrypt new password before saving
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                return ResponseEntity.ok().body("Password changed successfully");
            }
        }
    }

    @DeleteMapping("/deleteuser")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@RequestBody User deletedUser) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        var user = userService.getUserByEmail(currentUserEmail);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            // Verify password before deletion
            if (passwordEncoder.matches(deletedUser.getPassword(), user.getPassword())) {
                userRepository.delete(user);
                return ResponseEntity.ok().body("Account deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Invalid password");
            }
        }
    }
}
