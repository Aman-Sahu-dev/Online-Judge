package com.ares.judge.submission.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record TimelineEvent(
        String event,
        LocalDateTime timestamp,
        Map<String, Object> metadata
) {}