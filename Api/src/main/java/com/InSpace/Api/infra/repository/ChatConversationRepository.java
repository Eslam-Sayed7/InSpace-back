package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    
    List<ChatConversation> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
