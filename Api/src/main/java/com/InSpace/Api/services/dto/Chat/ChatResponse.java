package com.InSpace.Api.services.dto.Chat;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private Long chatId;
    private String chatName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChatParticipantResponse> participants;
    private List<ChatMessageResponse> messages;
    private int messageCount;
}
