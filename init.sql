
-- ── Extensions ──────────────────────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ── Full-text search index on articles ──────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_articles_fts
    ON articles USING GIN (to_tsvector('english', title || ' ' || content));

-- ── Escalation rules seed data ───────────────────────────────────────────────
INSERT INTO escalation_rules (name, priority, enabled, target_team, target_agent,
    sentiment_threshold, wait_time_minutes, vip_only, min_priority)
VALUES
    ('Critical Negative Sentiment',  1, true, 'tier-2-support', NULL, -0.7, NULL,  false, NULL),
    ('VIP Customer Fast Track',      2, true, 'vip-team',       NULL, NULL, 5,     true,  NULL),
    ('High Priority Timeout',        3, true, 'tier-2-support', NULL, -0.4, 30,    false, 'HIGH'),
    ('Long Unresolved Open Ticket',  4, true, 'tier-1-support', NULL, NULL, 60,    false, 'MEDIUM')
ON CONFLICT DO NOTHING;

-- ── Sample knowledge base articles ───────────────────────────────────────────
INSERT INTO articles (title, content, category, published, view_count, created_at, updated_at)
VALUES
    ('How to reset your password',
     'To reset your password: 1) Click "Forgot Password" on the login page. 2) Enter your registered email. 3) Check your inbox for a reset link. 4) Click the link and create a new password. The link expires in 24 hours.',
     'account', true, 0, NOW(), NOW()),

    ('Billing and payment FAQ',
     'Q: When am I charged? A: Charges occur on the 1st of each month. Q: How do I update my payment method? A: Go to Settings > Billing > Payment Methods. Q: Can I get a refund? A: Refunds are processed within 5-7 business days.',
     'billing', true, 0, NOW(), NOW()),

    ('Getting started guide',
     'Welcome! To get started: 1) Complete your profile in Settings. 2) Connect your data sources under Integrations. 3) Explore the dashboard overview. 4) Set up notifications in Preferences. Need help? Contact support@company.com.',
     'onboarding', true, 0, NOW(), NOW()),

    ('API rate limits and quotas',
     'Free tier: 100 requests/hour. Pro tier: 10,000 requests/hour. Enterprise: custom limits. Rate limit headers: X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset. Exceeding limits returns HTTP 429.',
     'api', true, 0, NOW(), NOW()),

    ('How to cancel your subscription',
     'To cancel: 1) Go to Settings > Billing > Subscription. 2) Click "Cancel Subscription". 3) Select a reason (optional). 4) Confirm cancellation. Your access continues until the end of the current billing period.',
     'billing', true, 0, NOW(), NOW())
ON CONFLICT DO NOTHING;
