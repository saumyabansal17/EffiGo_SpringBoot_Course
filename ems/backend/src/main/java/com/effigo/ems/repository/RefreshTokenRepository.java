package com.effigo.ems.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.effigo.ems.model.RefreshToken;
import com.effigo.ems.model.Users;

import jakarta.transaction.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    @Transactional
    @Query(value = "delete from refresh_token where user_id=:id",nativeQuery = true)
    void deleteByUser(UUID id);
    
	RefreshToken findByUser(Users user);
	
	@Query(value = "delete from refresh_token where token=:token",nativeQuery = true)
	void delete(String token);
}
