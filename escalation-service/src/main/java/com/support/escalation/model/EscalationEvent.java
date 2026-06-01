package com.support.escalation.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscalationEvent {
    private String escalationId;
    private String ticketId;
    private String ruleName;
    private String assignedTeam;
    private String reason;
    private Instant timestamp;
}
