# Online Judge System

A distributed, event-driven online judge inspired by platforms like LeetCode.

The project focuses on backend architecture, asynchronous processing, secure code execution, and system design rather than frontend development.

---

## Overview

The system accepts source code submissions, executes them inside isolated Docker containers, evaluates them against predefined test cases, and returns a final verdict.

The architecture separates business logic from code execution using independent services communicating through Kafka.

---

## Architecture

```
                Client
                   │
                   ▼
             Spring Boot API
                   │
          Store Submission
                   │
        Kafka (submission-topic)
                   │
                   ▼
            Rust Judge Worker
                   │
      Read Problem & Test Cases
            from PostgreSQL
                   │
           Docker Sandbox
                   │
      Compile → Execute → Judge
                   │
        Kafka (result-topic)
                   │
                   ▼
        Spring Boot Consumer
                   │
         Update Submission
                   │
                   ▼
              PostgreSQL
```

---

## Technology Stack

### Backend

- Java 21
- Spring Boot
- Spring Security (planned)
- PostgreSQL

### Execution Engine

- Rust

### Messaging

- Apache Kafka

### Isolation

- Docker

---

## Planned Features

- Submission API
- Problem Management
- Test Case Management
- Asynchronous Submission Queue
- Docker-based Sandboxed Execution
- Judge Engine
- Multiple Language Support
    - Rust
    - C++
    - Java
    - Python
- Execution Timeline
- Structured Logging
- Trace IDs
- Result Polling API

---

## Project Structure

```
online-judge/
│
├── spring-api/
├── rust-worker/
├── docker/
│   └── judge-rust/
├── docs/
└── docker-compose.yml
```

---

## Development Roadmap

### Phase 1

- Database Schema
- Problem CRUD
- Test Case CRUD
- Submission API

### Phase 2

- Docker Runner
- Rust Judge
- Verdict Engine

### Phase 3

- Kafka Integration
- Result Processing
- Execution Timeline

### Phase 4

- Multi-language Support
- Observability
- Performance Improvements

---

## Current Status

🚧 Project initialization

The architecture has been designed and implementation is currently in progress.

---

## Goals

- Learn distributed systems
- Build a scalable backend architecture
- Explore asynchronous communication with Kafka
- Execute untrusted code safely using Docker
- Develop a modular judge engine that can support multiple languages

---

## License

This project is intended for learning and educational purposes.
