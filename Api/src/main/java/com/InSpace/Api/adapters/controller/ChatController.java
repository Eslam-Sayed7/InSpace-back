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
