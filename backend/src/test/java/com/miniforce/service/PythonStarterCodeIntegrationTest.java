package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.judge.TestCaseRequest;
import com.miniforce.model.SubmissionStatus;
import com.miniforce.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PythonStarterCodeIntegrationTest {

    @Autowired
    private DockerRealJudgeAdapter realJudgeAdapter;

    @Autowired
    private ProblemRepository problemRepository;

    private ProblemContract mockContractForProblem(long problemId) {
        if (problemId == 1) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(1L, "[2,7,11,15];9", "[0,1]")));
        if (problemId == 2) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(2L, "()", "true")));
        if (problemId == 3) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(3L, "abcabcbb", "3")));
        if (problemId == 8) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(8L, "[7,1,5,3,6,4]", "5")));
        if (problemId == 12) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(12L, "2", "2")));
        if (problemId == 13) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(13L, "[1,2,3,4]", "[24,12,8,6]")));
        if (problemId == 14) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(14L, "[-2,1,-3,4,-1,2,1,-5,4]", "6")));
        if (problemId == 20) return new ProblemContract(null, 2000, 256, 128, List.of(new TestCaseRequest(20L, "[100,4,200,1,3,2]", "4")));
        return new ProblemContract(null, 2000, 256, 128, List.of());
    }

    @Test
    public void testEmptyStarterCode() {
        Map<Long, String> starters = Map.of(
            1L, "class Solution:\n    def twoSum(self, nums, target):\n        # Write your code here\n        pass\n",
            2L, "class Solution:\n    def isValid(self, s):\n        # Write your code here\n        pass\n",
            3L, "class Solution:\n    def lengthOfLongestSubstring(self, s):\n        # Write your code here\n        pass\n",
            8L, "class Solution:\n    def maxProfit(self, prices):\n        # Write your code here\n        pass\n",
            12L, "class Solution:\n    def climbStairs(self, n):\n        # Write your code here\n        pass\n",
            13L, "class Solution:\n    def productExceptSelf(self, nums):\n        # Write your code here\n        pass\n",
            14L, "class Solution:\n    def maxSubArray(self, nums):\n        # Write your code here\n        pass\n",
            20L, "class Solution:\n    def longestConsecutive(self, nums):\n        # Write your code here\n        pass\n"
        );

        for (Map.Entry<Long, String> entry : starters.entrySet()) {
            long pid = entry.getKey();
            String starter = entry.getValue();
            assertThat(starter).isNotNull();
            assertThat(starter).contains("def ");

            ExecutionRequest req = new ExecutionRequest(
                1000L + pid,
                pid,
                "PYTHON_3",
                starter,
                mockContractForProblem(pid)
            );
            
            ExecutionResult res = realJudgeAdapter.execute(req);
            
            // Starter code is syntactically valid but WRONG_ANSWER
            // Verify it didn't syntax error (COMPILATION_ERROR)
            assertThat(res.status()).isNotEqualTo(SubmissionStatus.SYSTEM_ERROR);
            assertThat(res.status()).isNotEqualTo(SubmissionStatus.COMPILATION_ERROR);
        }
    }

    @Test
    public void testKnownCorrectSolutions() {
        Map<Long, String> correctSolutions = Map.of(
            1L, "import json\nclass Solution:\n    def twoSum(self, nums, target):\n        m = {}\n        for i, v in enumerate(nums):\n            if target - v in m:\n                return [m[target - v], i]\n            m[v] = i\n        return []",
            2L, "class Solution:\n    def isValid(self, s):\n        stack = []\n        m = {')':'(', '}':'{', ']':'['}\n        for c in s:\n            if c in m:\n                if not stack or stack.pop() != m[c]: return False\n            else:\n                stack.append(c)\n        return not stack",
            3L, "class Solution:\n    def lengthOfLongestSubstring(self, s):\n        seen = {}\n        l = 0\n        res = 0\n        for r in range(len(s)):\n            if s[r] in seen:\n                l = max(l, seen[s[r]] + 1)\n            seen[s[r]] = r\n            res = max(res, r - l + 1)\n        return res",
            8L, "class Solution:\n    def maxProfit(self, prices):\n        min_p = float('inf')\n        max_p = 0\n        for p in prices:\n            min_p = min(min_p, p)\n            max_p = max(max_p, p - min_p)\n        return max_p",
            12L, "class Solution:\n    def climbStairs(self, n):\n        a, b = 1, 1\n        for _ in range(n):\n            a, b = b, a + b\n        return a",
            13L, "class Solution:\n    def productExceptSelf(self, nums):\n        res = [1]*len(nums)\n        p = 1\n        for i in range(len(nums)):\n            res[i] = p\n            p *= nums[i]\n        p = 1\n        for i in range(len(nums)-1, -1, -1):\n            res[i] *= p\n            p *= nums[i]\n        return res",
            14L, "class Solution:\n    def maxSubArray(self, nums):\n        cur = mx = nums[0]\n        for x in nums[1:]:\n            cur = max(x, cur + x)\n            mx = max(mx, cur)\n        return mx",
            20L, "class Solution:\n    def longestConsecutive(self, nums):\n        s = set(nums)\n        res = 0\n        for x in s:\n            if x - 1 not in s:\n                y = x + 1\n                while y in s:\n                    y += 1\n                res = max(res, y - x)\n        return res"
        );

        for (Map.Entry<Long, String> entry : correctSolutions.entrySet()) {
            ExecutionRequest req = new ExecutionRequest(
                2000L + entry.getKey(),
                entry.getKey(),
                "PYTHON_3",
                entry.getValue(),
                mockContractForProblem(entry.getKey())
            );
            
            ExecutionResult res = realJudgeAdapter.execute(req);
            
            assertThat(res.status()).isEqualTo(SubmissionStatus.ACCEPTED);
        }
    }
}
