package com.miniforce.service;

import com.miniforce.model.Submission;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobClaimService {
    private final SubmissionRepository repo;
    private final SubmissionService submissionService;
    private final String workerId = UUID.randomUUID().toString();

    public JobClaimService(SubmissionRepository repo, SubmissionService submissionService) {
        this.repo = repo;
        this.submissionService = submissionService;
    }

    @Transactional
    public Optional<Submission> claimNextJob() {
        Instant now = Instant.now();
        Optional<Submission> next = repo.findNextJobToClaim(now);
        if (next.isPresent()) {
            Submission s = next.get();
            if (s.getRetryCount() != null && s.getRetryCount() >= 3) {
                // Too many retries, mark as SYSTEM_ERROR
                repo.claimJob(s.getId(), SubmissionStatus.SYSTEM_ERROR, workerId, now, now);
                return Optional.empty();
            }
            Instant expiresAt = now.plus(5, ChronoUnit.MINUTES);
            int updated = repo.claimJob(s.getId(), SubmissionStatus.COMPILING, workerId, now, expiresAt);
            if (updated > 0) {
                return repo.findById(s.getId());
            }
        }
        return Optional.empty();
    }

    @Transactional
    public void markTerminal(Long submissionId, com.miniforce.judge.ExecutionResult result) {
        repo.findById(submissionId).ifPresent(s -> {
            if (!workerId.equals(s.getClaimedBy())) {
                throw new IllegalStateException("Stale worker " + workerId + " attempted to update job claimed by " + s.getClaimedBy());
            }
            if (s.getStatus().isTerminal()) {
                // Already completed — do not overwrite. This protects against
                // double-write from JudgeWorker catch blocks or race conditions.
                return;
            }
            if (result.status() == SubmissionStatus.ACCEPTED) {
                submissionService.applySuccessEffects(s);
            }
            s.setStatus(result.status());
            s.setRuntimeMs(result.runtimeMs());
            s.setMemoryKb(result.memoryKb());
            s.setCompilerOutput(result.compilerOutput());
            s.setFailingTestCaseId(result.failingTestCaseId());
            s.setRuntimeErrorOutput(result.runtimeErrorOutput());
            s.setSystemErrorMessage(result.systemErrorMessage());
            s.setLeaseExpiresAt(null);
            repo.save(s);
        });
    }
}