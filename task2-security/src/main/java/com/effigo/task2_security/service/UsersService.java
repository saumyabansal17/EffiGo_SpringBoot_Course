package com.effigo.task2_security.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.effigo.task2_security.dto.SignupRequest;
import com.effigo.task2_security.model.Users;
import com.effigo.task2_security.repository.UsersRepository;


@Service
public class UsersService {

	private UsersRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	
	public UsersService(UsersRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}


	public void register(SignupRequest request) {
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
    }
	
	
}
