package com.support.chat.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatEvent {
    private String sessionId;
    private String ticketId;
    private String userMessage;
    private String botResponse;
    private int messageCount;
    private boolean resolved;
    private boolean vipCustomer;
    private Instant timestamp;
}
