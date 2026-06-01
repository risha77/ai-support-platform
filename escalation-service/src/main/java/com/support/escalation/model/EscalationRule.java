package com.support.escalation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "escalation_rules")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EscalationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int priority;        // lower = evaluated first
    private boolean enabled;
    private String targetTeam;
    private String targetAgent;

    private Double sentimentThreshold;   // escalate if score < this value
    private Integer waitTimeMinutes;     // escalate if open longer than N mins
    private boolean vipOnly;

    @Enumerated(EnumType.STRING)
    private TicketPriority minPriority;

    public boolean matches(EscalationContext ctx) {
        if (!enabled) return false;
        if (vipOnly && !ctx.isVipCustomer()) return false;
        if (sentimentThreshold != null && ctx.getSentimentScore() > sentimentThreshold) return false;
        if (minPriority != null && ctx.getPriority() != null &&
            ctx.getPriority().ordinal() < minPriority.ordinal()) return false;
        if (waitTimeMinutes != null && ctx.getCreatedAt() != null) {
            long waited = java.time.Duration.between(ctx.getCreatedAt(),
                java.time.Instant.now()).toMinutes();
            if (waited < waitTimeMinutes) return false;
        }
        return true;
    }

    public enum TicketPriority { LOW, MEDIUM, HIGH, CRITICAL }
}
