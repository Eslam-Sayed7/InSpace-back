package com.InSpace.Api.services.dto.Chat;

import java.time.LocalDateTime;

public class ChatMessageDTO {

    private Long messageId;
    private String content;
    private String role;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(Long messageId, String content, String role, Integer sequenceOrder, LocalDateTime createdAt) {
        this.messageId = messageId;
        this.content = content;
        this.role = role;
        this.sequenceOrder = sequenceOrder;
        this.createdAt = createdAt;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
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

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
