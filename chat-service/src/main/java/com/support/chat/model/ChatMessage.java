package com.support.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_chat_session", columnList = "session_id")
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String userMessage;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String botResponse;

    @Column(name = "message_count")
    private int messageCount;

    private boolean resolved;

    @CreationTimestamp
    private Instant createdAt;

    public static ChatMessage of(String sessionId, String user, String bot) {
        return ChatMessage.builder()
                .sessionId(sessionId)
                .userMessage(user)
                .botResponse(bot)
                .build();
    }
}
