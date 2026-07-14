package com.ares.judge.submission;

import com.ares.judge.problem.ProblemRepository;
import com.ares.judge.submission.dto.SubmissionCreateRequest;
import com.ares.judge.submission.dto.SubmissionResponse;
import com.ares.judge.submission.dto.TimelineEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionTimelineRepository timelineRepository;
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
    public SubmissionResponse getById(UUID id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found: " + id));

        List<SubmissionTimeline> timelineEntities = timelineRepository.findBySubmissionIdOrderByTimestampAsc(id);

        return toResponse(submission, timelineEntities);
    }

    private SubmissionResponse toResponse(Submission submission, List<SubmissionTimeline> timelineEntities) {
        List<TimelineEvent> timeline = timelineEntities.stream()
                .map(t -> new TimelineEvent(t.getEvent(), t.getTimestamp(), t.getMetadata()))
                .toList();

        return new SubmissionResponse(
                submission.getId(),
                submission.getProblemId(),
                submission.getLanguage(),
                submission.getStatus().name(),
                submission.getTraceId(),
                submission.getExecutionTime(),
                submission.getMemoryUsed(),
                submission.getCreatedAt(),
                timeline
        );
    }
    private SubmissionResponse toResponse(Submission submission) {
        return toResponse(submission, List.of());
    }
}