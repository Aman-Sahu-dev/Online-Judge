package com.ares.judge.testcase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase,Long> {
    List<TestCase> findByProblemIdOrderByCreatedAtAsc(UUID problemId);
}
