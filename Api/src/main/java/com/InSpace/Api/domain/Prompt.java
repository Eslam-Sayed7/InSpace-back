package com.InSpace.Api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "prompts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Prompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promptId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Optional URL pointing to the schema used to interpret page elements (e.g. login page schema)
    @Column(name = "parsed_elements_json_url")
    private String parsedElementsJsonUrl;


    @Column(name = "parsed_elements_json", columnDefinition = "TEXT")
    private String parsedElementsJson;

    //  (agent config, temperature, role instructions) as JSON/text
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
