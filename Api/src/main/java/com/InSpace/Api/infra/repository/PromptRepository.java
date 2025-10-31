package com.InSpace.Api.infra.repository;

import com.InSpace.Api.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {

    Optional<Prompt> findByChatMessageMessageId(Long messageId);
}
