package com.miniforce.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "problem_hints",
    indexes = {
        @Index(name = "idx_problem_hints_problem_level", columnList = "problem_id, hint_level", unique = true)
    }
)
public class ProblemHint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "hint_level", nullable = false)
    private int hintLevel;

    @Column(name = "hint_text", nullable = false, columnDefinition = "TEXT")
    private String hintText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public ProblemHint() {}

    public ProblemHint(Long problemId, int hintLevel, String hintText) {
        this.problemId = problemId;
        this.hintLevel = hintLevel;
        this.hintText = hintText;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public int getHintLevel() { return hintLevel; }
    public void setHintLevel(int hintLevel) { this.hintLevel = hintLevel; }

    public String getHintText() { return hintText; }
    public void setHintText(String hintText) { this.hintText = hintText; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
