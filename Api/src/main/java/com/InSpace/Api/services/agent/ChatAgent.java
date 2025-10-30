package com.InSpace.Api.services.agent;

import com.InSpace.Api.domain.AiChatResponse;
import com.InSpace.Api.domain.Chat;
import com.InSpace.Api.domain.ChatMessage;
import com.InSpace.Api.domain.Prompt;
import com.InSpace.Api.infra.repository.AiResponseRepository;
import com.InSpace.Api.infra.repository.ChatMessageRepository;
import com.InSpace.Api.infra.repository.ChatRepository;
import com.InSpace.Api.infra.repository.PromptRepository;
import com.InSpace.Api.services.AiService;
import com.InSpace.Api.services.util.AIResponseExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ChatAgent {

    private final AiService aiService;
    private final PromptRepository promptRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiResponseRepository aiResponseRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public ChatAgent(AiService aiService,
                     PromptRepository promptRepository,
                     ChatMessageRepository chatMessageRepository,
                     AiResponseRepository aiResponseRepository,
                     ChatRepository chatRepository) {
        this.aiService = aiService;
        this.promptRepository = promptRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.aiResponseRepository = aiResponseRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public ChatMessage processUserMessage(ChatMessage userMessage) {
        // decide if prompt-like or normal message using simple heuristics
        if (isLikelyPrompt(userMessage.getContent())) {
            return handlePrompt(userMessage);
        } else {
            return handleNormalMessage(userMessage);
        }
    }

    private boolean isLikelyPrompt(String content) {
        if (content == null) return false;
        String t = content.trim();
        if (t.startsWith("/prompt")) return true;
        if (t.startsWith("{") && t.contains("\"schema\"")) return true;
        if (t.toLowerCase().contains("schema:")) return true;
        return false;
    }


    @Transactional
    public ChatMessage handleNormalMessage(ChatMessage userMessage) {
        String content = userMessage.getContent();
        org.springframework.ai.chat.model.ChatResponse modelResp = aiService.chat(content);

        String raw = modelResp == null ? null : modelResp.toString();
        String reply = AIResponseExtractor.extractTextContent(raw);
        if (reply == null) reply = raw == null ? "" : raw;

        ChatMessage aiMessage = ChatMessage.builder()
                .chat(userMessage.getChat())
                .sender(null)
                .content(reply)
                .isModelMessage(true)
                .build();

        aiMessage = chatMessageRepository.save(aiMessage);

        // update chat timestamp
        Chat chat = userMessage.getChat();
        chat.setUpdatedAt(java.time.LocalDateTime.now());
        chatRepository.save(chat);

        return aiMessage;
    }


    @Transactional
    public ChatMessage handlePrompt(ChatMessage userMessage) {
        // create and save prompt linked to this chat message
        Prompt prompt = Prompt.builder()
                .content(userMessage.getContent())
                .chatMessage(userMessage)
                .parsedElementsJsonUrl(null)
                .build();

        prompt = promptRepository.save(prompt);

        // TODO AiServiceimpl has schema-aware methods if needed
    org.springframework.ai.chat.model.ChatResponse modelResp = aiService.chat(prompt.getContent());

    String rawJson = modelResp == null ? null : modelResp.toString();
    String reply = AIResponseExtractor.extractTextContent(rawJson);
    if (reply == null) reply = rawJson == null ? "" : rawJson;

        // persist AI chat message
        ChatMessage aiMessage = ChatMessage.builder()
                .chat(userMessage.getChat())
                .sender(null)
                .content(reply)
                .isModelMessage(true)
                .build();
        aiMessage = chatMessageRepository.save(aiMessage);

        AiChatResponse aiResp = AiChatResponse.builder()
                .prompt(prompt)
                .chatMessage(aiMessage)
                .model(null)
                .rawJson(rawJson)
                .build();

        aiResponseRepository.save(aiResp);

        // update chat timestamp
        Chat chat = userMessage.getChat();
        chat.setUpdatedAt(java.time.LocalDateTime.now());
        chatRepository.save(chat);

        return aiMessage;
    }

    @Transactional
    public ChatMessage processPrompt(Long promptId) {
        Prompt prompt = promptRepository.findById(promptId).orElseThrow();
        ChatMessage userMessage = prompt.getChatMessage();
        return handlePrompt(userMessage);
    }
}
