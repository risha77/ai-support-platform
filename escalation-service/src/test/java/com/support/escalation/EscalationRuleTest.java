package com.support.escalation;

import com.support.escalation.model.EscalationContext;
import com.support.escalation.model.EscalationRule;
import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EscalationRuleTest {

    @Test
    void rule_matchesCriticalSentiment() {
        EscalationRule rule = EscalationRule.builder()
                .enabled(true).sentimentThreshold(-0.7)
                .name("Critical").targetTeam("tier-2").build();

        EscalationContext ctx = EscalationContext.builder()
                .ticketId("t1").sentimentScore(-0.9)
                .createdAt(Instant.now()).build();

        assertThat(rule.matches(ctx)).isTrue();
    }

    @Test
    void rule_doesNotMatchPositiveSentiment() {
        EscalationRule rule = EscalationRule.builder()
                .enabled(true).sentimentThreshold(-0.7)
                .name("Critical").targetTeam("tier-2").build();

        EscalationContext ctx = EscalationContext.builder()
                .ticketId("t2").sentimentScore(0.3)
                .createdAt(Instant.now()).build();

        assertThat(rule.matches(ctx)).isFalse();
    }

    @Test
    void rule_vipOnly_doesNotMatchNonVip() {
        EscalationRule rule = EscalationRule.builder()
                .enabled(true).vipOnly(true)
                .name("VIP").targetTeam("vip-team").build();

        EscalationContext ctx = EscalationContext.builder()
                .ticketId("t3").vipCustomer(false)
                .createdAt(Instant.now()).build();

        assertThat(rule.matches(ctx)).isFalse();
    }
}
