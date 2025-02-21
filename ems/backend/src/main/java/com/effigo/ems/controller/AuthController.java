package com.effigo.ems.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.effigo.ems.dto.AuthResponse;
import com.effigo.ems.dto.LoginRequest;
import com.effigo.ems.dto.SignupRequest;
import com.effigo.ems.model.RefreshToken;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.RefreshTokenRepository;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.security.JwtUtil;
import com.effigo.ems.service.AuthService;
import com.effigo.ems.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest signupRequest) {
        log.info("Registering user: {}", signupRequest.getEmailId());
        String response = authService.register(signupRequest);

        switch (response) {
            case "User registered successfully!":
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            case "Email is already in use!":
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            case "Invalid role ID!":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            log.info("Login attempt for user: {}", request.getEmailId());
            AuthResponse authResponse = authService.login(request, response);
            if(authResponse.getMessage().equals("User is inactive, please contact admin.")) {
            	return ResponseEntity.ok().body(new AuthResponse("Login Failed","User is inactive"));
            }
            Users user = userRepository.findByEmailId(request.getEmailId());
            Map<String, Object> userData = new HashMap<>();
            userData.put("role", user.getRole().getRole());
            userData.put("id", user.getId());
            
            return ResponseEntity.ok(userData);
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for user: {}", request.getEmailId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Error", "Invalid username or password"));
        } catch (Exception e) {
            log.error("Error during login for user: {}", request.getEmailId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("Error", "An error occurred during login"));
        }
    }
    
    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@CookieValue(name = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateJwtToken(token)) {
            log.warn("Invalid or expired token {}",token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        return ResponseEntity.ok(Map.of("valid", true));
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {
        log.info("Refreshing access token");

        if (!requestBody.containsKey("id")) {
            log.warn("User ID missing in refresh token request");
            return ResponseEntity.badRequest().body("User ID is missing");
        }

        UUID userId;
        try {
            userId = UUID.fromString(requestBody.get("id"));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", requestBody.get("id"));
            return ResponseEntity.badRequest().body("Invalid UUID format");
        }
        
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("User not found for ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user);
        if (refreshToken == null || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Expired or invalid refresh token for user: {}", user.getEmailId());
            refreshTokenService.deleteByToken(refreshToken.getToken());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Expired refresh token");
        }

        String newAccessToken = jwtUtil.generateTokenFromUsername(user.getEmailId());
        log.info("Generated new access token for user: {}", user.getEmailId());

        ResponseCookie accessTokenCookie = ResponseCookie.from("token", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        return ResponseEntity.ok("Access token refreshed successfully");
    }
}
