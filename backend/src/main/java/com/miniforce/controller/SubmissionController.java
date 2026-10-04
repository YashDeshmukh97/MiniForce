package com.miniforce.controller;
import com.miniforce.dto.SubmissionDtos.*; import com.miniforce.service.*; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;

@RestController @RequestMapping("/api/submissions") public class SubmissionController {
    private final SubmissionService submissions;
    private final CurrentUser current;
    public SubmissionController(SubmissionService s,CurrentUser c){submissions=s;current=c;}
    @PostMapping ResponseEntity<SubmissionResponse> submit(@Valid @RequestBody SubmissionRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(submissions.submit(current.require(),r));}
    @PostMapping("/async") ResponseEntity<SubmissionResponse> submitAsync(@Valid @RequestBody SubmissionRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(submissions.submitAsync(current.require(),r));}
    @PostMapping("/run") ResponseEntity<SubmissionResponse> run(@Valid @RequestBody SubmissionRequest r){return ResponseEntity.ok(submissions.run(current.require(),r));}
    @GetMapping List<SubmissionResponse> all(){return submissions.all(current.require());}
    @GetMapping("/{id}") ResponseEntity<SubmissionResponse> getSubmission(@PathVariable Long id){return ResponseEntity.ok(submissions.getById(id, current.require()));}
}
