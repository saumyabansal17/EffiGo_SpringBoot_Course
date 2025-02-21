package com.effigo.ems.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.effigo.ems.dto.SignupRequest;
import com.effigo.ems.dto.UserDetailsDto;
import com.effigo.ems.enums.UserStatus;
import com.effigo.ems.model.FinancialDocument;
import com.effigo.ems.model.Role;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.FinancialDocRepository;
import com.effigo.ems.repository.RoleRepository;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.security.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private FinancialDocRepository documentRepository;

	@Autowired
	private S3Service s3Service;

	public ResponseEntity<List<UserDetailsDto>> getAllUsers(String token) {
        if (token == null || token.isEmpty() || !jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(401).body(Collections.emptyList()); // Unauthorized
        }
        log.info("Inside getAllUsers method");
        String email = jwtUtil.getUserNameFromJwtToken(token);
        int r_id = usersRepository.findIdByEmail(email);
        List<UserDetailsDto> users;
        if (r_id == 1) {
        	users = usersRepository.findUDetailsByEmail(email);
        } else {
        	users = usersRepository.findDetailsByEmail(email);
        }
        
        log.info("Users: {}", users);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body(users);
        }

        return ResponseEntity.ok(users);
    }
	
	public String getName(String token) {
        String email = jwtUtil.getUserNameFromJwtToken(token);
        return usersRepository.findNameByEmail(email);
    }
	
	public String addUser(SignupRequest request) {
        try {
            if (usersRepository.findByEmailId(request.getEmailId()) != null) {
                return "Email is already in use!";
            }

            Users user = new Users();
            user.setEmailId(request.getEmailId());
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhone_no(request.getPhone_no());
            user.setRegister_at();
            
            Role userRole = roleRepository.findById(request.getRole_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID!"));

            user.setStatus(UserStatus.ACTIVE);
            user.setRole(userRole);

            usersRepository.save(user);
            log.info("User added successfully: {}", user);
            return "User added successfully!";
        } catch (Exception e) {
            log.error("Error adding user: ", e);
            return "Error: " + e.getMessage();
        }
    }
	
    public String updateUser(UUID id, UserDetailsDto request) {
        try {
            log.info("Updating user with ID: {}", id);
            Optional<Users> optionalUser = usersRepository.findById(id);
            if (!optionalUser.isPresent()) {
                log.warn("User not found");
                return "User not found";
            }
            Users user = optionalUser.get();
            log.info("Current User Data: {}", user);
            
            if (!user.getEmailId().equals(request.getEmailId()) && usersRepository.findByEmailId(request.getEmailId()) != null) {
                return "Username already exists (Constraint Violation)";
            }
            
            UserStatus status;
            try {
                status = UserStatus.valueOf(request.getStatus().toString());
            } catch (IllegalArgumentException e) {
                return "Invalid user status!";
            }
            
            user.setEmailId(request.getEmailId());
            user.setPhone_no(request.getPhone());
            user.setStatus(status);
            int rid = roleRepository.findIdByRole(request.getRole());
            Role role = roleRepository.findById(rid)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID!"));
            user.setRole(role);
            
            usersRepository.save(user);
            log.info("User updated successfully: {}", user);
            return "User Updated successfully";
        } catch (DataIntegrityViolationException e) {
            log.error("Constraint Violation Error: ", e);
            return "Username already exists (Constraint Violation)";
        } catch (Exception e) {
            log.error("Updation failed due to an unexpected error: ", e);
            return "Updation failed due to an unexpected error";
        }
    }

	public void deleteUser(UUID id) {
    	log.info("Deleting user with ID: {}", id);
    	List<FinancialDocument> documents = documentRepository.findDocumentsByUserId(id);
    	usersRepository.deleteById(id);
    	    
    	for (FinancialDocument doc : documents) {
    	    s3Service.deleteFile(doc.getDoc_url());
    	    documentRepository.delete(doc);
    	}
    	log.info("User and associated documents deleted successfully");
    } 
}
