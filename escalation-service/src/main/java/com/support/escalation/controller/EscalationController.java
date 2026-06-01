package com.support.escalation.controller;

import com.support.escalation.model.Escalation;
import com.support.escalation.model.EscalationRule;
import com.support.escalation.repository.EscalationRepository;
import com.support.escalation.repository.EscalationRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/escalations")
@RequiredArgsConstructor
public class EscalationController {

    private final EscalationRepository escalationRepo;
    private final EscalationRuleRepository ruleRepo;

    @GetMapping
    public ResponseEntity<Page<Escalation>> list(
            @RequestParam(defaultValue = "PENDING") Escalation.EscalationStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(escalationRepo.findByStatusOrderByCreatedAtDesc(status, pageable));
    }

    @GetMapping("/rules")
    public ResponseEntity<?> listRules() {
        return ResponseEntity.ok(ruleRepo.findAll());
    }

    @PostMapping("/rules")
    public ResponseEntity<EscalationRule> createRule(@RequestBody EscalationRule rule) {
        return ResponseEntity.status(201).body(ruleRepo.save(rule));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Escalation> updateStatus(
            @PathVariable String id,
            @RequestParam Escalation.EscalationStatus status) {
        return escalationRepo.findById(id).map(e -> {
            e.setStatus(status);
            return ResponseEntity.ok(escalationRepo.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }
}
