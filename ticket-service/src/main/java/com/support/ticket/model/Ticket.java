package com.support.ticket.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_tickets_session", columnList = "session_id"),
    @Index(name = "idx_tickets_status",  columnList = "status"),
    @Index(name = "idx_tickets_priority", columnList = "priority")
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String assignedTo;
    private Double sentimentScore;
    private boolean vipCustomer;

    @CreationTimestamp private Instant createdAt;
    @UpdateTimestamp   private Instant updatedAt;

    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
    public enum Status   { OPEN, IN_PROGRESS, ESCALATED, RESOLVED, CLOSED }
}
