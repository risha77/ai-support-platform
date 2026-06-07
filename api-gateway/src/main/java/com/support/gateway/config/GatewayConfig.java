package com.support.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private static final Logger log =
            LoggerFactory.getLogger(GatewayConfig.class);

    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            log.info("[GW] {} {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI().getPath());

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() ->
                            log.info("[GW] Response: {}",
                                    exchange.getResponse().getStatusCode())));
        };
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(
                        exchange.getRequest()
                                .getHeaders()
                                .getFirst("X-User-ID"))
                .defaultIfEmpty("anonymous");
    }
}