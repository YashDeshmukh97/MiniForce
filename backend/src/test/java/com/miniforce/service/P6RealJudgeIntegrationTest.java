package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.model.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.miniforce.judge.ProblemContract;
import com.miniforce.judge.TestCaseRequest;
import java.util.List;
import java.util.Collections;

@SpringBootTest
@ActiveProfiles("test")
public class P6RealJudgeIntegrationTest {

    @Autowired
    private DockerRealJudgeAdapter realJudge;

    private ExecutionRequest buildReq(Long pid, String code, List<TestCaseRequest> testCases) {
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, testCases);
        return new ExecutionRequest(1L, pid, "java", code, contract);
    }

    @Test
    public void testProblem41_SlidingWindowMaximum() {
        String correctCode = "import java.util.*;\n" +
                "class Solution {\n" +
                "    public int[] maxSlidingWindow(int[] nums, int k) {\n" +
                "        if (nums == null || k <= 0) return new int[0];\n" +
                "        int n = nums.length;\n" +
                "        int[] res = new int[n - k + 1];\n" +
                "        Deque<Integer> q = new ArrayDeque<>();\n" +
                "        for (int i = 0; i < n; i++) {\n" +
                "            while (!q.isEmpty() && q.peek() < i - k + 1) q.poll();\n" +
                "            while (!q.isEmpty() && nums[q.peekLast()] < nums[i]) q.pollLast();\n" +
                "            q.offer(i);\n" +
                "            if (i >= k - 1) res[i - k + 1] = nums[q.peek()];\n" +
                "        }\n" +
                "        return res;\n" +
                "    }\n" +
                "}";
        
        var tcs = List.of(new TestCaseRequest(8L, "[1,3,-1,-3,5,3,6,7]; 3", "[3,3,5,5,6,7]"));
        ExecutionResult passRes = realJudge.execute(buildReq(41L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        
        ExecutionResult failRes = realJudge.execute(buildReq(41L, "class Solution { public int[] maxSlidingWindow(int[] nums, int k) { return new int[]{0}; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem42_AsteroidCollision() {
        String correctCode = "import java.util.*;\n" +
                "class Solution {\n" +
                "    public int[] asteroidCollision(int[] asteroids) {\n" +
                "        Stack<Integer> st = new Stack<>();\n" +
                "        for(int ast : asteroids){\n" +
                "            boolean destroyed = false;\n" +
                "            while(!st.isEmpty() && ast < 0 && st.peek() > 0){\n" +
                "                if(st.peek() < -ast){\n" +
                "                    st.pop();\n" +
                "                    continue;\n" +
                "                }else if(st.peek() == -ast){\n" +
                "                    st.pop();\n" +
                "                }\n" +
                "                destroyed = true;\n" +
                "                break;\n" +
                "            }\n" +
                "            if(!destroyed) st.push(ast);\n" +
                "        }\n" +
                "        int[] res = new int[st.size()];\n" +
                "        for(int i = res.length - 1; i >= 0; i--) res[i] = st.pop();\n" +
                "        return res;\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[5,10,-5]", "[5,10]"));
        ExecutionResult passRes = realJudge.execute(buildReq(42L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(42L, "class Solution { public int[] asteroidCollision(int[] asteroids) { return new int[]{0}; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem43_BinarySearch() {
        String correctCode = "class Solution {\n" +
                "    public int search(int[] nums, int target) {\n" +
                "        int l = 0, r = nums.length - 1;\n" +
                "        while(l <= r){\n" +
                "            int m = l + (r-l)/2;\n" +
                "            if(nums[m] == target) return m;\n" +
                "            if(nums[m] < target) l = m + 1;\n" +
                "            else r = m - 1;\n" +
                "        }\n" +
                "        return -1;\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[-1,0,3,5,9,12]; 9", "4"));
        ExecutionResult passRes = realJudge.execute(buildReq(43L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(43L, "class Solution { public int search(int[] nums, int target) { return -2; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem44_Search2DMatrix() {
        String correctCode = "class Solution {\n" +
                "    public boolean searchMatrix(int[][] matrix, int target) {\n" +
                "        if (matrix == null || matrix.length == 0) return false;\n" +
                "        int m = matrix.length, n = matrix[0].length;\n" +
                "        int l = 0, r = m * n - 1;\n" +
                "        while (l <= r) {\n" +
                "            int mid = l + (r - l) / 2;\n" +
                "            int val = matrix[mid / n][mid % n];\n" +
                "            if (val == target) return true;\n" +
                "            if (val < target) l = mid + 1;\n" +
                "            else r = mid - 1;\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[[1,3,5,7],[10,11,16,20],[23,30,34,60]]; 3", "true"));
        ExecutionResult passRes = realJudge.execute(buildReq(44L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(44L, "class Solution { public boolean searchMatrix(int[][] m, int t) { return false; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem45_KokoEatingBananas() {
        String correctCode = "class Solution {\n" +
                "    public int minEatingSpeed(int[] piles, int h) {\n" +
                "        int l = 1, r = 1000000000;\n" +
                "        while (l <= r) {\n" +
                "            int m = l + (r - l) / 2;\n" +
                "            if (canEatAll(piles, m, h)) r = m - 1;\n" +
                "            else l = m + 1;\n" +
                "        }\n" +
                "        return l;\n" +
                "    }\n" +
                "    private boolean canEatAll(int[] piles, int k, int h) {\n" +
                "        int count = 0;\n" +
                "        for (int p : piles) {\n" +
                "            count += (p + k - 1) / k;\n" +
                "        }\n" +
                "        return count <= h;\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[3,6,7,11]; 8", "4"));
        ExecutionResult passRes = realJudge.execute(buildReq(45L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(45L, "class Solution { public int minEatingSpeed(int[] p, int h) { return 0; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem46_FindMinInRotated() {
        String correctCode = "class Solution {\n" +
                "    public int findMin(int[] nums) {\n" +
                "        int l = 0, r = nums.length - 1;\n" +
                "        while (l < r) {\n" +
                "            int m = l + (r - l) / 2;\n" +
                "            if (nums[m] > nums[r]) l = m + 1;\n" +
                "            else r = m;\n" +
                "        }\n" +
                "        return nums[l];\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[3,4,5,1,2]", "1"));
        ExecutionResult passRes = realJudge.execute(buildReq(46L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(46L, "class Solution { public int findMin(int[] n) { return -1; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem47_SearchRotated() {
        String correctCode = "class Solution {\n" +
                "    public int search(int[] nums, int target) {\n" +
                "        int l = 0, r = nums.length - 1;\n" +
                "        while (l <= r) {\n" +
                "            int m = l + (r - l) / 2;\n" +
                "            if (nums[m] == target) return m;\n" +
                "            if (nums[l] <= nums[m]) {\n" +
                "                if (target >= nums[l] && target < nums[m]) r = m - 1;\n" +
                "                else l = m + 1;\n" +
                "            } else {\n" +
                "                if (target > nums[m] && target <= nums[r]) l = m + 1;\n" +
                "                else r = m - 1;\n" +
                "            }\n" +
                "        }\n" +
                "        return -1;\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[4,5,6,7,0,1,2]; 0", "4"));
        ExecutionResult passRes = realJudge.execute(buildReq(47L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(47L, "class Solution { public int search(int[] n, int t) { return -2; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem48_TimeBasedKeyValueStore() {
        String correctCode = "import java.util.*;\n" +
                "class TimeMap {\n" +
                "    private Map<String, TreeMap<Integer, String>> map;\n" +
                "    public TimeMap() {\n" +
                "        map = new HashMap<>();\n" +
                "    }\n" +
                "    public void set(String key, String value, int timestamp) {\n" +
                "        map.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);\n" +
                "    }\n" +
                "    public String get(String key, int timestamp) {\n" +
                "        if (!map.containsKey(key)) return \"\";\n" +
                "        Map.Entry<Integer, String> entry = map.get(key).floorEntry(timestamp);\n" +
                "        if (entry == null) return \"\";\n" +
                "        return entry.getValue();\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[\"TimeMap\", \"set\", \"get\", \"get\", \"set\", \"get\", \"get\"]; [[], [\"foo\", \"bar\", 1], [\"foo\", 1], [\"foo\", 3], [\"foo\", \"bar2\", 4], [\"foo\", 4], [\"foo\", 5]]", "[null,null,\"bar\",\"bar\",null,\"bar2\",\"bar2\"]"));
        ExecutionResult passRes = realJudge.execute(buildReq(48L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(48L, "class TimeMap { public TimeMap(){} public void set(String k, String v, int t){} public String get(String k, int t){return \"Wrong\";} }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem49_SplitArrayLargestSum() {
        String correctCode = "class Solution {\n" +
                "    public int splitArray(int[] nums, int k) {\n" +
                "        int l = 0, r = 0;\n" +
                "        for (int n : nums) {\n" +
                "            l = Math.max(l, n);\n" +
                "            r += n;\n" +
                "        }\n" +
                "        while (l < r) {\n" +
                "            int mid = l + (r - l) / 2;\n" +
                "            int sum = 0, pieces = 1;\n" +
                "            for (int n : nums) {\n" +
                "                if (sum + n > mid) {\n" +
                "                    sum = n;\n" +
                "                    pieces++;\n" +
                "                } else {\n" +
                "                    sum += n;\n" +
                "                }\n" +
                "            }\n" +
                "            if (pieces > k) l = mid + 1;\n" +
                "            else r = mid;\n" +
                "        }\n" +
                "        return l;\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[7,2,5,10,8]; 2", "18"));
        ExecutionResult passRes = realJudge.execute(buildReq(49L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(49L, "class Solution { public int splitArray(int[] n, int k) { return -1; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testProblem50_MergeTwoSortedLists() {
        String correctCode = "class Solution {\n" +
                "    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {\n" +
                "        ListNode dummy = new ListNode(0);\n" +
                "        ListNode curr = dummy;\n" +
                "        while (list1 != null && list2 != null) {\n" +
                "            if (list1.val <= list2.val) {\n" +
                "                curr.next = list1;\n" +
                "                list1 = list1.next;\n" +
                "            } else {\n" +
                "                curr.next = list2;\n" +
                "                list2 = list2.next;\n" +
                "            }\n" +
                "            curr = curr.next;\n" +
                "        }\n" +
                "        if (list1 != null) curr.next = list1;\n" +
                "        else curr.next = list2;\n" +
                "        return dummy.next;\n" +
                "    }\n" +
                "}";
        var tcs = List.of(new TestCaseRequest(8L, "[1,2,4]; [1,3,4]", "[1,1,2,3,4,4]"));
        ExecutionResult passRes = realJudge.execute(buildReq(50L, correctCode, tcs));
        assertEquals(SubmissionStatus.ACCEPTED, passRes.status());
        ExecutionResult failRes = realJudge.execute(buildReq(50L, "class Solution { public ListNode mergeTwoLists(ListNode a, ListNode b) { return null; } }", tcs));
        assertEquals(SubmissionStatus.WRONG_ANSWER, failRes.status());
    }

    @Test
    public void testFailureModes() {
        var tcs = List.of(new TestCaseRequest(8L, "[1,3,-1,-3,5,3,6,7]; 3", "[3,3,5,5,6,7]"));
        ExecutionResult ceRes = realJudge.execute(buildReq(41L, "class Solution { syntax error }", tcs));
        System.out.println("COMPILER OUT: " + ceRes.compilerOutput());
        System.out.println("SYS ERR: " + ceRes.systemErrorMessage());
        assertEquals(SubmissionStatus.COMPILATION_ERROR, ceRes.status());

        ExecutionResult reRes = realJudge.execute(buildReq(41L, "class Solution { public int[] maxSlidingWindow(int[] n, int k) { throw new RuntimeException(); } }", tcs));
        assertEquals(SubmissionStatus.RUNTIME_ERROR, reRes.status());

        ExecutionResult tleRes = realJudge.execute(buildReq(41L, "class Solution { public int[] maxSlidingWindow(int[] n, int k) { while(true){} } }", tcs));
        assertEquals(SubmissionStatus.TIME_LIMIT_EXCEEDED, tleRes.status());
    }
}
