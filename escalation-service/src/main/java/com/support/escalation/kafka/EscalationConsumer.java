package com.support.escalation.kafka;

import com.support.escalation.model.EscalationContext;
import com.support.escalation.service.EscalationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EscalationConsumer {

    private final EscalationService escalationService;

    @KafkaListener(
            topics = "${kafka.topics.sentiment-results:sentiment-results}",
            groupId = "${spring.kafka.consumer.group-id:escalation-service}")
    public void onSentimentEvent(Map<String, Object> event, Acknowledgment ack) {
        try {
            String sessionId = String.valueOf(event.getOrDefault("sessionId", ""));
            String ticketId  = String.valueOf(event.getOrDefault("ticketId", ""));
            double score     = ((Number) event.getOrDefault("score", 0.0)).doubleValue();
            int urgency      = ((Number) event.getOrDefault("urgency", 3)).intValue();
            boolean vip      = Boolean.TRUE.equals(event.get("vipCustomer"));

            if (ticketId.isBlank() || ticketId.equals("null")) {
                ack.acknowledge();
                return;
            }

            EscalationContext ctx = EscalationContext.builder()
                    .sessionId(sessionId)
                    .ticketId(ticketId)
                    .sentimentScore(score)
                    .urgency(urgency)
                    .vipCustomer(vip)
                    .createdAt(Instant.now())
                    .build();

            escalationService.evaluate(ctx)
                    .ifPresent(e -> log.info("Escalation created: {}", e.getId()));

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Escalation consumer error", e);
        }
    }
}
