package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.AiChatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiResponseRepository extends JpaRepository<AiChatResponse, Long> {

    List<AiChatResponse> findByPromptPromptId(Long promptId);

    Optional<AiChatResponse> findByChatMessageMessageId(Long messageId);
}
