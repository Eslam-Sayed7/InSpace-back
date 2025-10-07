package com.InSpace.Api.services.dto.Chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddChatMessageRequest {

    @NotNull(message = "Conversation ID is required")
    private Long conversationId;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Role is required")
    private String role; // "user" or "assistant"

    public AddChatMessageRequest() {
    }

    public AddChatMessageRequest(Long conversationId, String content, String role) {
        this.conversationId = conversationId;
        this.content = content;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
