package com.support.knowledge.service;

import com.support.knowledge.model.*;
import com.support.knowledge.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepo;

    @Cacheable(value = "knowledgeSearch", key = "#query + '_' + #limit")
    public SearchResult search(String query, int limit) {
        log.info("Searching knowledge base: '{}'", query);
        List<Article> articles = articleRepo.fullTextSearch(query, limit);
        return SearchResult.builder()
                .query(query)
                .articles(articles)
                .totalCount(articles.size())
                .build();
    }

    @Cacheable(value = "articleById", key = "#id")
    public Article findById(Long id) {
        Article article = articleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found: " + id));
        articleRepo.incrementViewCount(id);
        return article;
    }

    public List<String> suggest(String prefix) {
        return articleRepo.suggest(prefix);
    }

    public Page<Article> findByCategory(String category, Pageable pageable) {
        return articleRepo.findByCategory(category, pageable);
    }

    public String buildContext(String query) {
        List<Article> articles = articleRepo.fullTextSearch(query, 3);
        if (articles.isEmpty()) return "No relevant knowledge base articles found.";
        return articles.stream()
                .map(a -> "## " + a.getTitle() + "\n" + a.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    @Transactional
    @CacheEvict(value = "knowledgeSearch", allEntries = true)
    public Article create(CreateArticleRequest req) {
        return articleRepo.save(Article.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .category(req.getCategory())
                .tags(req.getTags())
                .published(req.isPublished())
                .viewCount(0)
                .build());
    }

    @Transactional
    @CacheEvict(value = {"knowledgeSearch", "articleById"}, allEntries = true)
    public Article update(Long id, CreateArticleRequest req) {
        return articleRepo.findById(id).map(a -> {
            a.setTitle(req.getTitle());
            a.setContent(req.getContent());
            a.setCategory(req.getCategory());
            a.setTags(req.getTags());
            a.setPublished(req.isPublished());
            return articleRepo.save(a);
        }).orElseThrow(() -> new EntityNotFoundException("Article not found: " + id));
    }
}
