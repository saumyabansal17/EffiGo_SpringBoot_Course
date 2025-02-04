package com.effigo.task2_security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.effigo.task2_security.config.JwtTokenConfig;
import com.effigo.task2_security.dto.AuthResponse;
import com.effigo.task2_security.dto.AuthData;
import com.effigo.task2_security.dto.LoginRequest;
import com.effigo.task2_security.dto.SignupRequest;
import com.effigo.task2_security.repository.UsersRepository;
import com.effigo.task2_security.service.UsersService;

@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenConfig jwtUtil;
    private final UsersService userService;
    private final UsersRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenConfig jwtUtil, UsersService userService,
                           UsersRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
           
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = jwtUtil.generateTokenFromUsername(request.getUsername());

            AuthResponse response = new AuthResponse("Success", "Login successful", new AuthData(token));
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {

            AuthResponse errorResponse = new AuthResponse("Error", "Invalid username or password", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully!");
    }
}
