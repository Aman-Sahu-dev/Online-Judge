package com.ares.judge.problem.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemResponse {

    private UUID id;
    private String title;
    private String description;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
    private String difficulty;
    private LocalDateTime createdAt;
}