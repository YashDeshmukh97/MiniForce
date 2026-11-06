package com.miniforce.service;

import com.miniforce.repository.ProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Order(1)
public class DatabaseBaselineInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBaselineInitializer.class);

    private final DataSource dataSource;
    private final ProblemRepository problemRepository;

    public DatabaseBaselineInitializer(DataSource dataSource, ProblemRepository problemRepository) {
        this.dataSource = dataSource;
        this.problemRepository = problemRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            long count = problemRepository.count();
            if (count == 0) {
                log.info("Problems catalog is empty. Applying baseline seed from miniforce-seed.sql...");
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("miniforce-seed.sql"));
                populator.setContinueOnError(true);
                populator.execute(dataSource);
                log.info("Baseline seed completed successfully. Total problems: {}", problemRepository.count());
            } else {
                log.info("Database baseline already initialized with {} problems.", count);
            }
        } catch (Exception e) {
            log.warn("Database baseline check/initialization notice: {}", e.getMessage());
        }
    }
}
