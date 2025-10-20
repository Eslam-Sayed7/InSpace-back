package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chat.chatId = :chatId ORDER BY m.sentAt ASC")
    List<ChatMessage> findMessagesByChatId(@Param("chatId") Long chatId);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chat.chatId = :chatId")
    int countMessagesByChatId(@Param("chatId") Long chatId);
}
