package com.support.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class GatewayConfig {

    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            log.info("[GW] {} {}", exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI().getPath());
            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("[GW] Response: {}", exchange.getResponse().getStatusCode())));
        };
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(
                exchange.getRequest().getHeaders().getFirst("X-User-ID"))
                .defaultIfEmpty("anonymous");
    }
}
