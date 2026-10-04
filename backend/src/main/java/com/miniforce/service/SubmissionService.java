package com.miniforce.service;
import com.miniforce.dto.SubmissionDtos.*; import com.miniforce.exception.*; import com.miniforce.model.*; import com.miniforce.repository.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*; import java.time.*;

@Service public class SubmissionService {
 private final SubmissionRepository submissions;private final ProblemService problems;private final TestCaseRepository cases;private final ContestRepository contests;private final ContestProblemRepository contestProblems;private final ContestParticipantRepository participants;private final SafeJavaDemoJudge judge;
 public SubmissionService(SubmissionRepository s,ProblemService p,TestCaseRepository t,ContestRepository c,ContestProblemRepository cp,ContestParticipantRepository pa,SafeJavaDemoJudge j){submissions=s;problems=p;cases=t;contests=c;contestProblems=cp;participants=pa;judge=j;}
  public void applySuccessEffects(Submission s) {
      User user = s.getUser();
      Problem problem = s.getProblem();
      Contest finalContest = s.getContest();
      boolean firstAccepted = !submissions.existsByUserIdAndProblemIdAndStatusAndIdNot(user.getId(), problem.getId(), SubmissionStatus.ACCEPTED, s.getId());
      boolean firstAcceptedInContest = finalContest != null && !submissions.existsByUserIdAndProblemIdAndStatusAndContestIdAndIdNot(user.getId(), problem.getId(), SubmissionStatus.ACCEPTED, finalContest.getId(), s.getId());
      Submission lastAccepted = submissions.findFirstByUserIdAndStatusAndIdNotOrderBySubmittedAtDesc(user.getId(), SubmissionStatus.ACCEPTED, s.getId()).orElse(null);

      if (firstAccepted) {
          user.setSolvedCount(user.getSolvedCount() + 1);
      }
      LocalDate today = LocalDate.now(ZoneOffset.UTC);
      if (lastAccepted != null) {
          LocalDate lastDate = lastAccepted.getSubmittedAt().atZone(ZoneOffset.UTC).toLocalDate();
          if (lastDate.equals(today.minusDays(1))) {
              user.setStreak(user.getStreak() + 1);
          } else if (lastDate.isBefore(today.minusDays(1))) {
              user.setStreak(1);
          }
      } else {
          user.setStreak(1);
      }
      if (finalContest != null && finalContest.getStatus() == ContestStatus.LIVE) {
          if (firstAcceptedInContest) {
              participants.findByContestIdAndUserId(finalContest.getId(), user.getId()).ifPresent(p -> {
                  boolean isListed = contestProblems.findByContestId(finalContest.getId()).stream().anyMatch(cp -> cp.getProblem().getId().equals(problem.getId()));
                  if (isListed) {
                      int points = contestProblems.findByContestId(finalContest.getId()).stream().filter(cp -> cp.getProblem().getId().equals(problem.getId())).findFirst().map(ContestProblem::getPoints).orElse(0);
                      p.setScore(p.getScore() + points);
                  }
              });
          }
      }
  }

  @Transactional public SubmissionResponse submit(User user,SubmissionRequest request){Problem problem=problems.entity(request.problemId()); Contest contest=null;if(request.contestId()!=null){contest=contests.findById(request.contestId()).orElseThrow(()->new NotFoundException("Contest not found"));if(!participants.existsByContestIdAndUserId(contest.getId(),user.getId()))throw new ConflictException("Join the contest before submitting to it");if(contest.getStatus()!=ContestStatus.LIVE)throw new ConflictException("Contest is not currently live");}
   var verdict=judge.judge(problem,request.language(),request.code(),cases.findByProblemId(problem.getId()));Submission s=new Submission();s.setUser(user);s.setProblem(problem);s.setContest(contest);s.setLanguage(request.language());s.setCode(request.code());s.setStatus(verdict.status());s.setRuntimeMs(verdict.runtimeMs());
   submissions.save(s);
   if(verdict.status()==SubmissionStatus.ACCEPTED){ applySuccessEffects(s); }
   return SubmissionResponse.from(s);
  }

  @Transactional public SubmissionResponse submitAsync(User user,SubmissionRequest request){
  Problem problem=problems.entity(request.problemId()); 
  Contest contest=null;
  if(request.contestId()!=null){
    contest=contests.findById(request.contestId()).orElseThrow(()->new NotFoundException("Contest not found"));
    if(!participants.existsByContestIdAndUserId(contest.getId(),user.getId()))throw new ConflictException("Join the contest before submitting to it");
    if(contest.getStatus()!=ContestStatus.LIVE)throw new ConflictException("Contest is not currently live");
  }
  Submission s=new Submission();
  s.setUser(user);
  s.setProblem(problem);
  s.setContest(contest);
  s.setLanguage(request.language());
  s.setCode(request.code());
  s.setStatus(SubmissionStatus.QUEUED);
  s.setRetryCount(0);
  submissions.save(s);
  return SubmissionResponse.from(s);
 }

 @Transactional(readOnly=true) public SubmissionResponse run(User user,SubmissionRequest request){Problem problem=problems.entity(request.problemId());List<TestCase> sampleCases=cases.findByProblemId(problem.getId()).stream().filter(TestCase::isSampleCase).toList();var verdict=judge.judge(problem,request.language(),request.code(),sampleCases);return new SubmissionResponse(null,problem.getId(),problem.getTitle(),user.getId(),user.getUsername(),null,request.language(),verdict.status(),verdict.runtimeMs(),Instant.now(),null,null,null);}
 @Transactional(readOnly=true) public List<SubmissionResponse> all(User user){return submissions.findByUserIdOrderBySubmittedAtDesc(user.getId()).stream().map(SubmissionResponse::from).toList();} 
 @Transactional(readOnly=true) public List<SubmissionResponse> forProblem(Long id,User viewer){problems.entity(id);return submissions.findByProblemIdOrderBySubmittedAtDesc(id).stream().filter(s->s.getUser().getId().equals(viewer.getId())||viewer.getRole()==Role.ADMIN).map(SubmissionResponse::from).toList();}
 
 @Transactional(readOnly=true) public SubmissionResponse getById(Long id, User viewer){
    Submission s = submissions.findById(id).orElseThrow(() -> new NotFoundException("Submission not found"));
    if (!s.getUser().getId().equals(viewer.getId()) && viewer.getRole() != Role.ADMIN) {
        throw new UnauthorizedException("Not authorized");
    }
    return SubmissionResponse.from(s);
 }
}
