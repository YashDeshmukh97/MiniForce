
package com.miniforce.service;

import com.miniforce.judge.pilot.*;
import com.miniforce.judge.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
public class DebugJudgeTest {
    @Autowired DockerPilotJudge pilot;

    @Test
    public void test() {
        String code = "class Solution { public int climbStairs(int n) { return x; } }";
        PilotResult res = pilot.executeTestCase(
            "JAVA_17",
            code,
            "2",
            "2",
            new SingleIntInputCodec(),
            ClimbStairsHarnessGenerator.generate(),
            new IntegerExactComparator()
        );
        System.out.println("====== VERDICT: " + res.verdict());
        System.out.println("====== ACTUAL: " + res.actualOutput());
    }
}

