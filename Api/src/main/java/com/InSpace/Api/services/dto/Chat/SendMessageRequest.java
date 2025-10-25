package com.InSpace.Api.services.dto.Chat;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {
    private Long chatId;
    private String content;
}
