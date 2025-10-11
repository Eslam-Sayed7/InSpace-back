package com.InSpace.Api.services.dto.Chat;

import jakarta.validation.constraints.NotBlank;

public class CreateChatConversationRequest {

    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    public CreateChatConversationRequest() {
    }

    public CreateChatConversationRequest(Long userId, String title) {
        this.userId = userId;
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
