package com.effigo.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.effigo.ems.dto.MessageResponse;
import com.effigo.ems.model.Message;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByReceiverId(UUID receiverId);
    List<Message> findBySenderId(UUID senderId);
    
    @Query(value="select ud.name,m.sender_id,m.content from users ud JOIN message m ON ud.id=m.sender_id where m.receiver_id=:id",nativeQuery = true)
    List<MessageResponse> getResponse(UUID id);
}
