package com.support.escalation.service;

import com.support.escalation.model.*;
import com.support.escalation.repository.EscalationRepository;
import com.support.escalation.repository.EscalationRuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EscalationService {

    private final EscalationRuleRepository ruleRepo;
    private final EscalationRepository escalationRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.escalation-events:escalation-events}")
    private String escalationTopic;

    public Optional<Escalation> evaluate(EscalationContext ctx) {
        return ruleRepo.findByEnabledTrueOrderByPriorityAsc()
                .stream()
                .filter(rule -> rule.matches(ctx))
                .findFirst()
                .map(rule -> execute(rule, ctx));
    }

    @Transactional
    Escalation execute(EscalationRule rule, EscalationContext ctx) {
        Escalation escalation = escalationRepo.save(Escalation.builder()
                .ticketId(ctx.getTicketId())
                .ruleName(rule.getName())
                .assignedTeam(rule.getTargetTeam())
                .assignedAgent(rule.getTargetAgent())
                .reason("Rule '%s' triggered — sentiment=%.2f urgency=%d"
                        .formatted(rule.getName(), ctx.getSentimentScore(), ctx.getUrgency()))
                .status(Escalation.EscalationStatus.PENDING)
                .build());

        kafkaTemplate.send(escalationTopic, ctx.getTicketId(),
                EscalationEvent.builder()
                        .escalationId(escalation.getId())
                        .ticketId(ctx.getTicketId())
                        .ruleName(rule.getName())
                        .assignedTeam(rule.getTargetTeam())
                        .reason(escalation.getReason())
                        .timestamp(Instant.now())
                        .build());

        log.info("Ticket {} escalated to team '{}' via rule '{}'",
                ctx.getTicketId(), rule.getTargetTeam(), rule.getName());
        return escalation;
    }
}
