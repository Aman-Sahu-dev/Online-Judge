package com.ares.judge.testcase;

import com.ares.judge.testcase.dto.TestCaseCreateRequest;
import com.ares.judge.testcase.dto.TestCaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/problems/{problemId}/test-cases")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;

    @PostMapping
    public ResponseEntity<TestCaseResponse> create(
            @PathVariable UUID problemId,
            @Valid @RequestBody TestCaseCreateRequest request) {
        TestCaseResponse response = testCaseService.create(problemId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TestCaseResponse>> getByProblemId(@PathVariable UUID problemId) {
        return ResponseEntity.ok(testCaseService.getByProblemId(problemId));
    }
}