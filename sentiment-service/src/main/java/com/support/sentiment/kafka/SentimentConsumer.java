package com.support.sentiment.kafka;

import com.support.sentiment.model.SentimentEvent;
import com.support.sentiment.model.SentimentResult;
import com.support.sentiment.service.SentimentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SentimentConsumer {

    private final SentimentService sentimentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sentiment-results:sentiment-results}")
    private String resultsTopic;

    @KafkaListener(
            topics = "${kafka.topics.chat-events:chat-events}",
            groupId = "${spring.kafka.consumer.group-id:sentiment-service}",
            concurrency = "3")
    public void onChatEvent(Map<String, Object> event, Acknowledgment ack) {
        try {
            Object sessionId = event.get("sessionId");
            Object userMsg   = event.get("userMessage");
            Object ticketId  = event.get("ticketId");

            if (sessionId == null || userMsg == null) {
                ack.acknowledge();
                return;
            }

            SentimentResult result = sentimentService.analyze(userMsg.toString());
            log.info("Sentiment for session {}: {} score={}", sessionId, result.getLabel(), result.getScore());

            kafkaTemplate.send(resultsTopic, sessionId.toString(),
                    SentimentEvent.builder()
                            .sessionId(sessionId.toString())
                            .ticketId(ticketId != null ? ticketId.toString() : null)
                            .score(result.getScore())
                            .label(result.getLabel())
                            .urgency(result.getUrgency())
                            .vipCustomer(Boolean.TRUE.equals(event.get("vipCustomer")))
                            .timestamp(Instant.now())
                            .build());

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Sentiment consumer error", e);
        }
    }
}
