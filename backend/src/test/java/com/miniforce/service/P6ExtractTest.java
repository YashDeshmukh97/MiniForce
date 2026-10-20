package com.miniforce.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
public class P6ExtractTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void extractP6Data() {
        System.out.println("=== P6 DATA EXTRACTION ===");
        List<Map<String, Object>> problems = jdbcTemplate.queryForList(
                "SELECT id, title, starter_code FROM problems WHERE id BETWEEN 41 AND 50"
        );
        for (Map<String, Object> p : problems) {
            System.out.println("ID: " + p.get("id") + " - " + p.get("title"));
            System.out.println("Starter code:");
            System.out.println(p.get("starter_code"));
            
            List<Map<String, Object>> tests = jdbcTemplate.queryForList(
                    "SELECT id, input_data, expected_output FROM test_cases WHERE problem_id = ?",
                    p.get("id")
            );
            System.out.println("Test cases (" + tests.size() + "):");
            for (Map<String, Object> t : tests) {
                System.out.println("  TC ID " + t.get("id") + ": input = " + t.get("input_data") + " | expected = " + t.get("expected_output"));
            }
            System.out.println("----------------------------------------");
        }
    }
}
