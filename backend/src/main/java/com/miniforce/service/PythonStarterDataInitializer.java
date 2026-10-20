package com.miniforce.service;

import com.miniforce.model.Problem;
import com.miniforce.repository.ProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PythonStarterDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PythonStarterDataInitializer.class);

    private final ProblemRepository problemRepository;

    public PythonStarterDataInitializer(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensurePythonStartersSeeded();
    }

    @Transactional
    public void ensurePythonStartersSeeded() {
        Map<Long, String> allStarters = PythonStarterCatalog.getAllStarters();
        List<Problem> toUpdate = new ArrayList<>();

        for (Map.Entry<Long, String> entry : allStarters.entrySet()) {
            Long problemId = entry.getKey();
            String expectedStarter = entry.getValue();

            Optional<Problem> opt = problemRepository.findById(problemId);
            if (opt.isPresent()) {
                Problem p = opt.get();
                if (p.getStarterCodePython() == null || p.getStarterCodePython().isBlank()) {
                    p.setStarterCodePython(expectedStarter);
                    toUpdate.add(p);
                }
            }
        }

        if (!toUpdate.isEmpty()) {
            problemRepository.saveAll(toUpdate);
            log.info("Initialized Python starter code for {} public problems", toUpdate.size());
        }
    }
}
