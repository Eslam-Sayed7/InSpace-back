package com.InSpace.Api.services.dto.mappers;

import com.InSpace.Api.domain.ChatMessage;
import com.InSpace.Api.services.dto.Chat.ChatMessageDTO;

public class ChatMessageMapper {
    public static ChatMessageDTO toDto(ChatMessage entity) {
        if (entity == null) return null;
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setMessageId(entity.getMessageId());
        dto.setContent(entity.getContent());
        dto.setSequenceOrder(entity.getSequenceOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}