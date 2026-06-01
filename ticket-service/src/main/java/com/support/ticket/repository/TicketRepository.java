package com.support.ticket.repository;

import com.support.ticket.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    Page<Ticket> findByStatusOrderByCreatedAtDesc(Ticket.Status status, Pageable pageable);
    List<Ticket> findBySessionId(String sessionId);
    Page<Ticket> findByAssignedToOrderByCreatedAtDesc(String assignedTo, Pageable pageable);
}
