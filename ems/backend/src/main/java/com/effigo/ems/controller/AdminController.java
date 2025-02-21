package com.effigo.ems.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.effigo.ems.dto.ImportResult;
import com.effigo.ems.dto.SignupRequest;
import com.effigo.ems.dto.UserDetailsDto;
import com.effigo.ems.enums.UserStatus;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.RoleRepository;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.service.AdminService;
import com.effigo.ems.service.EmailService;
import com.effigo.ems.service.ExcelService;



@RestController
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private AdminService adminService;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ExcelService excelUserService;

	@Autowired
	private EmailService emailService;

	@GetMapping("/all")
    public ResponseEntity<List<UserDetailsDto>> getAllUsers(@CookieValue(name = "token", required = false) String token) {
        return adminService.getAllUsers(token);
    }

	@GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@CookieValue(name = "token", required = false) String token) {
		String name = adminService.getName(token);
    	Map<String, Object> userData = new HashMap<>();
    	UUID id = usersRepository.findUIdByName(name);
    	userData.put("name", name);
    	userData.put("id", id);
        return ResponseEntity.ok(userData);
    }

	@GetMapping("/approveUser")
    public ResponseEntity<String> approveUser(@RequestParam String email) {
        Users user = usersRepository.findByEmailId(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        user.setStatus(UserStatus.ACTIVE);
        usersRepository.save(user);
        emailService.sendAcceptanceNotification(email);
        return ResponseEntity.ok("User approved successfully!");
    }

    @GetMapping("/rejectUser")
    public ResponseEntity<String> rejectUser(@RequestParam String email) {
        Users user = usersRepository.findByEmailId(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        usersRepository.delete(user);
        return ResponseEntity.ok("User registration rejected!");
    }
    
	@PostMapping("/addUser")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
	    try {
	        String response = adminService.addUser(request);
	        if ("User added successfully!".equals(response)) {
	            Users user = usersRepository.findByEmailId(request.getEmailId());
	            if (user != null) {
	                UserDetailsDto userDto = new UserDetailsDto();
	                userDto.setId(user.getId());
	                userDto.setPhone(user.getPhone_no());
	                userDto.setEmailId(user.getEmailId());
	                userDto.setStatus("ACTIVE");
	                if(request.getRole_id() == 2) {
	                	userDto.setRole("USER");
	                } else if(request.getRole_id() == 1){
	                	userDto.setRole("ADMIN");
	                } else if(request.getRole_id() == 0){
	                	userDto.setRole("SUPER_ADMIN");
	                } else {
	                	userDto.setRole(null);
	                }
	                return ResponseEntity.ok(userDto);
	            }
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        } 
	        if ("Email is already in use!".equals(response)) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
	        } 
	        if ("Invalid role ID!".equals(response)) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	        } 
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	@PostMapping("/upload")
    public ResponseEntity<ImportResult> uploadUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResult( "No new users uploaded.",null));
        }
       List<Users> users = excelUserService.importUsers(file);
       ImportResult result = new ImportResult("Users uploaded successfully", users);
        return ResponseEntity.ok(result);
    }

	@PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable UUID id, @RequestBody UserDetailsDto request) {
        String response = adminService.updateUser(id, request);
        if ("User Updated successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else if ("Username already exists (Constraint Violation)".equals(response)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else if ("User not found".equals(response)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

	@DeleteMapping("/delete/{id}")
    public ResponseEntity<String> getUserDetailsInRange(@PathVariable UUID id) {
    	adminService.deleteUser(id);
    	return ResponseEntity.ok("Deleted successfully"); 
    }
}
