package com.support.ticket;

import com.support.ticket.kafka.TicketEventProducer;
import com.support.ticket.model.*;
import com.support.ticket.repository.TicketRepository;
import com.support.ticket.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock TicketRepository ticketRepo;
    @Mock TicketEventProducer eventProducer;

    @InjectMocks TicketService ticketService;

    @Test
    void createTicket_assignsCorrectPriorityForNegativeSentiment() {
        CreateTicketRequest req = new CreateTicketRequest();
        req.setSessionId("sess-1");
        req.setTitle("Urgent issue");
        req.setDescription("I'm extremely frustrated!");
        req.setVipCustomer(false);

        Ticket mockTicket = Ticket.builder()
                .id("ticket-1").sessionId("sess-1")
                .title("Urgent issue").priority(Ticket.Priority.MEDIUM)
                .status(Ticket.Status.OPEN).sentimentScore(0.0)
                .createdAt(Instant.now()).build();

        when(ticketRepo.save(any(Ticket.class))).thenReturn(mockTicket);
        doNothing().when(eventProducer).publishCreated(any());

        Ticket result = ticketService.createTicket(req);

        assertThat(result.getId()).isEqualTo("ticket-1");
        assertThat(result.getStatus()).isEqualTo(Ticket.Status.OPEN);
        verify(ticketRepo).save(any(Ticket.class));
        verify(eventProducer).publishCreated(any(TicketEvent.class));
    }

    @Test
    void createTicket_vipCustomerGetHighPriority() {
        CreateTicketRequest req = new CreateTicketRequest();
        req.setSessionId("sess-vip");
        req.setTitle("VIP request");
        req.setVipCustomer(true);

        Ticket mockTicket = Ticket.builder()
                .id("t-2").sessionId("sess-vip")
                .title("VIP request").priority(Ticket.Priority.HIGH)
                .status(Ticket.Status.OPEN).sentimentScore(0.0)
                .createdAt(Instant.now()).build();

        when(ticketRepo.save(any(Ticket.class))).thenReturn(mockTicket);
        doNothing().when(eventProducer).publishCreated(any());

        Ticket result = ticketService.createTicket(req);
        assertThat(result.getPriority()).isEqualTo(Ticket.Priority.HIGH);
    }
}
