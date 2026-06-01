package com.support.sentiment.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentEvent {
    private String sessionId;
    private String ticketId;
    private double score;
    private String label;
    private int urgency;
    private boolean vipCustomer;
    private Instant timestamp;
}
