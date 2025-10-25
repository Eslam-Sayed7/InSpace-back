package com.InSpace.Api.services;

import com.InSpace.Api.services.dto.Chat.*;

import java.util.List;

public interface ChatService {
    /**
     * Create a new group chat session with AI model.
     * All participants (team members) can access and collaborate in this chat.
     */
    ChatResponse createChat(CreateChatRequest request, Long currentUserId);

    /**
     * Send a user message to the chat (will be stored in order).
     * This triggers an AI model response.
     */
    ChatMessageResponse sendMessage(SendMessageRequest request, Long senderId);

    /**
     * Append an AI/model message to the chat (for ChatGPT-like responses).
     * This is called internally after user sends a message.
     */
    ChatMessageResponse appendModelMessage(Long chatId, String content);

    /**
     * List all chats the user is a participant in (team chats).
     */
    List<ChatResponse> getUserChats(Long userId);

    /**
     * Get chat details if the user is a participant.
     */
    ChatResponse getChatById(Long chatId, Long userId);

    /**
     * Get all messages in a chat, ordered by sent time (conversation history).
     */
    List<ChatMessageResponse> getChatMessages(Long chatId, Long userId);

    /**
     * Add team members to a chat session.
     */
    void addParticipants(Long chatId, List<Long> participantIds, Long requesterId);

    /**
     * Remove a participant from a chat session.
     */
    void removeParticipant(Long chatId, Long userId, Long requesterId);

    /**
     * Delete a chat session (only if user is a participant).
     */
    void deleteChat(Long chatId, Long requesterId);
}
