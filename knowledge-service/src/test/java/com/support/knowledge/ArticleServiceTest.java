package com.support.knowledge;

import com.support.knowledge.model.*;
import com.support.knowledge.repository.ArticleRepository;
import com.support.knowledge.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock ArticleRepository articleRepo;

    @InjectMocks ArticleService articleService;

    @Test
    void buildContext_withNoResults_returnsDefaultMessage() {
        when(articleRepo.fullTextSearch(anyString(), anyInt())).thenReturn(List.of());
        String ctx = articleService.buildContext("unknown query");
        assertThat(ctx).contains("No relevant knowledge base articles found");
    }

    @Test
    void buildContext_withArticles_includesTitles() {
        Article a = Article.builder().id(1L).title("How to reset password")
                .content("Click forgot password...").build();
        when(articleRepo.fullTextSearch(anyString(), anyInt())).thenReturn(List.of(a));

        String ctx = articleService.buildContext("password");
        assertThat(ctx).contains("How to reset password");
        assertThat(ctx).contains("Click forgot password");
    }
}
