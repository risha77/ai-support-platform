# AI Customer Support Platform

Enterprise-grade microservices platform for AI-powered customer support.

## Architecture

| Service            | Port | Description                                    |
|--------------------|------|------------------------------------------------|
| api-gateway        | 8000 | Spring Cloud Gateway — routing, rate limiting  |
| chat-service       | 8081 | Chatbot with Anthropic AI + conversation cache |
| ticket-service     | 8082 | Ticket lifecycle management + JPA              |
| sentiment-service  | 8083 | NLP sentiment analysis via Kafka               |
| escalation-service | 8084 | Rules engine for automatic escalation          |
| knowledge-service  | 8085 | Full-text search + Redis-cached KB             |
| kafka-ui           | 9090 | Kafka topic browser (dev only)                 |

## Quick Start

### Prerequisites
- Java 21+
- Docker + Docker Compose
- Maven 3.9+
- Anthropic API key

### 1. Configure environment
```bash
cp .env.example .env
# Edit .env and set ANTHROPIC_API_KEY
```

### 2. Start infrastructure only (for local dev)
```bash
docker compose up postgres redis kafka kafka-ui zookeeper -d
```

### 3. Run a service locally
```bash
cd chat-service
mvn spring-boot:run
```

### 4. Run everything with Docker
```bash
mvn clean package -DskipTests
docker compose up --build
```

## API Examples

### Send a chat message
```bash
curl -X POST http://localhost:8000/api/chat/message \
  -H "Content-Type: application/json" \
  -H "X-Session-ID: session-123" \
  -d '{"message": "How do I reset my password?"}'
```

### Create a ticket
```bash
curl -X POST http://localhost:8000/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"session-123","title":"Cannot login","description":"Getting 401 errors","vipCustomer":false}'
```

### Search knowledge base
```bash
curl "http://localhost:8000/api/knowledge/search?query=password+reset&limit=3"
```

### Analyze sentiment
```bash
curl -X POST http://localhost:8000/api/sentiment/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "I am extremely frustrated with your service!"}'
```

## Kafka Topics

| Topic              | Producers        | Consumers                          |
|--------------------|------------------|------------------------------------|
| chat-events        | chat-service     | sentiment-service, ticket-service  |
| ticket-events      | ticket-service   | escalation-service                 |
| sentiment-results  | sentiment-service| escalation-service                 |
| escalation-events  | escalation-service| (notifications, audit log)        |

## Event Flow

```
User Message
    → chat-service → [chat-events topic]
                          ↓              ↓
               sentiment-service    ticket-service
                          ↓
               [sentiment-results topic]
                          ↓
               escalation-service → [escalation-events topic]
```

## Tech Stack
- **Java 21** with Virtual Threads
- **Spring Boot 3.2** — all services
- **Spring Cloud Gateway** — API Gateway
- **Apache Kafka 7.5** — event streaming
- **Redis 7** — caching + rate limiting
- **PostgreSQL 16** — primary data store
- **Anthropic Claude API** — AI chatbot + sentiment
