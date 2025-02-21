package com.effigo.ems.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.effigo.ems.dto.UpdateProfileDto;
import com.effigo.ems.dto.ViewProfileDto;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.security.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String getName(String token) {
        String email = jwtUtil.getUserNameFromJwtToken(token);
        log.info("Fetching name for email: {}", email);
        return userRepository.findNameByEmail(email);
    }

    public UUID getId(String token) {
        String email = jwtUtil.getUserNameFromJwtToken(token);
        log.info("Fetching UUID for email: {}", email);
        return userRepository.findUIdByEmail(email);
    }

    public ResponseEntity<ViewProfileDto> viewDetails(String token) {
        if (token == null || token.isEmpty() || !jwtUtil.validateJwtToken(token)) {
            log.error("Invalid or missing JWT token.");
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        String email = jwtUtil.getUserNameFromJwtToken(token);
        log.info("Fetching details for email: {}", email);

        ViewProfileDto userProfile = userRepository.viewDetailsByEmail(email);
        if (userProfile == null) {
            log.warn("User profile not found for email: {}", email);
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(userProfile);
    }

    public String updateUser(UUID id, UpdateProfileDto request) {
        try {
            log.info("Attempting to update user with ID: {}", id);

            Users user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("User with ID {} not found.", id);
                        return new RuntimeException("User not found");
                    });

            log.info("Updating user details: {}", request);

            user.setPhone_no(request.getPhone());
            user.setName(request.getName());

            userRepository.save(user);
            log.info("User updated successfully: {}", user);

            return "User updated successfully";
        } catch (DataIntegrityViolationException e) {
            log.error("Constraint violation while updating user: {}", e.getMessage());
            return "Username already exists (Constraint Violation)";
        } catch (Exception e) {
            log.error("Unexpected error while updating user: {}", e.getMessage());
            return "Updation failed due to an unexpected error";
        }
    }
}
