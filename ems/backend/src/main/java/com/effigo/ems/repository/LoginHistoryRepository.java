package com.effigo.ems.repository;

import com.effigo.ems.model.LoggingHistory;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoggingHistory, UUID> {
    List<LoggingHistory> findByUserId(UUID userId);
    
//    @Query(value="select ud.name,l.login_time,l.user_id from users ud JOIN logging_history l on l.user_id=ud.id where ud.role_id<>0",nativeQuery = true)
//    List<Object[]> findDetails();
    
    @Query(
    	    value = "SELECT ud.name, l.login_time, l.user_id FROM users ud JOIN logging_history l ON l.user_id=ud.id WHERE ud.role_id <> 0",
    	    countQuery = "SELECT count(*) FROM users ud JOIN logging_history l ON l.user_id=ud.id WHERE ud.role_id <> 0",
    	    nativeQuery = true
    	)
    	Page<Object[]> findDetails(Pageable pageable);


}
