package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.domain.ChatConversation;
import com.InSpace.Api.domain.ChatMessage;
import com.InSpace.Api.services.ChatService;
import com.InSpace.Api.services.dto.Chat.AddChatMessageRequest;
import com.InSpace.Api.services.dto.Chat.ChatConversationResponse;
import com.InSpace.Api.services.dto.Chat.CreateChatConversationRequest;
import com.InSpace.Api.services.dto.ErrorResponse;
import com.InSpace.Api.services.dto.SuccessResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversations/create")
    public ResponseEntity<?> createConversation(@RequestBody @Valid CreateChatConversationRequest request) {
        try {
            ChatConversation conversation = chatService.createConversation(request.getUserId(), request.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/messages/add")
    public ResponseEntity<?> addMessage(@RequestBody @Valid AddChatMessageRequest request) {
        try {
            ChatMessage message = chatService.addMessage(
                    request.getConversationId(),
                    request.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<?> getConversationById(@PathVariable Long id) {
        Optional<ChatConversationResponse> conversation = chatService.findConversationById(id);
        if (conversation.isPresent()) {
            return ResponseEntity.ok(conversation.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Conversation with ID " + id + " not found"));
        }
    }

    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<List<ChatConversationResponse>> getConversationsByUserId(@PathVariable Long userId) {
        List<ChatConversationResponse> conversations = chatService.findConversationsByUserId(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/all")
    public ResponseEntity<List<ChatConversationResponse>> getAllConversations() {
        List<ChatConversationResponse> conversations = chatService.findAllConversations();
        return ResponseEntity.ok(conversations);
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<?> deleteConversation(@PathVariable Long id) {
        try {
            chatService.deleteConversation(id);
            return ResponseEntity.ok(new SuccessResponse("Conversation deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
