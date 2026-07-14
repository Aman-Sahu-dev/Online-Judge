package com.ares.judge.submission.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        UUID problemId,
        String language,
        String status,
        UUID traceId,
        Integer executionTime,
        Integer memoryUsed,
        LocalDateTime createdAt,
        List<TimelineEvent> timeline
) {}