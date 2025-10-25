package com.InSpace.Api.services.dto.Chat;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipantResponse {
    private Long userId;
    private String username;
    private String email;
}
