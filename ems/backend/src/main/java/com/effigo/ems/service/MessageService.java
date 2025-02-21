package com.effigo.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.effigo.ems.dto.MessageResponse;
import com.effigo.ems.model.Message;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.MessageRepository;
import com.effigo.ems.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UsersRepository userRepository;

    public Message sendMessage(UUID senderId, UUID receiverId, String content) {
        log.info("Attempting to send message from sender: {} to receiver: {}", senderId, receiverId);

        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    log.error("Sender with ID {} not found", senderId);
                    return new RuntimeException("Sender not found");
                });

        log.info("Sender found: {}", sender.getEmailId());

        Users receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> {
                    log.error("Receiver with ID {} not found", receiverId);
                    return new RuntimeException("Receiver not found");
                });

        log.info("Receiver found: {}", receiver.getEmailId());

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
     
    }

    public List<MessageResponse> getMessages(UUID receiverId) {
        log.info("Fetching messages for receiver ID: {}", receiverId);
        List<MessageResponse> messages = messageRepository.getResponse(receiverId);
        log.info("Retrieved {} messages for receiver ID: {}", messages.size(), receiverId);
        return messages;
    }
}
