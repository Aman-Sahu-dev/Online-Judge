package com.ares.judge.testcase;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "test_Case")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id",nullable = false)
    private UUID problemId;

    @Column(name = "input",columnDefinition = "Text")
    private String input;

    @Column(name = "expected_output",nullable = false,columnDefinition = "Text")
    private String ExpectedOutput;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hidden = false;

    @Column(name = "created_at",nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAT = LocalDateTime.now();
}

