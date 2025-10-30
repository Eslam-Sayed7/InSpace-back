package com.InSpace.Api.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.InSpace.Api.domain.AiChatResponse;
import com.InSpace.Api.domain.Chat;
import com.InSpace.Api.domain.ChatMessage;
import com.InSpace.Api.domain.User;
import com.InSpace.Api.infra.repository.ChatMessageRepository;
import com.InSpace.Api.infra.repository.ChatRepository;
import com.InSpace.Api.infra.repository.AiResponseRepository;
import com.InSpace.Api.infra.repository.PromptRepository;
import com.InSpace.Api.infra.repository.UserRepository;
import com.InSpace.Api.services.AiService;
import com.InSpace.Api.services.ChatService;
import com.InSpace.Api.services.agent.ChatAgent;
import com.InSpace.Api.services.dto.ResourceNotFoundException;
import com.InSpace.Api.services.dto.Chat.ChatMessageResponse;
import com.InSpace.Api.services.dto.Chat.ChatParticipantResponse;
import com.InSpace.Api.services.dto.Chat.ChatResponse;
import com.InSpace.Api.services.dto.Chat.CreateChatRequest;
import com.InSpace.Api.services.dto.Chat.SendMessageRequest;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatAgent chatAgent;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository,
            PromptRepository promptRepository,
            AiResponseRepository aiResponseRepository,
            AiService aiService,
            com.InSpace.Api.services.agent.ChatAgent chatAgent) {
        this.chatRepository = chatRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatAgent = chatAgent;
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

        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        if (!chatRepository.isUserParticipant(request.getChatId(), senderId)) {
            throw new IllegalArgumentException("User is not a participant of this chat");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatMessage message = ChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .isModelMessage(false)
                .build();

        message = chatMessageRepository.save(message);

        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        // Delegate to agent 
        chatAgent.processUserMessage(message);
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

        if (!chatRepository.isUserParticipant(chatId, userId)) {
            throw new IllegalArgumentException("Access denied: User is not a participant of this chat");
        }

        return mapToChatResponse(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getChatMessages(Long chatId, Long userId) {

        if (!chatRepository.isUserParticipant(chatId, userId)) {
            throw new IllegalArgumentException("Access denied: User is not a participant of this chat");
        }

        // ordered ASC by sentAt
        var messages = chatMessageRepository.findMessagesByChatId(chatId);
        return messages;
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

    private boolean isLikelyPrompt(String content) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("/prompt")) {
            return true;
        }
        if (trimmed.startsWith("{") && trimmed.contains("\"schema\"")) {
            return true;
        }
        if (trimmed.toLowerCase().contains("schema:")) {
            return true;
        }
        return false;
    }

    private String extractSchemaUrl(String content) {
        if (content == null) {
            return null;
        }
        // look for 'schema:' token
        int idx = content.toLowerCase().indexOf("schema:");
        if (idx >= 0) {
            String after = content.substring(idx + "schema:".length()).trim();
            // take first whitespace-delimited token
            String[] parts = after.split("\\s+", 2);
            if (parts.length > 0 && parts[0].startsWith("http")) {
                return parts[0];
            }
        }

        // fallback: find any http(s) url
        Pattern urlPattern = Pattern.compile("https?://\\S+");
        Matcher m = urlPattern.matcher(content);
        if (m.find()) {
            return m.group();
        }

        return null;
    }

}
