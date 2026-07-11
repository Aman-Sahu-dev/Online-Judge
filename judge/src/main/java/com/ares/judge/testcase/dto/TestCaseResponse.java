package com.ares.judge.testcase.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResponse {

    private Long id;
    private String input;
    private String expectedOutput;
    private Boolean hidden;
    private LocalDateTime createdAt;
}