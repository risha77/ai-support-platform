package com.support.sentiment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.support.sentiment.model.SentimentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SentimentService {

    private final WebClient anthropicClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.model:claude-haiku-4-5}")
    private String model;

    public SentimentResult analyze(String text) {
        String cacheKey = "sentiment:" + md5(text);
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.convertValue(cached, SentimentResult.class);
            } catch (Exception e) {
                log.warn("Cache deserialization failed, re-analyzing");
            }
        }

        String prompt = String.format("""
                Analyze the sentiment of this customer support message.
                Return ONLY a JSON object (no markdown, no explanation):
                {"score": <float from -1.0 to 1.0>, "label": "<POSITIVE|NEUTRAL|NEGATIVE>", "urgency": <int 0-10>}
                
                Message: "%s"
                """, text.replace("\"", "\\\"").substring(0, Math.min(text.length(), 500)));

        Map<String, Object> request = Map.of(
                "model", model,
                "max_tokens", 150,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = anthropicClient.post()
                    .uri("/v1/messages")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            String rawJson = (String) content.get(0).get("text");

            SentimentResult result = objectMapper.readValue(rawJson.trim(), SentimentResult.class);
            redisTemplate.opsForValue().set(cacheKey, result, Duration.ofHours(1));
            return result;
        } catch (Exception e) {
            log.error("Sentiment analysis failed for text snippet", e);
            return SentimentResult.neutral();
        }
    }

    private String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return String.valueOf(text.hashCode());
        }
    }
}
