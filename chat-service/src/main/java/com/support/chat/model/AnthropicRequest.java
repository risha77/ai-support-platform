package com.support.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnthropicRequest {
    private String model;
    @JsonProperty("max_tokens")
    private int maxTokens;
    private String system;
    private List<AnthropicMessage> messages;
}
