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
import com.InSpace.Api.services.ai.AiModelResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Small agent responsible for deciding whether a user message is a prompt or a normal message,
 * calling the AiService accordingly and persisting the AI reply.
 */
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

    /**
     * Handle a normal message by sending it to the AiService for a short reply
     * and persisting the reply as a ChatMessage (isModelMessage=true).
     */
    @Transactional
    public ChatMessage handleNormalMessage(ChatMessage userMessage) {
        String content = userMessage.getContent();
        org.springframework.ai.chat.model.ChatResponse modelResp = aiService.chat(content);

        String reply = modelResp == null ? "" : modelResp.toString();

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

    /**
     * Handle a prompt: create Prompt entity (if not already linked), call AiService to process,
     * persist AiChatResponse and an AI ChatMessage.
     */
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

        String reply = modelResp == null ? "" : modelResp.toString();
        String rawJson = modelResp == null ? null : modelResp.toString();

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
