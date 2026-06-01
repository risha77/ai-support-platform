package com.support.knowledge.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Publishes knowledge-base analytics events (article views, search queries)
 * for downstream consumption by a future analytics service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.knowledge-events:knowledge-events}")
    private String knowledgeEventsTopic;

    public void publishArticleViewed(Long articleId, String query) {
        Map<String, Object> event = Map.of(
                "type",      "ARTICLE_VIEWED",
                "articleId", articleId,
                "query",     query != null ? query : "",
                "timestamp", Instant.now().toString()
        );
        kafkaTemplate.send(knowledgeEventsTopic, String.valueOf(articleId), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) log.warn("Failed to publish article-viewed event", ex);
                });
    }

    public void publishSearchPerformed(String query, int resultCount) {
        Map<String, Object> event = Map.of(
                "type",        "SEARCH_PERFORMED",
                "query",       query,
                "resultCount", resultCount,
                "timestamp",   Instant.now().toString()
        );
        kafkaTemplate.send(knowledgeEventsTopic, query, event)
                .whenComplete((r, ex) -> {
                    if (ex != null) log.warn("Failed to publish search event", ex);
                });
    }
}
