package com.ares.judge.problem;

import com.ares.judge.problem.Problem;
import com.ares.judge.problem.ProblemRepository;
import com.ares.judge.problem.dto.ProblemCreateRequest;
import com.ares.judge.problem.dto.ProblemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemResponse create(ProblemCreateRequest request) {
        Problem problem = Problem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .timeLimitMs(request.getTimeLimitMs() != null ? request.getTimeLimitMs() : 5000)
                .memoryLimitMb(request.getMemoryLimitMb() != null ? request.getMemoryLimitMb() : 512)
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "EASY")
                .build();

        problem = problemRepository.save(problem);
        return toResponse(problem);
    }

    public ProblemResponse getById(UUID id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + id));
        return toResponse(problem);
    }

    public List<ProblemResponse> getAll() {
        return problemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private ProblemResponse toResponse(Problem problem) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .timeLimitMs(problem.getTimeLimitMs())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .difficulty(problem.getDifficulty())
                .createdAt(problem.getCreatedAt())
                .build();
    }
}