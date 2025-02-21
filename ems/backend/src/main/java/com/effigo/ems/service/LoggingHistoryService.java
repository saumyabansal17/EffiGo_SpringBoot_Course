	package com.effigo.ems.service;
	
	import com.effigo.ems.dto.LoggingHistoryResponse;
	import com.effigo.ems.model.LoggingHistory;
	import com.effigo.ems.model.Users;
	import com.effigo.ems.repository.LoginHistoryRepository;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.data.domain.Page;
	import org.springframework.data.domain.PageRequest;
	import org.springframework.data.domain.Pageable;
	import org.springframework.stereotype.Service;
	
	import java.sql.Timestamp;
	import java.time.LocalDateTime;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.UUID;
	
	@Service
	public class LoggingHistoryService {
	
	    @Autowired
	    private LoginHistoryRepository loggingHistoryRepository;
	
	    public void saveLoginHistory(Users user) {
	        LoggingHistory log = new LoggingHistory();
	        log.setUser(user);
	        log.setLoginTime(LocalDateTime.now());
	        loggingHistoryRepository.save(log);
	    }
	    
	    public List<LoggingHistoryResponse> getUserLogs() {
	        Pageable pageable = PageRequest.of(0, 5); // page index 0, 5 records per page
	        Page<Object[]> logs = loggingHistoryRepository.findDetails(pageable);
	        List<LoggingHistoryResponse> response = new ArrayList<>();
	        
	        for (Object[] log : logs) {
	            String name = (String) log[0];
	            LocalDateTime loginTime = ((Timestamp) log[1]).toLocalDateTime();
	            UUID userId = (UUID) log[2];
	            
	            LoggingHistoryResponse logResponse = new LoggingHistoryResponse(name, loginTime, userId);
	            response.add(logResponse);
	        }
	        
	        return response;
	    }
	
	
	}
