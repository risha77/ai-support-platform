package com.support.ticket.service;

import com.support.ticket.kafka.TicketEventProducer;
import com.support.ticket.model.*;
import com.support.ticket.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepo;
    private final TicketEventProducer eventProducer;

    @Transactional
    public Ticket createTicket(CreateTicketRequest req) {
        // Default sentiment score — will be updated by SentimentService via Kafka
        double defaultScore = 0.0;
        Ticket.Priority priority = determinePriority(defaultScore, req.isVipCustomer());

        Ticket saved = ticketRepo.save(Ticket.builder()
                .sessionId(req.getSessionId())
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(priority)
                .status(Ticket.Status.OPEN)
                .sentimentScore(defaultScore)
                .vipCustomer(req.isVipCustomer())
                .build());

        eventProducer.publishCreated(TicketEvent.from(saved));
        log.info("Ticket {} created with priority {}", saved.getId(), priority);
        return saved;
    }

    @Transactional
    public Ticket updateStatus(String id, Ticket.Status newStatus, String agentId) {
        return ticketRepo.findById(id).map(ticket -> {
            ticket.setStatus(newStatus);
            if (agentId != null) ticket.setAssignedTo(agentId);
            return ticketRepo.save(ticket);
        }).orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));
    }

    @Transactional
    public Ticket updateSentimentScore(String id, double score) {
        return ticketRepo.findById(id).map(ticket -> {
            ticket.setSentimentScore(score);
            Ticket.Priority newPriority = determinePriority(score, ticket.isVipCustomer());
            ticket.setPriority(newPriority);
            return ticketRepo.save(ticket);
        }).orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));
    }

    public Page<Ticket> findByStatus(Ticket.Status status, Pageable pageable) {
        return ticketRepo.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    private Ticket.Priority determinePriority(double score, boolean vip) {
        if (score < -0.7) return Ticket.Priority.CRITICAL;
        if (score < -0.4 || vip) return Ticket.Priority.HIGH;
        return Ticket.Priority.MEDIUM;
    }
}
