package com.ares.judge.submission;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SubmissionTimelineRepository extends JpaRepository<SubmissionTimeline, Long> {
    List<SubmissionTimeline> findBySubmissionIdOrderByTimestampAsc(UUID submissionId);
}