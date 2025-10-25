package com.InSpace.Api.services.dto.Chat;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long messageId;
    private Long chatId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private boolean isModelMessage;  // true if message is from AI model
    private LocalDateTime sentAt;
}
