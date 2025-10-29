package com.InSpace.Api.adapters.controller;

import com.InSpace.Api.services.AiService;
import com.InSpace.Api.services.dto.ai.GenerateRequest;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;

import com.InSpace.Api.services.dto.ai.GenerateResponse;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;
    private static final ChatModel DEFAULT_MODEL = ChatModel.GPT_5_MINI;

    @Autowired
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generate(
        @RequestBody(required = false) GenerateRequest request) {

        if (request == null || request.getMessage() == null || request.getMessage().isEmpty()) {
            return buildErrorResponse("Invalid request: prompt is required", HttpStatus.BAD_REQUEST);
        }

        var aiResult = aiService.chat(request.getMessage());
        if (aiResult == null) {
            return buildErrorResponse("AI service returned no result", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        var success = GenerateResponse.builder().generation(aiResult).build();
        return ResponseEntity.ok(success);
    }

    @PostMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestBody(required = false) GenerateRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request: prompt is required"));
        }

        String message = request.getMessage();
        return aiService.chatStream(message);
    }

    
    @PostMapping("/generate-schema")
    public ChatResponse generateWithSchema(@RequestBody GenerateRequest request) {
        SchemaParams p = parseSchemaRequest(request);
        return aiService.chatWithSchemaKey(p.message, p.schemaKey, p.model);
    }

    @PostMapping("/generateStream-schema")
    public Flux<ChatResponse> generateStreamWithSchema(@RequestBody GenerateRequest request) {
        SchemaParams p = parseSchemaRequest(request);
        return aiService.chatStreamWithSchemaKey(p.message, p.schemaKey, p.model);
    }

    private ResponseEntity<GenerateResponse> buildErrorResponse(String message, HttpStatus status) {
        var resp = GenerateResponse.builder().error(message).build();
        return ResponseEntity.status(status).body(resp);
    }


    private SchemaParams parseSchemaRequest(GenerateRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }
        if (request.getSchemaKey() == null || request.getSchemaKey().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schemaKey is required");
        }
        String message = request.getMessage() == null ? "" : request.getMessage();
        ChatModel model = parseModelOrDefault(request.getModel());
        return new SchemaParams(message, request.getSchemaKey(), model);
    }

    private ChatModel parseModelOrDefault(String modelStr) {
        if (modelStr == null || modelStr.isEmpty()) return DEFAULT_MODEL;
        try {
            return ChatModel.valueOf(modelStr);
        } catch (Exception e) {
            return DEFAULT_MODEL;
        }
    }

    private static class SchemaParams {
        final String message;
        final String schemaKey;
        final ChatModel model;

        SchemaParams(String message, String schemaKey, ChatModel model) {
            this.message = message;
            this.schemaKey = schemaKey;
            this.model = model;
        }
    }

}