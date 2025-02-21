package com.effigo.ems.controller;

import com.effigo.ems.dto.LoggingHistoryResponse;
import com.effigo.ems.model.LoggingHistory;
import com.effigo.ems.repository.LoginHistoryRepository;
import com.effigo.ems.service.LoggingHistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class LoggingHistoryController {

    @Autowired
    private LoggingHistoryService loggingHistoryService;
    
    @Autowired
	private LoginHistoryRepository loginHistoryRepository;

//    @GetMapping("/logs")
//    public ResponseEntity<List<LoggingHistoryResponse>> getLogs() {
//        List<LoggingHistoryResponse> logs = loggingHistoryService.getUserLogs();
//        return ResponseEntity.ok(logs);
//    }
    
    @GetMapping("/logs")
    public Page<Object[]> getLogs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return loginHistoryRepository.findDetails(pageable);
    }

    }


