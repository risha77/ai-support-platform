package com.support.sentiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SentimentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentimentServiceApplication.class, args);
    }
}
