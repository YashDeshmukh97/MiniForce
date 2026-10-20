package com.miniforce.service;

import com.miniforce.model.ProblemHint;
import com.miniforce.repository.ProblemHintRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HintDataInitializer implements ApplicationRunner {

    private final ProblemHintRepository problemHintRepository;

    public HintDataInitializer(ProblemHintRepository problemHintRepository) {
        this.problemHintRepository = problemHintRepository;
    }

    @Configuration
    public static class HintSchemaBootstrapConfig {
        @Bean
        public static BeanPostProcessor hintTableBootstrapProcessor() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                    if (bean instanceof DataSource dataSource) {
                        try (Connection conn = dataSource.getConnection();
                             Statement stmt = conn.createStatement()) {
                            stmt.execute("""
                                CREATE TABLE IF NOT EXISTS problem_hints (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  problem_id BIGINT NOT NULL,
                                  hint_level INT NOT NULL,
                                  hint_text TEXT NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                )
                                """);
                        } catch (Exception ignored) {
                            // Ignore if table already exists or managed by Hibernate
                        }
                    }
                    return bean;
                }
            };
        }
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensureAllHintsSeeded();
    }

    @Transactional
    public void ensureAllHintsSeeded() {
        if (problemHintRepository.count() >= 150) {
            return;
        }
        List<ProblemHint> toSave = new ArrayList<>();
        for (Map.Entry<Long, List<ProblemHint>> entry : HintCatalog.getAllHints().entrySet()) {
            Long problemId = entry.getKey();
            for (ProblemHint template : entry.getValue()) {
                if (!problemHintRepository.existsByProblemIdAndHintLevel(problemId, template.getHintLevel())) {
                    toSave.add(new ProblemHint(problemId, template.getHintLevel(), template.getHintText()));
                }
            }
        }
        if (!toSave.isEmpty()) {
            problemHintRepository.saveAll(toSave);
        }
    }
}
