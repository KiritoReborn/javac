package com.canteen.canteen_system.controller;

import com.canteen.canteen_system.dto.AuthResponse;
import com.canteen.canteen_system.dto.LoginDto;
import com.canteen.canteen_system.model.User;
import com.canteen.canteen_system.security.JwtTokenProvider;
import com.canteen.canteen_system.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, UriComponentsBuilder uribuilder) {
        try {
            User registeredUser = userService.registerUser(user);
            
            // Generate token for newly registered user
            String token = tokenProvider.generateTokenFromEmail(registeredUser.getEmail());
            
            AuthResponse authResponse = new AuthResponse(
                    token,
                    registeredUser.getId(),
                    registeredUser.getName(),
                    registeredUser.getEmail(),
                    registeredUser.getRole().name()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);
            User user = userService.getUserByEmail(loginDto.getEmail());
            
            AuthResponse authResponse = new AuthResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}
