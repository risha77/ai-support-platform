package com.support.sentiment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentimentResult {
    private double score;
    private String label;
    private int urgency;

    public static SentimentResult neutral() {
        SentimentResult r = new SentimentResult();
        r.setScore(0.0);
        r.setLabel("NEUTRAL");
        r.setUrgency(3);
        return r;
    }
}
