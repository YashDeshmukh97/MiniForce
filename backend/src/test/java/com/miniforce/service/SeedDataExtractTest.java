package com.miniforce.service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest
@ActiveProfiles("test")
public class SeedDataExtractTest {
    @Autowired private JdbcTemplate jdbcTemplate;
    @Test
    public void extract() {
        System.out.println("=== INSERTING MISSING TEST CASES ===");
        try {
            jdbcTemplate.update("INSERT INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES (2027, 16, '\"anagram\"; \"nagaram\"', 'true', true)");
            jdbcTemplate.update("INSERT INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES (2028, 16, '\"rat\"; \"car\"', 'false', true)");
            jdbcTemplate.update("INSERT INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES (2029, 16, '\"a\"; \"ab\"', 'false', false)");
            jdbcTemplate.update("INSERT INTO test_cases(id,problem_id,input_data,expected_output,sample_case) VALUES (2030, 17, '[\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"]', '[[\"bat\"],[\"nat\",\"tan\"],[\"ate\",\"eat\",\"tea\"]]', true)");
        } catch(Exception e) {
            System.out.println("Already inserted or error: " + e.getMessage());
        }
        System.out.println("=== DONE ===");
    }
}
