package com.InSpace.Api.services.dto.Chat;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatRequest {
    private String chatName;
    private String description;
    private List<Long> participantIds;  // Team members who can access this chat
}
