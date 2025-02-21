package com.effigo.task2_security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.effigo.task2_security.model.UserDetails;
import com.effigo.task2_security.model.Users;

public interface UserDetailRepository extends JpaRepository<UserDetails, Integer>{
	Optional<UserDetails> findByUsername(String username);
}
