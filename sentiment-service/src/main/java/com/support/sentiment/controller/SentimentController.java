package com.support.sentiment.controller;

import com.support.sentiment.model.SentimentResult;
import com.support.sentiment.service.SentimentService;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sentiment")
@RequiredArgsConstructor
public class SentimentController {

    private final SentimentService sentimentService;

    @PostMapping("/analyze")
    public ResponseEntity<SentimentResult> analyze(@RequestBody Map<String, String> body) {
        String text = body.getOrDefault("text", "");
        return ResponseEntity.ok(sentimentService.analyze(text));
    }
}
