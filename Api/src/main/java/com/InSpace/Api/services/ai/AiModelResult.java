package com.InSpace.Api.services.ai;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiModelResult {
    // human readable content the model returned (e.g. explanation or action list)
    private String content;

    // raw JSON string returned by the provider (for debugging / re-processing)
    private String rawJson;

    // optional storage URL if the JSON was persisted externally
    private String jsonUrl;
}
