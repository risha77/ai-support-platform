package com.support.knowledge.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class CreateArticleRequest {
    @NotBlank private String title;
    @NotBlank private String content;
    private String category;
    private List<String> tags;
    private boolean published = true;
}
