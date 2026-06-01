package com.support.knowledge.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String query;
    private List<Article> articles;
    private long totalCount;
}
