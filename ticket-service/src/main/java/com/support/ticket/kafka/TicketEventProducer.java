package com.support.ticket.kafka;

import com.support.ticket.model.TicketEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.ticket-events:ticket-events}")
    private String ticketEventsTopic;

    public void publishCreated(TicketEvent event) {
        kafkaTemplate.send(ticketEventsTopic, event.getTicketId(), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) log.error("Failed to publish ticket event", ex);
                    else log.info("Ticket event published: {}", event.getTicketId());
                });
    }
}
