package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.userId = :userId ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Chat c JOIN c.participants p " +
           "WHERE c.chatId = :chatId AND p.userId = :userId")
    boolean isUserParticipant(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
