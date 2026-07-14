package com.ares.judge.submission;

import com.ares.judge.submission.dto.SubmissionCreateRequest;
import com.ares.judge.submission.dto.SubmissionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(@Valid @RequestBody SubmissionCreateRequest request) {
        SubmissionResponse response = submissionService.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(submissionService.getById(id));
    }
}
