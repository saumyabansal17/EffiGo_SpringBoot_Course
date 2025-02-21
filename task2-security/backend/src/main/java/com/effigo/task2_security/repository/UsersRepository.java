package com.effigo.task2_security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.effigo.task2_security.model.Users;

public interface UsersRepository extends JpaRepository<Users, Integer>{
	Optional<Users> findByUsername(String username);
}
