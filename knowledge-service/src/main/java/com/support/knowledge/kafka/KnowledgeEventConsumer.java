package com.support.knowledge.kafka;

import com.support.knowledge.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes ticket-events to pre-warm the knowledge-base cache
 * with articles relevant to the ticket description, so the
 * chat-service Feign call to /api/knowledge/context gets a cache hit.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeEventConsumer {

    private final ArticleService articleService;

    @KafkaListener(
            topics = "${kafka.topics.ticket-events:ticket-events}",
            groupId = "${spring.kafka.consumer.group-id:knowledge-service}",
            concurrency = "2")
    public void onTicketEvent(Map<String, Object> event, Acknowledgment ack) {
        try {
            Object ticketId    = event.get("ticketId");
            Object description = event.get("description");

            if (description != null && !description.toString().isBlank()) {
                log.info("Pre-warming KB cache for ticket: {}", ticketId);
                // Triggers @Cacheable — result is stored in Redis for future hits
                articleService.search(description.toString(), 5);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("KnowledgeEventConsumer error", e);
        }
    }
}
