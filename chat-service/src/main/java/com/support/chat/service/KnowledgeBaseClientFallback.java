package com.support.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KnowledgeBaseClientFallback implements KnowledgeBaseClient {

    @Override
    public String searchContext(String query) {
        log.warn("Knowledge base unavailable for query: {}", query);
        return "Knowledge base is currently unavailable. Please answer based on general knowledge.";
    }
}
