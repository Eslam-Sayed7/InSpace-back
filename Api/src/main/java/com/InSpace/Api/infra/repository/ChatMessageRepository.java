package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByConversation_ConversationIdOrderBySequenceOrderAsc(Long conversationId);
}
