package com.InSpace.Api.services;

import java.util.List;

import com.InSpace.Api.domain.ChatMessage;
import com.InSpace.Api.services.dto.Chat.ChatMessageResponse;
import com.InSpace.Api.services.dto.Chat.ChatResponse;
import com.InSpace.Api.services.dto.Chat.CreateChatRequest;
import com.InSpace.Api.services.dto.Chat.SendMessageRequest;

public interface ChatService {
    
    ChatResponse createChat(CreateChatRequest request, Long currentUserId);

    ChatMessageResponse sendMessage(SendMessageRequest request, Long senderId);

    List<ChatResponse> getUserChats(Long userId);

    ChatResponse getChatById(Long chatId, Long userId);

    List<ChatMessage> getChatMessages(Long chatId, Long userId);

    void addParticipants(Long chatId, List<Long> participantIds, Long requesterId);

    void removeParticipant(Long chatId, Long userId, Long requesterId);

    void deleteChat(Long chatId, Long requesterId);

}
