package com.miniforce.service;

import com.miniforce.model.*;
import java.util.*;
import org.springframework.stereotype.Component;

/**
 * Deliberately non-executing classroom/demo judge.
 *
 * <p>It never compiles or executes submitted Java, reads no test-case input or
 * expected output, and must not be represented as a production judge. Verdicts
 * are deterministic source-pattern matches only. The returned runtime is a
 * synthetic display value derived from source length, not measured execution time.</p>
 */
@Component
public class SafeJavaDemoJudge {
 public record Verdict(SubmissionStatus status,Integer runtimeMs,int passed,int total,String message){}

 public Verdict judge(Problem problem,String language,String code,List<TestCase> cases){
  if(!"JAVA_17".equals(language)) return new Verdict(SubmissionStatus.COMPILATION_ERROR,null,0,cases.size(),"Only Java 17 source is supported by the non-executing MiniForce demo judge.");
  String compact=code.replaceAll("\\s+","");
  if(!compact.contains("classSolution")||code.length()<40) return new Verdict(SubmissionStatus.COMPILATION_ERROR,null,0,cases.size(),"Demo pattern check expects a Java class named Solution; no compilation was attempted.");
  boolean accepted=switch(problem.getSlug()) {
   case "two-sum" -> compact.contains("twoSum(int[]nums,inttarget)") && (compact.contains("HashMap")||compact.contains("Map<")) && compact.contains("returnnewint[]{");
   case "valid-parentheses" -> compact.contains("isValid(Strings)") && (compact.contains("Deque<")||compact.contains("Stack<")) && compact.contains("returntrue");
   case "longest-substring-without-repeating-characters" -> compact.contains("lengthOfLongestSubstring(Strings)") && (compact.contains("HashMap")||compact.contains("Map<")) && compact.contains("Math.max");
   default -> code.contains("// MINIFORCE: ACCEPT");
  };
  if(accepted) return new Verdict(SubmissionStatus.SYSTEM_ERROR,12+(code.length()%37),cases.size(),cases.size(),"Run mode is disabled to prevent false verdicts. Use 'Submit' for actual evaluation.");
  return new Verdict(SubmissionStatus.SYSTEM_ERROR,9+(code.length()%19),0,cases.size(),"Run mode is disabled to prevent false verdicts. Use 'Submit' for actual evaluation.");
 }
}
