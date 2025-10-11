package com.InSpace.Api.services;

import com.InSpace.Api.domain.ChatConversation;
import com.InSpace.Api.domain.ChatMessage;
import com.InSpace.Api.infra.repository.ChatConversationRepository;
import com.InSpace.Api.infra.repository.ChatMessageRepository;
import com.InSpace.Api.services.dto.Chat.ChatConversationResponse;
import com.InSpace.Api.services.dto.Chat.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    private final ChatConversationRepository chatConversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatService(ChatConversationRepository chatConversationRepository,
            ChatMessageRepository chatMessageRepository) {
        this.chatConversationRepository = chatConversationRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatConversation createConversation(Long userId, String title) {
        ChatConversation conversation = new ChatConversation(userId, title);
        return chatConversationRepository.save(conversation);
    }

    public ChatMessage addMessage(Long conversationId, String content) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation with ID " + conversationId + " not found"));

        // Get the next sequence order
        int nextSequenceOrder = conversation.getMessages().size();

        ChatMessage message = new ChatMessage(conversation, content, nextSequenceOrder);
        ChatMessage savedMessage = chatMessageRepository.save(message);

        conversation.addMessage(savedMessage);
        chatConversationRepository.save(conversation);

        return savedMessage;
    }

    @Transactional(readOnly = true)
    public Optional<ChatConversationResponse> findConversationById(Long conversationId) {
        Optional<ChatConversation> conversationOpt = chatConversationRepository.findById(conversationId);
        return conversationOpt.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ChatConversationResponse> findConversationsByUserId(Long userId) {
        List<ChatConversation> conversations = chatConversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return conversations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatConversationResponse> findAllConversations() {
        List<ChatConversation> conversations = chatConversationRepository.findAll();
        return conversations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteConversation(Long conversationId) {
        if (!chatConversationRepository.existsById(conversationId)) {
            throw new IllegalArgumentException("Conversation with ID " + conversationId + " not found");
        }
        chatConversationRepository.deleteById(conversationId);
    }

    private ChatConversationResponse toResponse(ChatConversation conversation) {
        List<ChatMessageDTO> messageDTOs = conversation.getMessages().stream()
                .map(msg -> new ChatMessageDTO(
                msg.getMessageId(),
                msg.getContent(),
                msg.getSequenceOrder(),
                msg.getCreatedAt()))
                .collect(Collectors.toList());

        return new ChatConversationResponse(
                conversation.getConversationId(),
                conversation.getUserId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messageDTOs);
    }
}
