package com.support.sentiment;

import com.support.sentiment.model.SentimentResult;
import com.support.sentiment.service.SentimentService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SentimentServiceTest {

    @Test
    void sentimentResult_neutral_returnsDefaultValues() {
        SentimentResult neutral = SentimentResult.neutral();
        assertThat(neutral.getScore()).isEqualTo(0.0);
        assertThat(neutral.getLabel()).isEqualTo("NEUTRAL");
        assertThat(neutral.getUrgency()).isEqualTo(3);
    }
}
