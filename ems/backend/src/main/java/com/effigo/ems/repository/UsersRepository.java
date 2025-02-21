package com.effigo.ems.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.effigo.ems.dto.MessageUsers;
import com.effigo.ems.dto.UserDetailsDto;
import com.effigo.ems.dto.ViewProfileDto;
import com.effigo.ems.enums.UserStatus;
import com.effigo.ems.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID>{
	Users findByEmailId(String email_id);
	
	Optional<Users> findById(UUID id);
	
	List<Users> findByStatus(UserStatus status);
	
	boolean existsByEmailId(String emailId);

	@Query(value = "SELECT ud.id,ud.name FROM users ud", nativeQuery = true)
	List<MessageUsers> findUsers();
	
	@Query(value = "SELECT ud.id FROM users ud WHERE ud.name = :name", nativeQuery = true)
	UUID findUIdByName(String name);
	
	@Query(value = "SELECT ud.email_id,ud.phone_no,ud.id,r.role,ud.status FROM users ud JOIN role r ON  ud.role_id=r.role_id WHERE ud.email_id <> :email and r.role <> 'SUPER_ADMIN' ", nativeQuery = true)
	List<UserDetailsDto> findDetailsByEmail(String email);
	
	@Query(value = "SELECT ud.email_id,ud.phone_no,ud.id,r.role,ud.status FROM users ud JOIN role r ON  ud.role_id=r.role_id WHERE ud.email_id <> :email and r.role = 'USER'", nativeQuery = true)
	List<UserDetailsDto> findUDetailsByEmail(String email);

	@Query(value = "SELECT ud.name FROM users ud WHERE ud.email_id = :email", nativeQuery = true)
	String findNameByEmail(String email);
	
	@Query(value = "SELECT ud.role_id FROM users ud WHERE ud.email_id = :email", nativeQuery = true)
	int findIdByEmail(String email);
	
	@Query(value = "SELECT ud.id FROM users ud WHERE ud.email_id = :email", nativeQuery = true)
	UUID findUIdByEmail(String email);
	
	@Query(value = "SELECT ud.email_id FROM users ud WHERE ud.id = :id", nativeQuery = true)
	String findEmailByUID(UUID id);

	@Query(value = "SELECT ud.email_id FROM users ud WHERE ud.role_id = 0", nativeQuery = true)
	String findSuperAdminEmail();  
	
	@Query(value = "SELECT ud.email_id,ud.phone_no,r.role,ud.id FROM users ud JOIN role r ON  ud.role_id=r.role_id WHERE ud.email_id = :email", nativeQuery = true)
	ViewProfileDto viewDetailsByEmail(String email);

}
