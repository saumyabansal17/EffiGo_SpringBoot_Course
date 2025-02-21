package com.effigo.task2_security.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.effigo.task2_security.dto.SignupRequest;
import com.effigo.task2_security.model.UserDetails;
import com.effigo.task2_security.model.Users;
import com.effigo.task2_security.repository.UserDetailRepository;
import com.effigo.task2_security.repository.UsersRepository;


@Service
public class UsersService {

	private UsersRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private UserDetailRepository userDetailRepository;
	
	
	public UsersService(UsersRepository userRepository, PasswordEncoder passwordEncoder,UserDetailRepository userDetailRepository) {
		super();
		this.userRepository = userRepository;
		this.userDetailRepository=userDetailRepository;
		this.passwordEncoder = passwordEncoder;
	}


	public void register(SignupRequest request) {
	    Users user = new Users();
	    user.setUsername(request.getUsername());
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    user.setRole("USER");
	    userRepository.save(user);
	    
	    UserDetails userDetails = new UserDetails();
	    userDetails.setUsername(request.getUsername());
	    userDetails.setEmail(request.getEmail());
	    userDetails.setName(request.getName());
	    userDetails.setUser(user);
	    user.setUserDetails(userDetails);

	    userDetailRepository.save(userDetails);
	}

	
//	public void saveDetails(SignupRequest request) {
//		UserDetails user = new UserDetails();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setName(request.getName());
//        userDetailRepository.save(user);
//	}
	
	
}
