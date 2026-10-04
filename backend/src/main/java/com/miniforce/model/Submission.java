package com.miniforce.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="submissions",indexes={@Index(name="idx_submission_user",columnList="user_id"),@Index(name="idx_submission_problem",columnList="problem_id"),@Index(name="idx_submission_time",columnList="submitted_at")}) public class Submission {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="user_id") private User user; @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="problem_id") private Problem problem; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contest_id") private Contest contest;
 @Column(nullable=false,length=20) private String language; @Column(nullable=false,columnDefinition="MEDIUMTEXT") private String code; @Enumerated(EnumType.STRING) @Column(nullable=false,length=25) private SubmissionStatus status; @Column(name="runtime_ms") private Integer runtimeMs; @Column(name="submitted_at",nullable=false) private Instant submittedAt=Instant.now();
 @Column(name="claimed_by", length=100) private String claimedBy; @Column(name="claimed_at") private Instant claimedAt; @Column(name="lease_expires_at") private Instant leaseExpiresAt; @Column(name="retry_count", nullable=false) private Integer retryCount = 0; @Column(name="memory_kb") private Integer memoryKb; @Column(name="compiler_output", columnDefinition="MEDIUMTEXT") private String compilerOutput;
    @Column(name = "failing_test_case_id")
    private Long failingTestCaseId;
    @Column(name = "runtime_error_output")
    private String runtimeErrorOutput;
    @Column(name = "system_error_message")
    private String systemErrorMessage;
 public Long getId(){return id;} public User getUser(){return user;} public void setUser(User v){user=v;} public Problem getProblem(){return problem;} public void setProblem(Problem v){problem=v;} public Contest getContest(){return contest;} public void setContest(Contest v){contest=v;} public String getLanguage(){return language;} public void setLanguage(String v){language=v;} public String getCode(){return code;} public void setCode(String v){code=v;} public SubmissionStatus getStatus(){return status;} public void setStatus(SubmissionStatus v){status=v;} public Integer getRuntimeMs(){return runtimeMs;} public void setRuntimeMs(Integer v){runtimeMs=v;} public Instant getSubmittedAt(){return submittedAt;}
 public String getClaimedBy(){return claimedBy;} public void setClaimedBy(String v){claimedBy=v;} public Instant getClaimedAt(){return claimedAt;} public void setClaimedAt(Instant v){claimedAt=v;} public Instant getLeaseExpiresAt(){return leaseExpiresAt;} public void setLeaseExpiresAt(Instant v){leaseExpiresAt=v;} public Integer getRetryCount(){return retryCount;} public void setRetryCount(Integer v){retryCount=v;} public Integer getMemoryKb(){return memoryKb;} public void setMemoryKb(Integer v){memoryKb=v;} public String getCompilerOutput(){return compilerOutput;}
    public Long getFailingTestCaseId() { return failingTestCaseId; }
    public void setFailingTestCaseId(Long id) { this.failingTestCaseId = id; }
    public String getRuntimeErrorOutput() { return runtimeErrorOutput; }
    public void setRuntimeErrorOutput(String s) { this.runtimeErrorOutput = s; }
    public String getSystemErrorMessage() { return systemErrorMessage; }
    public void setSystemErrorMessage(String s) { this.systemErrorMessage = s; } public void setCompilerOutput(String v){compilerOutput=v;}
}
