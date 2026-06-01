package com.support.chat;

import com.support.chat.model.*;
import com.support.chat.repository.ChatMessageRepository;
import com.support.chat.service.ChatbotService;
import com.support.chat.service.KnowledgeBaseClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock WebClient anthropicClient;
    @Mock WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock WebClient.RequestBodySpec requestBodySpec;
    @Mock WebClient.ResponseSpec responseSpec;
    @Mock RedisTemplate<String, Object> redisTemplate;
    @Mock ValueOperations<String, Object> valueOps;
    @Mock ChatMessageRepository messageRepo;
    @Mock KnowledgeBaseClient knowledgeBaseClient;

    @InjectMocks ChatbotService chatbotService;

    @Test
    void escalateToTicket_savesMessageAndReturnsConfirmation() {
        CreateTicketRequest req = new CreateTicketRequest();
        req.setSessionId("sess-001");
        req.setTitle("Cannot login");
        req.setDescription("Getting 401 errors");

        ChatMessage saved = ChatMessage.builder()
                .id("msg-1")
                .sessionId("sess-001")
                .userMessage("[Ticket requested] Cannot login")
                .botResponse("I've created a support ticket for you.")
                .resolved(false)
                .createdAt(Instant.now())
                .build();

        when(messageRepo.save(any(ChatMessage.class))).thenReturn(saved);

        ChatMessage result = chatbotService.escalateToTicket(req);

        assertThat(result.getSessionId()).isEqualTo("sess-001");
        assertThat(result.getBotResponse()).contains("support ticket");
        verify(messageRepo).save(any(ChatMessage.class));
    }

    @Test
    void getHistory_returnsPagedMessages() {
        when(messageRepo.findBySessionIdOrderByCreatedAtDesc(eq("sess-001"), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var result = chatbotService.getHistory("sess-001", 0);
        assertThat(result).isEmpty();
    }
}
