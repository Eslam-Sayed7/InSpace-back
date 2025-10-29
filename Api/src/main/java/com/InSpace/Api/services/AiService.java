package com.InSpace.Api.services;

import org.springframework.ai.chat.model.ChatResponse;

import reactor.core.publisher.Flux;

import org.springframework.ai.openai.api.OpenAiApi.ChatModel;

public interface AiService {

    ChatResponse chat(String message);

    ChatResponse chatWithSchemaKey(String message, String schemaKey);

    ChatResponse chatWithSchemaKey(String message, String schemaKey, ChatModel model);

    Flux<ChatResponse> chatStream(String message);

    Flux<ChatResponse> chatStreamWithSchemaKey(String message, String schemaKey);

    Flux<ChatResponse> chatStreamWithSchemaKey(String message, String schemaKey, ChatModel model);
}
