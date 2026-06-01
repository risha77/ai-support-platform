package com.support.escalation.repository;

import com.support.escalation.model.Escalation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, String> {
    Page<Escalation> findByStatusOrderByCreatedAtDesc(Escalation.EscalationStatus status, Pageable pageable);
    Page<Escalation> findByAssignedTeamOrderByCreatedAtDesc(String team, Pageable pageable);
}
