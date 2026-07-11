package com.ares.judge.problem;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "problem")
public class Problem {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid",updatable = false,nullable = false)
    private UUID id;
    @Column(nullable = false,length = 255)
    private String title;

    @Column(nullable = false,columnDefinition = "Text")
    private String description;

    @Column(nullable = false,name = "time_limit_ms")
    @Builder.Default
    private Integer timeLimitMs = 5000;

    @Column(name = "memeory_limit_mb",nullable = false)
    @Builder.Default
    private Integer memoryLimitMb = 512;

    @Column(name = "difficulty",length = 50)
    @Builder.Default
    private String difficulty = "EASY";

    @Column(name = "created_at",nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();


}
