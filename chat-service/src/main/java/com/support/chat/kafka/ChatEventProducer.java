package com.support.chat.kafka;

import com.support.chat.model.ChatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.chat-events:chat-events}")
    private String chatEventsTopic;

    public void publish(ChatEvent event) {
        kafkaTemplate.send(chatEventsTopic, event.getSessionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish chat event for session: {}",
                                event.getSessionId(), ex);
                    } else {
                        log.debug("Chat event published: partition={}, offset={}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
