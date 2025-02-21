package com.effigo.ems.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.effigo.ems.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

	@Query(value = "SELECT r.role_id FROM role r WHERE r.role = :role", nativeQuery = true)
	int findIdByRole(String role);
	
	@Query(value = "SELECT r.status FROM role r WHERE r.role_id = :id", nativeQuery = true)
	String findStatusById(int id);

	Role findByRole(String role);

}
