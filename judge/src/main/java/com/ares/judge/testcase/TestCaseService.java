package com.ares.judge.testcase;
import com.ares.judge.problem.ProblemRepository;
import com.ares.judge.testcase.dto.TestCaseCreateRequest;
import com.ares.judge.testcase.dto.TestCaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ProblemRepository problemRepository;

    public TestCaseResponse create(UUID problemId, TestCaseCreateRequest request) {
        problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + problemId));

        TestCase testCase = TestCase.builder()
                .problemId(problemId)
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .hidden(request.getHidden() != null ? request.getHidden() : false)
                .build();

        testCase = testCaseRepository.save(testCase);
        return toResponse(testCase);
    }

    public List<TestCaseResponse> getByProblemId(UUID problemId) {
        problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + problemId));

        return testCaseRepository.findByProblemIdOrderByCreatedAtAsc(problemId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TestCaseResponse toResponse(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .problemId(testCase.getProblemId())
                .hidden(testCase.getHidden())
                .createdAt(testCase.getCreatedAt())
                .build();
    }
}