package com.ares.judge.problem;

import com.ares.judge.problem.dto.ProblemCreateRequest;
import com.ares.judge.problem.dto.ProblemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<ProblemResponse> create(@Valid @RequestBody ProblemCreateRequest request) {
        ProblemResponse response = problemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getAll() {
        return ResponseEntity.ok(problemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(problemService.getById(id));
    }
}