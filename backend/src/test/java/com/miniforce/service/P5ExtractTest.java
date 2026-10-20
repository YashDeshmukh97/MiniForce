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
public class P5ExtractTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    public void dumpP5() {
        System.out.println("========== P5 DUMP ==========");
        List<Map<String, Object>> problems = jdbcTemplate.queryForList("SELECT id, title, starter_code FROM problems WHERE id BETWEEN 31 AND 40 ORDER BY id");
        for (Map<String, Object> p : problems) {
            long id = ((Number) p.get("id")).longValue();
            System.out.println("ID: " + id + " Title: " + p.get("title"));
            System.out.println("Starter: " + p.get("starter_code"));
            List<Map<String, Object>> tcs = jdbcTemplate.queryForList("SELECT id, input_data, expected_output, sample_case FROM test_cases WHERE problem_id = " + id + " ORDER BY id");
            for (Map<String, Object> tc : tcs) {
                System.out.println("  TC " + tc.get("id") + " (sample=" + tc.get("sample_case") + "): input=" + tc.get("input_data") + " | expected=" + tc.get("expected_output"));
            }
        }
        System.out.println("=============================");
    }
}
