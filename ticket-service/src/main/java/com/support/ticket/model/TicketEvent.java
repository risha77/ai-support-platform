package com.support.ticket.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketEvent {
    private String ticketId;
    private Ticket.Priority priority;
    private Ticket.Status status;
    private double sentimentScore;
    private boolean vipCustomer;
    private Instant createdAt;

    public static TicketEvent from(Ticket t) {
        return TicketEvent.builder()
                .ticketId(t.getId())
                .priority(t.getPriority())
                .status(t.getStatus())
                .sentimentScore(t.getSentimentScore() != null ? t.getSentimentScore() : 0.0)
                .vipCustomer(t.isVipCustomer())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
