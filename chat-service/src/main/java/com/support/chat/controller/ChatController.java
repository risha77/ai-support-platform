package com.support.chat.controller;

import com.support.chat.kafka.ChatEventProducer;
import com.support.chat.model.*;
import com.support.chat.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatbotService chatbotService;
    private final ChatEventProducer eventProducer;

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(
            @RequestBody @Valid ChatRequest request,
            @RequestHeader(value = "X-Session-ID", defaultValue = "default-session") String sessionId) {

        log.info("Processing chat message for session: {}", sessionId);
        ChatResponse response = chatbotService.processMessage(request, sessionId);

        eventProducer.publish(ChatEvent.builder()
                .sessionId(sessionId)
                .userMessage(request.getMessage())
                .botResponse(response.getMessage())
                .resolved(response.isResolved())
                .vipCustomer(request.isVipCustomer())
                .timestamp(Instant.now())
                .build());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(chatbotService.getHistory(sessionId, page));
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        log.info("Clearing chat session: {}", sessionId);
        return ResponseEntity.noContent().build();
    }
}
