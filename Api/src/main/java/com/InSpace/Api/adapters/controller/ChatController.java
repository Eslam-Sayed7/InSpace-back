package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.services.ChatService;
import com.InSpace.Api.services.dto.Chat.*;
import com.InSpace.Api.services.dto.SuccessResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.InSpace.Api.services.UserService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserIdFromUsername(username);
    }

    /**
     * Create a new chat session (group chat with AI model).
     */
    @PostMapping("/create")
    public ResponseEntity<ChatResponse> createChat(@RequestBody CreateChatRequest request) {
        try {
            Long currentUserId = getCurrentUserId();
            ChatResponse response = chatService.createChat(request, currentUserId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send a message to the chat (user message).
     */
    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            Long currentUserId = getCurrentUserId();
            ChatMessageResponse response = chatService.sendMessage(request, currentUserId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Append an AI model response to the chat.
     * This endpoint can be called after getting a response from your AI model.
     */
    // @PostMapping("/{chatId}/model-response")
    // public ResponseEntity<ChatMessageResponse> appendModelMessage(
    //         @PathVariable Long chatId,
    //         @RequestBody String content) {
    //     try {
    //         Long currentUserId = getCurrentUserId();
    //         // Verify user has access to this chat
    //         chatService.getChatById(chatId, currentUserId);
    //         ChatMessageResponse response = chatService.appendModelMessage(chatId, content);
    //         return new ResponseEntity<>(response, HttpStatus.CREATED);
    //     } catch (IllegalArgumentException e) {
    //         return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }

    /**
     * Get all chats the user is a participant in.
     */
    @GetMapping("/list")
    public ResponseEntity<List<ChatResponse>> getUserChats() {
        try {
            Long currentUserId = getCurrentUserId();
            List<ChatResponse> chats = chatService.getUserChats(currentUserId);
            return new ResponseEntity<>(chats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get chat details by ID (only if user is a participant).
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(@PathVariable Long chatId) {
        try {
            Long currentUserId = getCurrentUserId();
            ChatResponse response = chatService.getChatById(chatId, currentUserId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all messages in a chat (conversation history), ordered by time.
     */
    // @GetMapping("/{chatId}/messages")
    // public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@PathVariable Long chatId) {
    //     try {
    //         Long currentUserId = getCurrentUserId();
    //         var messages = chatService.getChatMessages(chatId, currentUserId);

    //         List<ChatMessageResponse> response = messages.stream()
    //                 .map(message -> new ChatMessageResponse(message.getMessageId(),
    //                 message.getContent(), message.getSender().getUserId(),
    //                 message.getSentAt(), message.isModelMessage()))
    //                 .collect(Collectors.toList());

    //         return new ResponseEntity<>(response, HttpStatus.OK);
    //     } catch (IllegalArgumentException e) {
    //         return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    //     }
    // }

    /**
     * Add team members to a chat.
     */
    @PostMapping("/{chatId}/participants")
    public ResponseEntity<SuccessResponse> addParticipants(
            @PathVariable Long chatId,
            @RequestBody List<Long> participantIds) {
        try {
            Long currentUserId = getCurrentUserId();
            chatService.addParticipants(chatId, participantIds, currentUserId);
            SuccessResponse response = new SuccessResponse("Participants added successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove a participant from a chat.
     */
    @DeleteMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<SuccessResponse> removeParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            chatService.removeParticipant(chatId, userId, currentUserId);
            SuccessResponse response = new SuccessResponse("Participant removed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a chat session.
     */
    @DeleteMapping("/{chatId}")
    public ResponseEntity<SuccessResponse> deleteChat(@PathVariable Long chatId) {
        try {
            Long currentUserId = getCurrentUserId();
            chatService.deleteChat(chatId, currentUserId);
            SuccessResponse response = new SuccessResponse("Chat deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
