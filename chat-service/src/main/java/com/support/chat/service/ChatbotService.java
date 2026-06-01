package com.support.chat.service;

import com.support.chat.model.*;
import com.support.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final WebClient anthropicClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageRepository messageRepo;
    private final KnowledgeBaseClient knowledgeBaseClient;

    @Value("${anthropic.api.model:claude-sonnet-4-6}")
    private String model;

    @SuppressWarnings("unchecked")
    public ChatResponse processMessage(ChatRequest req, String sessionId) {
        List<AnthropicMessage> history = loadHistory(sessionId);

        // Enrich prompt with KB context
        String kbContext = "No context retrieved.";
        try {
            kbContext = knowledgeBaseClient.searchContext(req.getMessage());
        } catch (Exception e) {
            log.warn("KB context fetch failed: {}", e.getMessage());
        }

        String systemPrompt = """
                You are a helpful customer support assistant for an enterprise platform.
                Answer customer queries accurately and empathetically.
                Use the following knowledge base context when relevant:
                %s
                If you cannot resolve the issue directly, suggest creating a support ticket.
                Keep responses concise and professional.
                """.formatted(kbContext);

        history.add(new AnthropicMessage("user", req.getMessage()));

        AnthropicRequest aiReq = AnthropicRequest.builder()
                .model(model)
                .maxTokens(1024)
                .system(systemPrompt)
                .messages(history)
                .build();

        String botMsg = anthropicClient.post()
                .uri("/v1/messages")
                .bodyValue(aiReq)
                .retrieve()
                .bodyToMono(AnthropicResponse.class)
                .map(r -> r.getContent().get(0).getText())
                .doOnError(e -> log.error("Anthropic API error", e))
                .onErrorReturn("I'm sorry, I'm having trouble responding. Please try again or contact support.")
                .block();

        persist(sessionId, req.getMessage(), botMsg);
        history.add(new AnthropicMessage("assistant", botMsg));
        saveHistory(sessionId, history);

        return ChatResponse.of(botMsg, sessionId);
    }

    public List<ChatMessage> getHistory(String sessionId, int page) {
        return messageRepo.findBySessionIdOrderByCreatedAtDesc(
                sessionId, PageRequest.of(page, 20)).getContent();
    }

    /**
     * Escalates the conversation to a human-handled ticket.
     * Called explicitly by the user or automatically after N unresolved messages.
     */
    public ChatMessage escalateToTicket(CreateTicketRequest req) {
        log.info("Escalating session {} to ticket", req.getSessionId());
        // Record the escalation as a chat message for history continuity
        return messageRepo.save(ChatMessage.builder()
                .sessionId(req.getSessionId())
                .userMessage("[Ticket requested] " + req.getTitle())
                .botResponse("I've created a support ticket for you. Our team will follow up shortly. " +
                             "Your ticket title: \"" + req.getTitle() + "\"")
                .resolved(false)
                .createdAt(Instant.now())
                .build());
    }

    @SuppressWarnings("unchecked")
    private List<AnthropicMessage> loadHistory(String sessionId) {
        Object cached = redisTemplate.opsForValue().get("chat:history:" + sessionId);
        if (cached instanceof List<?> list) {
            return new ArrayList<>((List<AnthropicMessage>) list);
        }
        return new ArrayList<>();
    }

    private void saveHistory(String sessionId, List<AnthropicMessage> history) {
        List<AnthropicMessage> trimmed = history.size() > 20
                ? history.subList(history.size() - 20, history.size())
                : history;
        redisTemplate.opsForValue().set("chat:history:" + sessionId, trimmed, Duration.ofHours(2));
    }

    private void persist(String sessionId, String user, String bot) {
        long count = messageRepo.countBySessionId(sessionId);
        messageRepo.save(ChatMessage.builder()
                .sessionId(sessionId)
                .userMessage(user)
                .botResponse(bot)
                .messageCount((int) count + 1)
                .createdAt(Instant.now())
                .build());
    }
}
