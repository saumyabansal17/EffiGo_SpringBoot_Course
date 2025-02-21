package com.effigo.ems.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.effigo.ems.dto.UpdateProfileDto;
import com.effigo.ems.dto.ViewProfileDto;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UsersRepository usersRepository;

	@GetMapping("/view")
    public ResponseEntity<ViewProfileDto> getDetails(@CookieValue(name = "token", required = false) String token) {
        return userService.viewDetails(token);
    }
	
	@PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable UUID id, @RequestBody UpdateProfileDto request) {
        String response = userService.updateUser(id, request); 

        if ("User updated successfully".equals(response)) {
            return ResponseEntity.ok(response);
        } else if ("Username already exists (Constraint Violation)".equals(response)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else if ("User not found".equals(response)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
	
	@GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@CookieValue(name = "token", required = false) String token) {     
    	String name = userService.getName(token);
    	Map<String, Object> userData = new HashMap<>();
    	System.out.println(name);
    	UUID id=userService.getId(token);
    	userData.put("name", name);
    	userData.put("id", id);
        return ResponseEntity.ok(userData);
    }
	
}
