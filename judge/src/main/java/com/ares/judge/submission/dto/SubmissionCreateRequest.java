package com.ares.judge.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubmissionCreateRequest(
        @NotNull(message = "problem id is required")UUID problemId,
        String language,
        @NotBlank(message = "code is required") String code
        ) {}
