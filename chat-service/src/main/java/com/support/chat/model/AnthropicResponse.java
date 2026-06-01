package com.support.chat.model;

import lombok.Data;
import java.util.List;

@Data
public class AnthropicResponse {
    private List<ContentBlock> content;

    @Data
    public static class ContentBlock {
        private String type;
        private String text;
    }
}
