package com.ares.judge.testcase.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResponse {

    private Long id;
    private String input;
    private String expectedOutput;
    private UUID problemId;
    private Boolean hidden;
    private LocalDateTime createdAt;
}