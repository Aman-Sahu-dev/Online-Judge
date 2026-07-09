-- ============================================
-- Online Judge Database Schema
-- PostgreSQL
-- ============================================

-- Submission status enum
CREATE TYPE submission_status AS ENUM (
    'PENDING',
    'QUEUED',
    'COMPILING',
    'RUNNING',
    'CHECKING',
    'ACCEPTED',
    'WRONG_ANSWER',
    'TIME_LIMIT_EXCEEDED',
    'MEMORY_LIMIT_EXCEEDED',
    'RUNTIME_ERROR',
    'COMPILATION_ERROR',
    'SYSTEM_ERROR'
);

-- ============================================
-- PROBLEM
-- ============================================
CREATE TABLE problem (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    time_limit_ms   INTEGER NOT NULL DEFAULT 5000,
    memory_limit_mb INTEGER NOT NULL DEFAULT 512,
    difficulty      VARCHAR(50) NOT NULL DEFAULT 'EASY',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================
-- TEST CASE
-- ============================================
CREATE TABLE test_case (
    id              BIGSERIAL PRIMARY KEY,
    problem_id      UUID NOT NULL REFERENCES problem(id) ON DELETE CASCADE,
    input           TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    hidden          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_test_case_problem_id ON test_case(problem_id);

-- ============================================
-- SUBMISSION
-- ============================================
CREATE TABLE submission (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID,                                          -- nullable for v1, no auth
    problem_id      UUID NOT NULL REFERENCES problem(id),
    language        VARCHAR(50) NOT NULL DEFAULT 'rust',
    code            TEXT NOT NULL,
    status          submission_status NOT NULL DEFAULT 'PENDING',
    trace_id        UUID NOT NULL DEFAULT gen_random_uuid(),
    execution_time  INTEGER,                                       -- milliseconds, null until judged
    memory_used     INTEGER,                                       -- kb, null until judged
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_submission_problem_id ON submission(problem_id);
CREATE INDEX idx_submission_trace_id ON submission(trace_id);
CREATE INDEX idx_submission_status ON submission(status);
CREATE INDEX idx_submission_created_at ON submission(created_at DESC);

-- ============================================
-- SUBMISSION TIMELINE
-- ============================================
CREATE TABLE submission_timeline (
    id              BIGSERIAL PRIMARY KEY,
    submission_id   UUID NOT NULL REFERENCES submission(id) ON DELETE CASCADE,
    event           VARCHAR(100) NOT NULL,
    timestamp       TIMESTAMP NOT NULL DEFAULT NOW(),
    metadata        JSONB                                          -- optional: {"testCaseId": 1}
);

CREATE INDEX idx_timeline_submission_id ON submission_timeline(submission_id);
