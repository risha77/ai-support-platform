package com.support.ticket.kafka;

import com.support.ticket.model.CreateTicketRequest;
import com.support.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketEventConsumer {

    private final TicketService ticketService;

    @KafkaListener(
            topics = "${kafka.topics.chat-events:chat-events}",
            groupId = "${spring.kafka.consumer.group-id:ticket-service}",
            concurrency = "3")
    public void onChatEvent(Map<String, Object> event, Acknowledgment ack) {
        try {
            Object msgCount = event.get("messageCount");
            Object resolved = event.get("resolved");
            Object sessionId = event.get("sessionId");
            Object userMsg = event.get("userMessage");

            int count = msgCount instanceof Number n ? n.intValue() : 0;
            boolean isResolved = resolved instanceof Boolean b && b;

            if (count > 10 && !isResolved && sessionId != null) {
                String msg = userMsg != null ? userMsg.toString() : "";
                String preview = msg.length() > 50 ? msg.substring(0, 50) : msg;

                ticketService.createTicket(CreateTicketRequest.builder()
                        .sessionId(sessionId.toString())
                        .title("Auto-escalated: " + preview)
                        .description(msg)
                        .vipCustomer(Boolean.TRUE.equals(event.get("vipCustomer")))
                        .build());

                log.info("Auto-ticket created for long unresolved session: {}", sessionId);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ticket consumer error", e);
        }
    }
}
