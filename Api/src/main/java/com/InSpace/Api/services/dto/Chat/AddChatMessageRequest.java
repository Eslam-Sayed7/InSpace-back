package com.InSpace.Api.services.dto.Chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddChatMessageRequest {

    @NotNull(message = "Conversation ID is required")
    private Long conversationId;

    @NotBlank(message = "Content is required")
    private String content;

    public AddChatMessageRequest() {
    }

    public AddChatMessageRequest(Long conversationId, String content) {
        this.conversationId = conversationId;
        this.content = content;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
