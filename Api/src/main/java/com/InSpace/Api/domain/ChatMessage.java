package com.InSpace.Api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long messageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_id", nullable = false)
  private Chat chat;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id")
  private User sender;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column
  @Builder.Default
  private boolean isModelMessage = false; // if true go fetch AI response

  @Column
  @CreationTimestamp
  @Builder.Default
  private LocalDateTime sentAt = LocalDateTime.now();

  @OneToOne(mappedBy = "chatMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private Prompt prompt;

  @OneToOne(mappedBy = "chatMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private AiChatResponse chatResponse;
}
