package com.support.ticket.controller;

import com.support.ticket.model.*;
import com.support.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody @Valid CreateTicketRequest req) {
        return ResponseEntity.status(201).body(ticketService.createTicket(req));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(
            @PathVariable String id,
            @RequestParam Ticket.Status status,
            @RequestParam(required = false) String agentId) {
        return ResponseEntity.ok(ticketService.updateStatus(id, status, agentId));
    }

    @GetMapping
    public ResponseEntity<Page<Ticket>> listByStatus(
            @RequestParam(defaultValue = "OPEN") Ticket.Status status,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.findByStatus(status, pageable));
    }
}
