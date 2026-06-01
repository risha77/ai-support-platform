package com.support.knowledge.controller;

import com.support.knowledge.model.*;
import com.support.knowledge.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final ArticleService articleService;

    @GetMapping("/search")
    public ResponseEntity<SearchResult> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(articleService.search(query, limit));
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
    }

    @GetMapping("/articles")
    public ResponseEntity<Page<Article>> listByCategory(
            @RequestParam(required = false) String category,
            Pageable pageable) {
        if (category != null) {
            return ResponseEntity.ok(articleService.findByCategory(category, pageable));
        }
        return ResponseEntity.ok(articleService.findByCategory("general", pageable));
    }

    @PostMapping("/articles")
    public ResponseEntity<Article> createArticle(
            @RequestBody @Valid CreateArticleRequest req) {
        return ResponseEntity.status(201).body(articleService.create(req));
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @RequestBody @Valid CreateArticleRequest req) {
        return ResponseEntity.ok(articleService.update(id, req));
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> suggest(@RequestParam String q) {
        return ResponseEntity.ok(articleService.suggest(q));
    }

    @GetMapping("/context")
    public ResponseEntity<String> buildContext(@RequestParam String query) {
        return ResponseEntity.ok(articleService.buildContext(query));
    }
}
