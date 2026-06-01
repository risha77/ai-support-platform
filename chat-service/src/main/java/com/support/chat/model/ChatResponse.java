package com.support.chat.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private String sessionId;
    private boolean resolved;
    private Instant timestamp;

    public static ChatResponse of(String message, String sessionId) {
        return ChatResponse.builder()
                .message(message)
                .sessionId(sessionId)
                .resolved(message.toLowerCase().contains("ticket") ||
                          message.toLowerCase().contains("resolved"))
                .timestamp(Instant.now())
                .build();
    }
}
