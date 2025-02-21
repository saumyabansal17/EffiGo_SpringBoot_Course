package com.effigo.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.effigo.ems.dto.MessageRequest;
import com.effigo.ems.dto.MessageResponse;
import com.effigo.ems.dto.MessageUsers;
import com.effigo.ems.model.Message;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.service.MessageService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
	private UsersRepository usersRepository;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(request.getSenderId(), request.getReceiverId(), request.getContent()));
    }

    @GetMapping("/received/{receiverId}")
    public ResponseEntity<List<MessageResponse>> getReceivedMessages(@PathVariable UUID receiverId) {
    	List<MessageResponse> response = messageService.getMessages(receiverId);

        if (response == null) {
            return ResponseEntity.ok(Collections.emptyList()); 
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<MessageUsers>> getAll(){
    	List<MessageUsers> response=usersRepository.findUsers();
    	if (response == null) {
            return ResponseEntity.ok(Collections.emptyList()); 
        }
    	return ResponseEntity.ok(response);
    }
}
	