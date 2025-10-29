package com.InSpace.Api.services.impl;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import com.InSpace.Api.config.AiProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.InSpace.Api.services.AiService;

@Service
public class AiServiceimpl implements AiService {

    private final OpenAiChatModel chatModel;
    private final AiProperties aiProperties;

    @Autowired
    public  AiServiceimpl(OpenAiChatModel chatModel, AiProperties aiProperties) {
        this.chatModel = chatModel;
        this.aiProperties = aiProperties;
    }

    public String chat(String message) {
        return chatModel.call(message);
    }

    @Override
    public ChatResponse chatWithSchemaKey(String message, String schemaKey) {
        return chatWithSchemaKey(message, schemaKey, ChatModel.GPT_4_O_MINI);
    }

    @Override
    public ChatResponse chatWithSchemaKey(String message, String schemaKey, ChatModel model) {
        String jsonSchema = loadSchemaByKey(schemaKey);

        Prompt prompt = new Prompt(message,
            OpenAiChatOptions.builder()
                .model(model)
                .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                .build());

        return chatModel.call(prompt);
    }

    @Override
    public Flux<ChatResponse> chatStream(String message) {
        Prompt prompt = new Prompt(message);
        return chatModel.stream(prompt);
    }

    @Override
    public Flux<ChatResponse> chatStreamWithSchemaKey(String message, String schemaKey) {
        return chatStreamWithSchemaKey(message, schemaKey, ChatModel.GPT_4_O_MINI);
    }

    @Override
    public Flux<ChatResponse> chatStreamWithSchemaKey(String message, String schemaKey, ChatModel model) {
        String jsonSchema = loadSchemaByKey(schemaKey);

        Prompt prompt = new Prompt(message,
            OpenAiChatOptions.builder()
                .model(model)
                .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                .build());

        return chatModel.stream(prompt);
    }

    private String loadSchemaByKey(String schemaKey) {
        if (schemaKey == null) {
            throw new IllegalArgumentException("schemaKey cannot be null");
        }

        Map<String, String> mapping = aiProperties.getSchemas();
        String fileName = null;
        if (mapping != null) {
            fileName = mapping.get(schemaKey);
        }

        if (fileName == null) {
            throw new IllegalArgumentException("cannot be resolved to a schema file name: " + schemaKey);
        }

        String resourcePath = "schemas/" + fileName + ".json";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found on classpath: " + resourcePath);
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema resource: " + resourcePath, e);
        }
    }
}
