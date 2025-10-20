package com.InSpace.Api.services.impl;

import com.InSpace.Api.domain.Chat;
import com.InSpace.Api.domain.ChatMessage;
import com.InSpace.Api.domain.User;
import com.InSpace.Api.infra.repository.ChatMessageRepository;
import com.InSpace.Api.infra.repository.ChatRepository;
import com.InSpace.Api.infra.repository.UserRepository;
import com.InSpace.Api.services.ChatService;
import com.InSpace.Api.services.dto.Chat.*;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository,
                          ChatMessageRepository chatMessageRepository,
                          UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ChatResponse createChat(CreateChatRequest request, Long currentUserId) {
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("At least one participant is required");
        }

        // Validate that current user is included in participants
        if (!request.getParticipantIds().contains(currentUserId)) {
            request.getParticipantIds().add(currentUserId);
        }

        // Load participants (team members)
        List<User> participants = userRepository.findAllById(request.getParticipantIds());
        if (participants.size() != request.getParticipantIds().size()) {
            throw new ResourceNotFoundException("One or more participants not found");
        }

        // Create new group chat
        Chat chat = Chat.builder()
                .chatName(request.getChatName())
                .description(request.getDescription())
                .participants(participants)
                .messages(new ArrayList<>())
                .build();

        chat = chatRepository.save(chat);
        return mapToChatResponse(chat);
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(SendMessageRequest request, Long senderId) {
        // Validate chat exists
        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        // Validate sender is participant (team member)
        if (!chatRepository.isUserParticipant(request.getChatId(), senderId)) {
            throw new IllegalArgumentException("User is not a participant of this chat");
        }

        // Get sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create user message
        ChatMessage message = ChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .isModelMessage(false)
                .build();

        message = chatMessageRepository.save(message);

        // Update chat's updatedAt timestamp
        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        // TODO: Call AI model here to generate response
        // String aiResponse = callAIModel(chatId, content);
        // appendModelMessage(chatId, aiResponse);

        return mapToMessageResponse(message);
    }    /**
     * Append an AI/model message to the chat (for ChatGPT-like responses).
     */
    @Override
    @Transactional
    public ChatMessageResponse appendModelMessage(Long chatId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        // Create AI model message (no sender, or set a system user)
        ChatMessage message = ChatMessage.builder()
                .chat(chat)
                .sender(null) // AI message has no human sender
                .content(content)
                .isModelMessage(true)
                .build();

        message = chatMessageRepository.save(message);
        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);
        return mapToMessageResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponse> getUserChats(Long userId) {
        List<Chat> chats = chatRepository.findChatsByUserId(userId);
        return chats.stream()
                .map(this::mapToChatResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatResponse getChatById(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        // Only participants (team members) can access the chat
        if (!chatRepository.isUserParticipant(chatId, userId)) {
            throw new IllegalArgumentException("Access denied: User is not a participant of this chat");
        }

        return mapToChatResponse(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatMessages(Long chatId, Long userId) {
        // Validate user is participant
        if (!chatRepository.isUserParticipant(chatId, userId)) {
            throw new IllegalArgumentException("Access denied: User is not a participant of this chat");
        }

        // Return messages ordered by sentAt (conversation order)
        List<ChatMessage> messages = chatMessageRepository.findMessagesByChatId(chatId);
        return messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addParticipants(Long chatId, List<Long> participantIds, Long requesterId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        if (!chatRepository.isUserParticipant(chatId, requesterId)) {
            throw new IllegalArgumentException("Access denied: User is not a participant of this chat");
        }

        List<User> newParticipants = userRepository.findAllById(participantIds);
        if (newParticipants.size() != participantIds.size()) {
            throw new ResourceNotFoundException("One or more users not found");
        }

        // Add new team members to the chat
        for (User newParticipant : newParticipants) {
            if (!chat.getParticipants().contains(newParticipant)) {
                chat.getParticipants().add(newParticipant);
            }
        }
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void removeParticipant(Long chatId, Long userId, Long requesterId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        if (!chatRepository.isUserParticipant(chatId, requesterId)) {
            throw new IllegalArgumentException("Access denied: User is not a participant of this chat");
        }

        chat.getParticipants().removeIf(user -> user.getUserId().equals(userId));
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void deleteChat(Long chatId, Long requesterId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        if (!chatRepository.isUserParticipant(chatId, requesterId)) {
            throw new IllegalArgumentException("Access denied: User is not a participant of this chat");
        }

        chatRepository.delete(chat);
    }

    private ChatResponse mapToChatResponse(Chat chat) {
        List<ChatParticipantResponse> participants = chat.getParticipants().stream()
                .map(this::mapToParticipantResponse)
                .collect(Collectors.toList());

        // Get last message
        ChatMessageResponse lastMessage = null;
        if (!chat.getMessages().isEmpty()) {
            ChatMessage lastMsg = chat.getMessages().get(chat.getMessages().size() - 1);
            lastMessage = mapToMessageResponse(lastMsg);
        }

        // Get message count
        int messageCount = chatMessageRepository.countMessagesByChatId(chat.getChatId());

        return ChatResponse.builder()
                .chatId(chat.getChatId())
                .chatName(chat.getChatName())
                .description(chat.getDescription())
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .participants(participants)
                .lastMessage(lastMessage)
                .messageCount(messageCount)
                .build();
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChat().getChatId())
                .senderId(message.getSender() != null ? message.getSender().getUserId() : null)
                .senderUsername(message.getSender() != null ? message.getSender().getUsername() : "AI Model")
                .content(message.getContent())
                .isModelMessage(message.isModelMessage())
                .sentAt(message.getSentAt())
                .build();
    }

    private ChatParticipantResponse mapToParticipantResponse(User user) {
        return ChatParticipantResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
