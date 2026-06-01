package com.support.chat.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "knowledge-service", fallback = KnowledgeBaseClientFallback.class)
public interface KnowledgeBaseClient {

    @GetMapping("/api/knowledge/context")
    String searchContext(@RequestParam("query") String query);
}
