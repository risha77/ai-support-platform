package com.support.escalation.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscalationContext {
    private String ticketId;
    private String sessionId;
    private double sentimentScore;
    private int urgency;
    private boolean vipCustomer;
    private EscalationRule.TicketPriority priority;
    private Instant createdAt;
}
