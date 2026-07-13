package com.ares.judge.submission;

public enum SubmissionStatus {
    PENDING,
    QUEUED,
    COMPILING,
    RUNNING,
    CHECKING,
    ACCEPTED,
    WRONG_ANSWER,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    RUNTIME_ERROR,
    COMPILATION_ERROR,
    SYSTEM_ERROR
}
