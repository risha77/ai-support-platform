package com.support.escalation.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "escalations", indexes = {
    @Index(name = "idx_esc_ticket", columnList = "ticket_id"),
    @Index(name = "idx_esc_status", columnList = "status")
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

    private String ruleName;
    private String assignedTeam;
    private String assignedAgent;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    private EscalationStatus status;

    @CreationTimestamp
    private Instant createdAt;

    public enum EscalationStatus { PENDING, ASSIGNED, RESOLVED }
}
