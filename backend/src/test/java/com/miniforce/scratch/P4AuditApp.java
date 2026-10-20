package com.miniforce.scratch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@SpringBootTest(classes = com.miniforce.MiniForceApplication.class)
@ActiveProfiles("test")
public class P4AuditApp {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void runAudit() throws Exception {
        System.out.println("====== P4 AUDIT START ======");
        PrintWriter out = new PrintWriter(new FileWriter("P4_Audit.txt"));

        // Audit Problems 21-30
        for (long pid = 21; pid <= 30; pid++) {
            List<Map<String, Object>> problems = jdbcTemplate.queryForList("SELECT * FROM problems WHERE id = ?", pid);
            if (problems.isEmpty()) {
                out.println("Problem " + pid + " IS MISSING!");
                continue;
            }
            Map<String, Object> p = problems.get(0);
            out.println("----------------------------------------");
            out.println("Problem ID: " + pid);
            out.println("Title: " + p.get("title"));
            out.println("Slug: " + p.get("slug"));
            out.println("Starter Code:");
            out.println(p.get("starter_code"));

            List<Map<String, Object>> tcs = jdbcTemplate.queryForList("SELECT * FROM test_cases WHERE problem_id = ?", pid);
            out.println("Test Cases: " + tcs.size());
            for (Map<String, Object> tc : tcs) {
                out.println("  ID=" + tc.get("id") + " Sample=" + tc.get("sample_case"));
                out.println("  In:  " + tc.get("input_data"));
                out.println("  Out: " + tc.get("expected_output"));
            }
        }
        
        out.println("----------------------------------------");
        out.println("Looking for duplicate test case IDs across ALL problems:");
        List<Map<String, Object>> dupTcs = jdbcTemplate.queryForList(
            "SELECT id, COUNT(*) as c FROM test_cases GROUP BY id HAVING COUNT(*) > 1"
        );
        if (dupTcs.isEmpty()) {
            out.println("No duplicate test case IDs found.");
        } else {
            for (Map<String, Object> d : dupTcs) {
                out.println("DUPLICATE TC ID: " + d.get("id") + " (Count: " + d.get("c") + ")");
            }
        }
        
        out.close();
        System.out.println("====== P4 AUDIT END ======");
    }
}
