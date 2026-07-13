package com.ares.judge.submission;

import com.ares.judge.problem.ProblemRepository;
import com.ares.judge.submission.dto.SubmissionCreateRequest;
import com.ares.judge.submission.dto.SubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;

    public SubmissionResponse submit(SubmissionCreateRequest request){
        problemRepository.findById(request.problemId()).orElseThrow(()-> new RuntimeException("problem not found"));
        Submission submission = Submission.builder()
                .problemId(request.problemId())
                .language(request.language() != null ?request.language():"rust")
                .code(request.code())
                .status(SubmissionStatus.PENDING)
                .traceId(UUID.randomUUID())
                .build();
        submissionRepository.save(submission);
        return toResponse(submission);
    }
    private SubmissionResponse toResponse(Submission submission){
        return new SubmissionResponse(
                submission.getId(),
                submission.getProblemId(),
                submission.getLanguage(),
                submission.getStatus().name(),
                submission.getTraceId(),
                submission.getExecutionTime(),
                submission.getMemoryUsed(),
                submission.getCreatedAt()
        );
    }
}
