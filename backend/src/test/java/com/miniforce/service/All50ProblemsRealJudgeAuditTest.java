package com.miniforce.service;

import com.miniforce.judge.ExecutionRequest;
import com.miniforce.judge.ExecutionResult;
import com.miniforce.judge.ProblemContract;
import com.miniforce.judge.TestCaseRequest;
import com.miniforce.model.SubmissionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {"miniforce.judge.real-enabled=true"})
@ActiveProfiles("test")
public class All50ProblemsRealJudgeAuditTest {

    @Autowired
    private DockerRealJudgeAdapter realJudge;

    public static TestCaseRequest getTestCaseForProblem(int pid) {
        return switch (pid) {
            case 1 -> new TestCaseRequest(1L, "[2,7,11,15]; 9", "[0,1]");
            case 2 -> new TestCaseRequest(2L, "()[]{}", "true");
            case 3 -> new TestCaseRequest(3L, "abcabcbb", "3");
            case 4 -> new TestCaseRequest(4L, "[[1,3],[2,6],[8,10]]", "[[1,6],[8,10]]");
            case 5 -> new TestCaseRequest(5L, "2; [[1,0]]", "true");
            case 6 -> new TestCaseRequest(6L, "[1,3]; [2]", "2.0");
            case 7 -> new TestCaseRequest(7L, "hit; cog; hot,dot,dog,lot,log,cog", "5");
            case 8 -> new TestCaseRequest(8L, "[7,1,5,3,6,4]", "5");
            case 9 -> new TestCaseRequest(9L, "[1,2,3,4,5]", "[5,4,3,2,1]");
            case 10 -> new TestCaseRequest(10L, "[[1,1,0],[0,1,0],[1,0,1]]", "3");
            case 11 -> new TestCaseRequest(11L, "[1,2,5]; 11", "3");
            case 12 -> new TestCaseRequest(12L, "2", "2");
            case 13 -> new TestCaseRequest(13L, "[1,2,3,4]", "[24,12,8,6]");
            case 14 -> new TestCaseRequest(14L, "[-2,1,-3,4,-1,2,1,-5,4]", "6");
            case 15 -> new TestCaseRequest(15L, "[0,1,0,2,1,0,1,3,2,1,2,1]", "6");
            case 16 -> new TestCaseRequest(16L, "\"anagram\"; \"nagaram\"", "true");
            case 17 -> new TestCaseRequest(17L, "[\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"]", "[[\"bat\"],[\"nat\",\"tan\"],[\"ate\",\"eat\",\"tea\"]]");
            case 18 -> new TestCaseRequest(18L, "[1,1,1,2,2,3]; 2", "[1,2]");
            case 19 -> new TestCaseRequest(19L, "[\"Hello\",\"World\"]", "[\"Hello\",\"World\"]");
            case 20 -> new TestCaseRequest(20L, "[100,4,200,1,3,2]", "4");
            case 21 -> new TestCaseRequest(21L, "[[\"5\",\"3\",\".\",\".\",\"7\",\".\",\".\",\".\",\".\"],[\"6\",\".\",\".\",\"1\",\"9\",\"5\",\".\",\".\",\".\"],[\".\",\"9\",\"8\",\".\",\".\",\".\",\".\",\"6\",\".\"],[\"8\",\".\",\".\",\".\",\"6\",\".\",\".\",\".\",\"3\"],[\"4\",\".\",\".\",\"8\",\".\",\"3\",\".\",\".\",\"1\"],[\"7\",\".\",\".\",\".\",\"2\",\".\",\".\",\".\",\"6\"],[\".\",\"6\",\".\",\".\",\".\",\".\",\"2\",\"8\",\".\"],[\".\",\".\",\".\",\"4\",\"1\",\"9\",\".\",\".\",\"5\"],[\".\",\".\",\".\",\".\",\"8\",\".\",\".\",\"7\",\"9\"]]", "true");
            case 22 -> new TestCaseRequest(22L, "[1,2,3,1]", "true");
            case 23 -> new TestCaseRequest(23L, "[4,3,2,7,8,2,3,1]", "[5,6]");
            case 24 -> new TestCaseRequest(24L, "[1,2,2,1]; [2,2]", "[2]");
            case 25 -> new TestCaseRequest(25L, "\"egg\"; \"add\"", "true");
            case 26 -> new TestCaseRequest(26L, "\"A man, a plan, a canal: Panama\"", "true");
            case 27 -> new TestCaseRequest(27L, "[-1,0,1,2,-1,-4]", "[[-1,-1,2],[-1,0,1]]");
            case 28 -> new TestCaseRequest(28L, "[1,8,6,2,5,4,8,3,7]", "49");
            case 29 -> new TestCaseRequest(29L, "\"ADOBECODEBANC\"; \"ABC\"", "\"BANC\"");
            case 30 -> new TestCaseRequest(30L, "\"AABABBA\"; 1", "4");
            case 31 -> new TestCaseRequest(31L, "\"ab\"; \"eidbaooo\"", "true");
            case 32 -> new TestCaseRequest(32L, "[1,12,-5,-6,50,3]; 4", "12.75000");
            case 33 -> new TestCaseRequest(33L, "\"cbaebabacd\"; \"abc\"", "[0,6]");
            case 34 -> new TestCaseRequest(34L, "[1,2,1,2,3]; 2", "7");
            case 35 -> new TestCaseRequest(35L, "[1,2,1]", "3");
            case 36 -> new TestCaseRequest(36L, "[\"MinStack\",\"push\",\"push\",\"push\",\"getMin\",\"pop\",\"top\",\"getMin\"]; [[],[-2],[0],[-3],[],[],[],[]]", "[null,null,null,null,-3,null,0,-2]");
            case 37 -> new TestCaseRequest(37L, "[\"2\",\"1\",\"+\",\"3\",\"*\"]", "9");
            case 38 -> new TestCaseRequest(38L, "[73,74,75,71,69,72,76,73]", "[1,1,4,2,1,1,0,0]");
            case 39 -> new TestCaseRequest(39L, "[2,1,5,6,2,3]", "10");
            case 40 -> new TestCaseRequest(40L, "[\"MyQueue\",\"push\",\"push\",\"peek\",\"pop\",\"empty\"]; [[],[1],[2],[],[],[]]", "[null,null,null,1,1,false]");
            case 41 -> new TestCaseRequest(41L, "[1,3,-1,-3,5,3,6,7]; 3", "[3,3,5,5,6,7]");
            case 42 -> new TestCaseRequest(42L, "[5,10,-5]", "[5,10]");
            case 43 -> new TestCaseRequest(43L, "[-1,0,3,5,9,12]; 9", "4");
            case 44 -> new TestCaseRequest(44L, "[[1,3,5,7],[10,11,16,20],[23,30,34,60]]; 3", "true");
            case 45 -> new TestCaseRequest(45L, "[3,6,7,11]; 8", "4");
            case 46 -> new TestCaseRequest(46L, "[3,4,5,1,2]", "1");
            case 47 -> new TestCaseRequest(47L, "[4,5,6,7,0,1,2]; 0", "4");
            case 48 -> new TestCaseRequest(48L, "[\"TimeMap\", \"set\", \"get\", \"get\", \"set\", \"get\", \"get\"]; [[], [\"foo\", \"bar\", 1], [\"foo\", 1], [\"foo\", 3], [\"foo\", \"bar2\", 4], [\"foo\", 4], [\"foo\", 5]]", "[null,null,\"bar\",\"bar\",null,\"bar2\",\"bar2\"]");
            case 49 -> new TestCaseRequest(49L, "[7,2,5,10,8]; 2", "18");
            case 50 -> new TestCaseRequest(50L, "[1,2,4]; [1,3,4]", "[1,1,2,3,4,4]");
            default -> throw new IllegalArgumentException("Unknown problem: " + pid);
        };
    }

    public static String getCorrectJavaSolution(int pid) {
        return switch (pid) {
            case 1 -> """
                import java.util.*;
                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        Map<Integer, Integer> map = new HashMap<>();
                        for (int i = 0; i < nums.length; i++) {
                            int comp = target - nums[i];
                            if (map.containsKey(comp)) return new int[]{map.get(comp), i};
                            map.put(nums[i], i);
                        }
                        return new int[]{};
                    }
                }
                """;
            case 2 -> """
                import java.util.*;
                class Solution {
                    public boolean isValid(String s) {
                        Deque<Character> stack = new ArrayDeque<>();
                        for (char c : s.toCharArray()) {
                            if (c == '(') stack.push(')');
                            else if (c == '{') stack.push('}');
                            else if (c == '[') stack.push(']');
                            else if (stack.isEmpty() || stack.pop() != c) return false;
                        }
                        return stack.isEmpty();
                    }
                }
                """;
            case 3 -> """
                import java.util.*;
                class Solution {
                    public int lengthOfLongestSubstring(String s) {
                        Map<Character, Integer> map = new HashMap<>();
                        int max = 0, left = 0;
                        for (int right = 0; right < s.length(); right++) {
                            char c = s.charAt(right);
                            if (map.containsKey(c)) {
                                left = Math.max(left, map.get(c) + 1);
                            }
                            map.put(c, right);
                            max = Math.max(max, right - left + 1);
                        }
                        return max;
                    }
                }
                """;
            case 4 -> """
                import java.util.*;
                class Solution {
                    public int[][] merge(int[][] intervals) {
                        if (intervals.length <= 1) return intervals;
                        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
                        List<int[]> result = new ArrayList<>();
                        int[] curr = intervals[0];
                        result.add(curr);
                        for (int[] interval : intervals) {
                            if (interval[0] <= curr[1]) {
                                curr[1] = Math.max(curr[1], interval[1]);
                            } else {
                                curr = interval;
                                result.add(curr);
                            }
                        }
                        return result.toArray(new int[result.size()][]);
                    }
                }
                """;
            case 5 -> """
                import java.util.*;
                class Solution {
                    public boolean canFinish(int numCourses, int[][] prerequisites) {
                        List<List<Integer>> adj = new ArrayList<>();
                        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
                        int[] indegree = new int[numCourses];
                        for (int[] p : prerequisites) {
                            adj.get(p[1]).add(p[0]);
                            indegree[p[0]]++;
                        }
                        Queue<Integer> q = new LinkedList<>();
                        for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) q.offer(i);
                        int count = 0;
                        while (!q.isEmpty()) {
                            int node = q.poll();
                            count++;
                            for (int next : adj.get(node)) {
                                indegree[next]--;
                                if (indegree[next] == 0) q.offer(next);
                            }
                        }
                        return count == numCourses;
                    }
                }
                """;
            case 6 -> """
                import java.util.*;
                class Solution {
                    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
                        int[] merged = new int[nums1.length + nums2.length];
                        System.arraycopy(nums1, 0, merged, 0, nums1.length);
                        System.arraycopy(nums2, 0, merged, nums1.length, nums2.length);
                        Arrays.sort(merged);
                        int n = merged.length;
                        if (n % 2 == 1) return (double) merged[n / 2];
                        return (merged[n / 2 - 1] + merged[n / 2]) / 2.0;
                    }
                }
                """;
            case 7 -> """
                import java.util.*;
                class Solution {
                    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
                        Set<String> set = new HashSet<>(wordList);
                        if (!set.contains(endWord)) return 0;
                        Queue<String> queue = new LinkedList<>();
                        queue.add(beginWord);
                        int level = 1;
                        while (!queue.isEmpty()) {
                            int size = queue.size();
                            for (int i = 0; i < size; i++) {
                                String cur = queue.poll();
                                if (cur.equals(endWord)) return level;
                                char[] chars = cur.toCharArray();
                                for (int j = 0; j < chars.length; j++) {
                                    char orig = chars[j];
                                    for (char c = 'a'; c <= 'z'; c++) {
                                        chars[j] = c;
                                        String next = new String(chars);
                                        if (set.contains(next)) {
                                            set.remove(next);
                                            queue.add(next);
                                        }
                                    }
                                    chars[j] = orig;
                                }
                            }
                            level++;
                        }
                        return 0;
                    }
                }
                """;
            case 8 -> """
                class Solution {
                    public int maxProfit(int[] prices) {
                        int minPrice = Integer.MAX_VALUE;
                        int maxProfit = 0;
                        for (int p : prices) {
                            if (p < minPrice) minPrice = p;
                            else if (p - minPrice > maxProfit) maxProfit = p - minPrice;
                        }
                        return maxProfit;
                    }
                }
                """;
            case 9 -> """
                class Solution {
                    public ListNode reverseList(ListNode head) {
                        ListNode prev = null;
                        ListNode curr = head;
                        while (curr != null) {
                            ListNode next = curr.next;
                            curr.next = prev;
                            prev = curr;
                            curr = next;
                        }
                        return prev;
                    }
                }
                """;
            case 10 -> """
                class Solution {
                    public int numIslands(char[][] grid) {
                        if (grid == null || grid.length == 0) return 0;
                        int count = 0;
                        for (int r = 0; r < grid.length; r++) {
                            for (int c = 0; c < grid[0].length; c++) {
                                if (grid[r][c] == '1') {
                                    count++;
                                    dfs(grid, r, c);
                                }
                            }
                        }
                        return count;
                    }
                    private void dfs(char[][] grid, int r, int c) {
                        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
                        grid[r][c] = '0';
                        dfs(grid, r + 1, c);
                        dfs(grid, r - 1, c);
                        dfs(grid, r, c + 1);
                        dfs(grid, r, c - 1);
                    }
                }
                """;
            case 11 -> """
                import java.util.*;
                class Solution {
                    public int coinChange(int[] coins, int amount) {
                        int[] dp = new int[amount + 1];
                        Arrays.fill(dp, amount + 1);
                        dp[0] = 0;
                        for (int c : coins) {
                            for (int i = c; i <= amount; i++) {
                                dp[i] = Math.min(dp[i], dp[i - c] + 1);
                            }
                        }
                        return dp[amount] > amount ? -1 : dp[amount];
                    }
                }
                """;
            case 12 -> """
                class Solution {
                    public int climbStairs(int n) {
                        if (n <= 1) return 1;
                        int a = 1, b = 1;
                        for (int i = 2; i <= n; i++) {
                            int c = a + b;
                            a = b;
                            b = c;
                        }
                        return b;
                    }
                }
                """;
            case 13 -> """
                class Solution {
                    public int[] productExceptSelf(int[] nums) {
                        int n = nums.length;
                        int[] res = new int[n];
                        res[0] = 1;
                        for (int i = 1; i < n; i++) res[i] = res[i - 1] * nums[i - 1];
                        int r = 1;
                        for (int i = n - 1; i >= 0; i--) {
                            res[i] *= r;
                            r *= nums[i];
                        }
                        return res;
                    }
                }
                """;
            case 14 -> """
                class Solution {
                    public int maxSubArray(int[] nums) {
                        int max = nums[0], cur = nums[0];
                        for (int i = 1; i < nums.length; i++) {
                            cur = Math.max(nums[i], cur + nums[i]);
                            max = Math.max(max, cur);
                        }
                        return max;
                    }
                }
                """;
            case 15 -> """
                class Solution {
                    public int trap(int[] height) {
                        if (height == null || height.length == 0) return 0;
                        int l = 0, r = height.length - 1;
                        int lMax = height[l], rMax = height[r], ans = 0;
                        while (l < r) {
                            if (lMax < rMax) {
                                l++;
                                lMax = Math.max(lMax, height[l]);
                                ans += Math.max(0, lMax - height[l]);
                            } else {
                                r--;
                                rMax = Math.max(rMax, height[r]);
                                ans += Math.max(0, rMax - height[r]);
                            }
                        }
                        return ans;
                    }
                }
                """;
            case 16 -> """
                import java.util.*;
                class Solution {
                    public boolean isAnagram(String s, String t) {
                        if (s.length() != t.length()) return false;
                        char[] ca1 = s.toCharArray();
                        char[] ca2 = t.toCharArray();
                        Arrays.sort(ca1);
                        Arrays.sort(ca2);
                        return Arrays.equals(ca1, ca2);
                    }
                }
                """;
            case 17 -> """
                import java.util.*;
                class Solution {
                    public List<List<String>> groupAnagrams(String[] strs) {
                        Map<String, List<String>> map = new HashMap<>();
                        for (String s : strs) {
                            char[] ca = s.toCharArray();
                            Arrays.sort(ca);
                            String key = String.valueOf(ca);
                            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
                        }
                        return new ArrayList<>(map.values());
                    }
                }
                """;
            case 18 -> """
                import java.util.*;
                class Solution {
                    public int[] topKFrequent(int[] nums, int k) {
                        Map<Integer, Integer> count = new HashMap<>();
                        for (int n : nums) count.put(n, count.getOrDefault(n, 0) + 1);
                        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> count.get(a) - count.get(b));
                        for (int n : count.keySet()) {
                            pq.offer(n);
                            if (pq.size() > k) pq.poll();
                        }
                        int[] res = new int[k];
                        for (int i = 0; i < k; i++) res[i] = pq.poll();
                        return res;
                    }
                }
                """;
            case 19 -> """
                import java.util.*;
                class Codec {
                    public String encode(List<String> strs) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : strs) {
                            sb.append(s.length()).append(":").append(s);
                        }
                        return sb.toString();
                    }
                    public List<String> decode(String s) {
                        List<String> res = new ArrayList<>();
                        int i = 0;
                        while (i < s.length()) {
                            int colon = s.indexOf(':', i);
                            int len = Integer.parseInt(s.substring(i, colon));
                            res.add(s.substring(colon + 1, colon + 1 + len));
                            i = colon + 1 + len;
                        }
                        return res;
                    }
                }
                """;
            case 20 -> """
                import java.util.*;
                class Solution {
                    public int longestConsecutive(int[] nums) {
                        Set<Integer> set = new HashSet<>();
                        for (int n : nums) set.add(n);
                        int max = 0;
                        for (int n : set) {
                            if (!set.contains(n - 1)) {
                                int cur = n;
                                int streak = 1;
                                while (set.contains(cur + 1)) {
                                    cur++;
                                    streak++;
                                }
                                max = Math.max(max, streak);
                            }
                        }
                        return max;
                    }
                }
                """;
            case 21 -> """
                import java.util.*;
                class Solution {
                    public boolean isValidSudoku(char[][] board) {
                        Set<String> seen = new HashSet<>();
                        for (int r = 0; r < 9; r++) {
                            for (int c = 0; c < 9; c++) {
                                char ch = board[r][c];
                                if (ch != '.') {
                                    if (!seen.add(ch + " in row " + r) ||
                                        !seen.add(ch + " in col " + c) ||
                                        !seen.add(ch + " in box " + (r / 3) + "-" + (c / 3))) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return true;
                    }
                }
                """;
            case 22 -> """
                import java.util.*;
                class Solution {
                    public boolean containsDuplicate(int[] nums) {
                        Set<Integer> set = new HashSet<>();
                        for (int n : nums) if (!set.add(n)) return true;
                        return false;
                    }
                }
                """;
            case 23 -> """
                import java.util.*;
                class Solution {
                    public List<Integer> findDisappearedNumbers(int[] nums) {
                        for (int i = 0; i < nums.length; i++) {
                            int val = Math.abs(nums[i]) - 1;
                            if (nums[val] > 0) nums[val] = -nums[val];
                        }
                        List<Integer> res = new ArrayList<>();
                        for (int i = 0; i < nums.length; i++) {
                            if (nums[i] > 0) res.add(i + 1);
                        }
                        return res;
                    }
                }
                """;
            case 24 -> """
                import java.util.*;
                class Solution {
                    public int[] intersection(int[] nums1, int[] nums2) {
                        Set<Integer> s1 = new HashSet<>();
                        for (int n : nums1) s1.add(n);
                        Set<Integer> resSet = new HashSet<>();
                        for (int n : nums2) if (s1.contains(n)) resSet.add(n);
                        int[] res = new int[resSet.size()];
                        int i = 0;
                        for (int n : resSet) res[i++] = n;
                        return res;
                    }
                }
                """;
            case 25 -> """
                class Solution {
                    public boolean isIsomorphic(String s, String t) {
                        if (s.length() != t.length()) return false;
                        int[] m1 = new int[256];
                        int[] m2 = new int[256];
                        for (int i = 0; i < s.length(); i++) {
                            if (m1[s.charAt(i)] != m2[t.charAt(i)]) return false;
                            m1[s.charAt(i)] = i + 1;
                            m2[t.charAt(i)] = i + 1;
                        }
                        return true;
                    }
                }
                """;
            case 26 -> """
                class Solution {
                    public boolean isPalindrome(String s) {
                        int l = 0, r = s.length() - 1;
                        while (l < r) {
                            while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
                            while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
                            if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) return false;
                            l++;
                            r--;
                        }
                        return true;
                    }
                }
                """;
            case 27 -> """
                import java.util.*;
                class Solution {
                    public List<List<Integer>> threeSum(int[] nums) {
                        Arrays.sort(nums);
                        List<List<Integer>> res = new ArrayList<>();
                        for (int i = 0; i < nums.length - 2; i++) {
                            if (i > 0 && nums[i] == nums[i - 1]) continue;
                            int l = i + 1, r = nums.length - 1;
                            while (l < r) {
                                int sum = nums[i] + nums[l] + nums[r];
                                if (sum == 0) {
                                    res.add(Arrays.asList(nums[i], nums[l], nums[r]));
                                    while (l < r && nums[l] == nums[l + 1]) l++;
                                    while (l < r && nums[r] == nums[r - 1]) r--;
                                    l++;
                                    r--;
                                } else if (sum < 0) l++;
                                else r--;
                            }
                        }
                        return res;
                    }
                }
                """;
            case 28 -> """
                class Solution {
                    public int maxArea(int[] height) {
                        int l = 0, r = height.length - 1, max = 0;
                        while (l < r) {
                            max = Math.max(max, Math.min(height[l], height[r]) * (r - l));
                            if (height[l] < height[r]) l++;
                            else r--;
                        }
                        return max;
                    }
                }
                """;
            case 29 -> """
                import java.util.*;
                class Solution {
                    public String minWindow(String s, String t) {
                        if (s == null || t == null || s.length() < t.length()) return "";
                        int[] target = new int[128];
                        for (char c : t.toCharArray()) target[c]++;
                        int required = 0;
                        for (int count : target) if (count > 0) required++;
                        int[] window = new int[128];
                        int formed = 0, l = 0, r = 0;
                        int minLen = Integer.MAX_VALUE, start = 0;
                        while (r < s.length()) {
                            char c = s.charAt(r);
                            window[c]++;
                            if (target[c] > 0 && window[c] == target[c]) formed++;
                            while (l <= r && formed == required) {
                                if (r - l + 1 < minLen) {
                                    minLen = r - l + 1;
                                    start = l;
                                }
                                char leftChar = s.charAt(l);
                                window[leftChar]--;
                                if (target[leftChar] > 0 && window[leftChar] < target[leftChar]) formed--;
                                l++;
                            }
                            r++;
                        }
                        return minLen == Integer.MAX_VALUE ? "" : s.substring(start, start + minLen);
                    }
                }
                """;
            case 30 -> """
                class Solution {
                    public int characterReplacement(String s, int k) {
                        int[] count = new int[26];
                        int maxCount = 0, l = 0, maxLen = 0;
                        for (int r = 0; r < s.length(); r++) {
                            maxCount = Math.max(maxCount, ++count[s.charAt(r) - 'A']);
                            while (r - l + 1 - maxCount > k) {
                                count[s.charAt(l) - 'A']--;
                                l++;
                            }
                            maxLen = Math.max(maxLen, r - l + 1);
                        }
                        return maxLen;
                    }
                }
                """;
            case 31 -> """
                import java.util.*;
                class Solution {
                    public boolean checkInclusion(String s1, String s2) {
                        if (s1.length() > s2.length()) return false;
                        int[] c1 = new int[26];
                        int[] c2 = new int[26];
                        for (int i = 0; i < s1.length(); i++) {
                            c1[s1.charAt(i) - 'a']++;
                            c2[s2.charAt(i) - 'a']++;
                        }
                        if (Arrays.equals(c1, c2)) return true;
                        for (int i = s1.length(); i < s2.length(); i++) {
                            c2[s2.charAt(i) - 'a']++;
                            c2[s2.charAt(i - s1.length()) - 'a']--;
                            if (Arrays.equals(c1, c2)) return true;
                        }
                        return false;
                    }
                }
                """;
            case 32 -> """
                class Solution {
                    public double findMaxAverage(int[] nums, int k) {
                        double sum = 0;
                        for (int i = 0; i < k; i++) sum += nums[i];
                        double max = sum;
                        for (int i = k; i < nums.length; i++) {
                            sum += nums[i] - nums[i - k];
                            max = Math.max(max, sum);
                        }
                        return max / k;
                    }
                }
                """;
            case 33 -> """
                import java.util.*;
                class Solution {
                    public List<Integer> findAnagrams(String s, String p) {
                        List<Integer> res = new ArrayList<>();
                        if (s.length() < p.length()) return res;
                        int[] cp = new int[26];
                        int[] cs = new int[26];
                        for (int i = 0; i < p.length(); i++) {
                            cp[p.charAt(i) - 'a']++;
                            cs[s.charAt(i) - 'a']++;
                        }
                        if (Arrays.equals(cp, cs)) res.add(0);
                        for (int i = p.length(); i < s.length(); i++) {
                            cs[s.charAt(i) - 'a']++;
                            cs[s.charAt(i - p.length()) - 'a']--;
                            if (Arrays.equals(cp, cs)) res.add(i - p.length() + 1);
                        }
                        return res;
                    }
                }
                """;
            case 34 -> """
                class Solution {
                    public int subarraysWithKDistinct(int[] nums, int k) {
                        return atMost(nums, k) - atMost(nums, k - 1);
                    }
                    private int atMost(int[] nums, int k) {
                        int l = 0, count = 0, distinct = 0;
                        int[] freq = new int[nums.length + 1];
                        for (int r = 0; r < nums.length; r++) {
                            if (freq[nums[r]] == 0) distinct++;
                            freq[nums[r]]++;
                            while (distinct > k) {
                                freq[nums[l]]--;
                                if (freq[nums[l]] == 0) distinct--;
                                l++;
                            }
                            count += r - l + 1;
                        }
                        return count;
                    }
                }
                """;
            case 35 -> """
                import java.util.*;
                class Solution {
                    public int totalFruit(int[] fruits) {
                        Map<Integer, Integer> count = new HashMap<>();
                        int l = 0, max = 0;
                        for (int r = 0; r < fruits.length; r++) {
                            count.put(fruits[r], count.getOrDefault(fruits[r], 0) + 1);
                            while (count.size() > 2) {
                                count.put(fruits[l], count.get(fruits[l]) - 1);
                                if (count.get(fruits[l]) == 0) count.remove(fruits[l]);
                                l++;
                            }
                            max = Math.max(max, r - l + 1);
                        }
                        return max;
                    }
                }
                """;
            case 36 -> """
                import java.util.*;
                class MinStack {
                    private List<Integer> stack = new ArrayList<>();
                    private List<Integer> minStack = new ArrayList<>();
                    public MinStack() {}
                    public void push(int val) {
                        stack.add(val);
                        if (minStack.isEmpty() || val <= minStack.get(minStack.size() - 1)) {
                            minStack.add(val);
                        }
                    }
                    public void pop() {
                        int val = stack.remove(stack.size() - 1);
                        if (val == minStack.get(minStack.size() - 1)) {
                            minStack.remove(minStack.size() - 1);
                        }
                    }
                    public int top() {
                        return stack.get(stack.size() - 1);
                    }
                    public int getMin() {
                        return minStack.get(minStack.size() - 1);
                    }
                }
                """;
            case 37 -> """
                import java.util.*;
                class Solution {
                    public int evalRPN(String[] tokens) {
                        Deque<Integer> stack = new ArrayDeque<>();
                        for (String t : tokens) {
                            if ("+".equals(t)) stack.push(stack.pop() + stack.pop());
                            else if ("-".equals(t)) {
                                int b = stack.pop(), a = stack.pop();
                                stack.push(a - b);
                            } else if ("*".equals(t)) stack.push(stack.pop() * stack.pop());
                            else if ("/".equals(t)) {
                                int b = stack.pop(), a = stack.pop();
                                stack.push(a / b);
                            } else {
                                stack.push(Integer.parseInt(t));
                            }
                        }
                        return stack.pop();
                    }
                }
                """;
            case 38 -> """
                import java.util.*;
                class Solution {
                    public int[] dailyTemperatures(int[] temperatures) {
                        int n = temperatures.length;
                        int[] res = new int[n];
                        Deque<Integer> stack = new ArrayDeque<>();
                        for (int i = 0; i < n; i++) {
                            while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
                                int prev = stack.pop();
                                res[prev] = i - prev;
                            }
                            stack.push(i);
                        }
                        return res;
                    }
                }
                """;
            case 39 -> """
                import java.util.*;
                class Solution {
                    public int largestRectangleArea(int[] heights) {
                        Deque<Integer> stack = new ArrayDeque<>();
                        int max = 0;
                        int n = heights.length;
                        for (int i = 0; i <= n; i++) {
                            int h = (i == n) ? 0 : heights[i];
                            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                                int height = heights[stack.pop()];
                                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                                max = Math.max(max, height * width);
                            }
                            stack.push(i);
                        }
                        return max;
                    }
                }
                """;
            case 40 -> """
                import java.util.*;
                class MyQueue {
                    private Deque<Integer> s1 = new ArrayDeque<>();
                    private Deque<Integer> s2 = new ArrayDeque<>();
                    public MyQueue() {}
                    public void push(int x) {
                        s1.push(x);
                    }
                    public int pop() {
                        shift();
                        return s2.pop();
                    }
                    public int peek() {
                        shift();
                        return s2.peek();
                    }
                    public boolean empty() {
                        return s1.isEmpty() && s2.isEmpty();
                    }
                    private void shift() {
                        if (s2.isEmpty()) {
                            while (!s1.isEmpty()) s2.push(s1.pop());
                        }
                    }
                }
                """;
            case 41 -> """
                import java.util.*;
                class Solution {
                    public int[] maxSlidingWindow(int[] nums, int k) {
                        if (nums == null || k <= 0) return new int[0];
                        int n = nums.length;
                        int[] res = new int[n - k + 1];
                        Deque<Integer> q = new ArrayDeque<>();
                        for (int i = 0; i < n; i++) {
                            while (!q.isEmpty() && q.peek() < i - k + 1) q.poll();
                            while (!q.isEmpty() && nums[q.peekLast()] < nums[i]) q.pollLast();
                            q.offer(i);
                            if (i >= k - 1) res[i - k + 1] = nums[q.peek()];
                        }
                        return res;
                    }
                }
                """;
            case 42 -> """
                import java.util.*;
                class Solution {
                    public int[] asteroidCollision(int[] asteroids) {
                        Deque<Integer> stack = new ArrayDeque<>();
                        for (int ast : asteroids) {
                            boolean destroyed = false;
                            while (!stack.isEmpty() && ast < 0 && stack.peek() > 0) {
                                if (stack.peek() < -ast) {
                                    stack.pop();
                                    continue;
                                } else if (stack.peek() == -ast) {
                                    stack.pop();
                                }
                                destroyed = true;
                                break;
                            }
                            if (!destroyed) stack.push(ast);
                        }
                        int[] res = new int[stack.size()];
                        for (int i = res.length - 1; i >= 0; i--) res[i] = stack.pop();
                        return res;
                    }
                }
                """;
            case 43 -> """
                class Solution {
                    public int search(int[] nums, int target) {
                        int l = 0, r = nums.length - 1;
                        while (l <= r) {
                            int m = l + (r - l) / 2;
                            if (nums[m] == target) return m;
                            else if (nums[m] < target) l = m + 1;
                            else r = m - 1;
                        }
                        return -1;
                    }
                }
                """;
            case 44 -> """
                class Solution {
                    public boolean searchMatrix(int[][] matrix, int target) {
                        if (matrix == null || matrix.length == 0) return false;
                        int R = matrix.length, C = matrix[0].length;
                        int l = 0, r = R * C - 1;
                        while (l <= r) {
                            int m = l + (r - l) / 2;
                            int val = matrix[m / C][m % C];
                            if (val == target) return true;
                            else if (val < target) l = m + 1;
                            else r = m - 1;
                        }
                        return false;
                    }
                }
                """;
            case 45 -> """
                class Solution {
                    public int minEatingSpeed(int[] piles, int h) {
                        int l = 1, r = 1;
                        for (int p : piles) if (p > r) r = p;
                        while (l <= r) {
                            int m = l + (r - l) / 2;
                            long hours = 0;
                            for (int p : piles) hours += (p + m - 1) / m;
                            if (hours <= h) r = m - 1;
                            else l = m + 1;
                        }
                        return l;
                    }
                }
                """;
            case 46 -> """
                class Solution {
                    public int findMin(int[] nums) {
                        int l = 0, r = nums.length - 1;
                        while (l < r) {
                            int m = l + (r - l) / 2;
                            if (nums[m] > nums[r]) l = m + 1;
                            else r = m;
                        }
                        return nums[l];
                    }
                }
                """;
            case 47 -> """
                class Solution {
                    public int search(int[] nums, int target) {
                        int l = 0, r = nums.length - 1;
                        while (l <= r) {
                            int m = l + (r - l) / 2;
                            if (nums[m] == target) return m;
                            if (nums[l] <= nums[m]) {
                                if (nums[l] <= target && target < nums[m]) r = m - 1;
                                else l = m + 1;
                            } else {
                                if (nums[m] < target && target <= nums[r]) l = m + 1;
                                else r = m - 1;
                            }
                        }
                        return -1;
                    }
                }
                """;
            case 48 -> """
                import java.util.*;
                class TimeMap {
                    private static class Entry {
                        int time;
                        String val;
                        Entry(int time, String val) { this.time = time; this.val = val; }
                    }
                    private Map<String, List<Entry>> map = new HashMap<>();
                    public TimeMap() {}
                    public void set(String key, String value, int timestamp) {
                        map.computeIfAbsent(key, k -> new ArrayList<>()).add(new Entry(timestamp, value));
                    }
                    public String get(String key, int timestamp) {
                        if (!map.containsKey(key)) return "";
                        List<Entry> list = map.get(key);
                        int l = 0, r = list.size() - 1;
                        String res = "";
                        while (l <= r) {
                            int m = l + (r - l) / 2;
                            if (list.get(m).time <= timestamp) {
                                res = list.get(m).val;
                                l = m + 1;
                            } else {
                                r = m - 1;
                            }
                        }
                        return res;
                    }
                }
                """;
            case 49 -> """
                class Solution {
                    public int splitArray(int[] nums, int k) {
                        long l = 0, r = 0;
                        for (int n : nums) {
                            l = Math.max(l, n);
                            r += n;
                        }
                        while (l < r) {
                            long m = l + (r - l) / 2;
                            int pieces = 1;
                            long cur = 0;
                            for (int n : nums) {
                                if (cur + n > m) {
                                    cur = n;
                                    pieces++;
                                } else {
                                    cur += n;
                                }
                            }
                            if (pieces > k) l = m + 1;
                            else r = m;
                        }
                        return (int) l;
                    }
                }
                """;
            case 50 -> """
                class Solution {
                    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
                        ListNode dummy = new ListNode(0);
                        ListNode curr = dummy;
                        while (list1 != null && list2 != null) {
                            if (list1.val <= list2.val) {
                                curr.next = list1;
                                list1 = list1.next;
                            } else {
                                curr.next = list2;
                                list2 = list2.next;
                            }
                            curr = curr.next;
                        }
                        curr.next = (list1 != null) ? list1 : list2;
                        return dummy.next;
                    }
                }
                """;
            default -> throw new IllegalArgumentException("Unknown problem: " + pid);
        };
    }

    public static String getWrongJavaSolution(int pid) {
        return switch (pid) {
            case 1 -> "class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{-1, -1}; } }";
            case 2 -> "class Solution { public boolean isValid(String s) { return false; } }";
            case 3 -> "class Solution { public int lengthOfLongestSubstring(String s) { return -1; } }";
            case 4 -> "class Solution { public int[][] merge(int[][] intervals) { return new int[0][0]; } }";
            case 5 -> "class Solution { public boolean canFinish(int numCourses, int[][] prerequisites) { return false; } }";
            case 6 -> "class Solution { public double findMedianSortedArrays(int[] nums1, int[] nums2) { return -1.0; } }";
            case 7 -> "import java.util.*; class Solution { public int ladderLength(String beginWord, String endWord, List<String> wordList) { return -1; } }";
            case 8 -> "class Solution { public int maxProfit(int[] prices) { return -1; } }";
            case 9 -> "class Solution { public ListNode reverseList(ListNode head) { return null; } }";
            case 10 -> "class Solution { public int numIslands(char[][] grid) { return -1; } }";
            case 11 -> "class Solution { public int coinChange(int[] coins, int amount) { return -99; } }";
            case 12 -> "class Solution { public int climbStairs(int n) { return -1; } }";
            case 13 -> "class Solution { public int[] productExceptSelf(int[] nums) { return new int[nums.length]; } }";
            case 14 -> "class Solution { public int maxSubArray(int[] nums) { return -999; } }";
            case 15 -> "class Solution { public int trap(int[] height) { return -1; } }";
            case 16 -> "class Solution { public boolean isAnagram(String s, String t) { return false; } }";
            case 17 -> "import java.util.*; class Solution { public List<List<String>> groupAnagrams(String[] strs) { return new ArrayList<>(); } }";
            case 18 -> "class Solution { public int[] topKFrequent(int[] nums, int k) { return new int[]{9999}; } }";
            case 19 -> "import java.util.*; class Codec { public String encode(List<String> strs) { return \"wrong\"; } public List<String> decode(String s) { return List.of(\"wrong\"); } }";
            case 20 -> "class Solution { public int longestConsecutive(int[] nums) { return -1; } }";
            case 21 -> "class Solution { public boolean isValidSudoku(char[][] board) { return false; } }";
            case 22 -> "class Solution { public boolean containsDuplicate(int[] nums) { return false; } }";
            case 23 -> "import java.util.*; class Solution { public List<Integer> findDisappearedNumbers(int[] nums) { return new ArrayList<>(); } }";
            case 24 -> "class Solution { public int[] intersection(int[] nums1, int[] nums2) { return new int[]{9999}; } }";
            case 25 -> "class Solution { public boolean isIsomorphic(String s, String t) { return false; } }";
            case 26 -> "class Solution { public boolean isPalindrome(String s) { return false; } }";
            case 27 -> "import java.util.*; class Solution { public List<List<Integer>> threeSum(int[] nums) { return new ArrayList<>(); } }";
            case 28 -> "class Solution { public int maxArea(int[] height) { return -1; } }";
            case 29 -> "class Solution { public String minWindow(String s, String t) { return \"WRONG\"; } }";
            case 30 -> "class Solution { public int characterReplacement(String s, int k) { return -1; } }";
            case 31 -> "class Solution { public boolean checkInclusion(String s1, String s2) { return false; } }";
            case 32 -> "class Solution { public double findMaxAverage(int[] nums, int k) { return -1.0; } }";
            case 33 -> "import java.util.*; class Solution { public List<Integer> findAnagrams(String s, String p) { return new ArrayList<>(); } }";
            case 34 -> "class Solution { public int subarraysWithKDistinct(int[] nums, int k) { return -1; } }";
            case 35 -> "class Solution { public int totalFruit(int[] fruits) { return -1; } }";
            case 36 -> "class MinStack { public MinStack() {} public void push(int val) {} public void pop() {} public int top() { return 0; } public int getMin() { return 0; } }";
            case 37 -> "class Solution { public int evalRPN(String[] tokens) { return -99; } }";
            case 38 -> "class Solution { public int[] dailyTemperatures(int[] temperatures) { return new int[temperatures.length]; } }";
            case 39 -> "class Solution { public int largestRectangleArea(int[] heights) { return -1; } }";
            case 40 -> "class MyQueue { public MyQueue() {} public void push(int x) {} public int pop() { return -1; } public int peek() { return -1; } public boolean empty() { return true; } }";
            case 41 -> "class Solution { public int[] maxSlidingWindow(int[] nums, int k) { return new int[]{0}; } }";
            case 42 -> "class Solution { public int[] asteroidCollision(int[] asteroids) { return new int[]{0}; } }";
            case 43 -> "class Solution { public int search(int[] nums, int target) { return -99; } }";
            case 44 -> "class Solution { public boolean searchMatrix(int[][] matrix, int target) { return false; } }";
            case 45 -> "class Solution { public int minEatingSpeed(int[] piles, int h) { return -1; } }";
            case 46 -> "class Solution { public int findMin(int[] nums) { return -99; } }";
            case 47 -> "class Solution { public int search(int[] nums, int target) { return -99; } }";
            case 48 -> "class TimeMap { public TimeMap() {} public void set(String k, String v, int t) {} public String get(String k, int t) { return \"wrong\"; } }";
            case 49 -> "class Solution { public int splitArray(int[] nums, int k) { return -1; } }";
            case 50 -> "class Solution { public ListNode mergeTwoLists(ListNode list1, ListNode list2) { return null; } }";
            default -> throw new IllegalArgumentException("Unknown problem: " + pid);
        };
    }

    public static String getCorrectPythonSolution(int pid) {
        return switch (pid) {
            case 1 -> "class Solution:\n    def twoSum(self, nums, target):\n        m = {}\n        for i, x in enumerate(nums):\n            if target - x in m: return [m[target - x], i]\n            m[x] = i\n        return []";
            case 2 -> "class Solution:\n    def isValid(self, s):\n        st = []\n        m = {')':'(', ']':'[', '}':'{'}\n        for c in s:\n            if c in m:\n                if not st or st.pop() != m[c]: return False\n            else: st.append(c)\n        return not st";
            case 3 -> "class Solution:\n    def lengthOfLongestSubstring(self, s):\n        seen = {}\n        l = res = 0\n        for r, c in enumerate(s):\n            if c in seen:\n                l = max(l, seen[c] + 1)\n            seen[c] = r\n            res = max(res, r - l + 1)\n        return res";
            case 4 -> "class Solution:\n    def merge(self, intervals):\n        intervals.sort(key=lambda x: x[0])\n        merged = []\n        for iv in intervals:\n            if not merged or merged[-1][1] < iv[0]:\n                merged.append(iv)\n            else:\n                merged[-1][1] = max(merged[-1][1], iv[1])\n        return merged";
            case 5 -> "class Solution:\n    def canFinish(self, numCourses, prerequisites):\n        adj = [[] for _ in range(numCourses)]\n        indeg = [0] * numCourses\n        for u, v in prerequisites:\n            adj[v].append(u)\n            indeg[u] += 1\n        q = [i for i in range(numCourses) if indeg[i] == 0]\n        cnt = 0\n        while q:\n            node = q.pop(0)\n            cnt += 1\n            for nxt in adj[node]:\n                indeg[nxt] -= 1\n                if indeg[nxt] == 0: q.append(nxt)\n        return cnt == numCourses";
            case 6 -> "class Solution:\n    def findMedianSortedArrays(self, nums1, nums2):\n        merged = sorted(nums1 + nums2)\n        n = len(merged)\n        if n % 2 == 1: return float(merged[n // 2])\n        return (merged[n // 2 - 1] + merged[n // 2]) / 2.0";
            case 7 -> "from collections import deque\nclass Solution:\n    def ladderLength(self, beginWord, endWord, wordList):\n        words = set(wordList)\n        if endWord not in words: return 0\n        q = deque([(beginWord, 1)])\n        while q:\n            w, d = q.popleft()\n            if w == endWord: return d\n            for i in range(len(w)):\n                for c in 'abcdefghijklmnopqrstuvwxyz':\n                    nxt = w[:i] + c + w[i+1:]\n                    if nxt in words:\n                        words.remove(nxt)\n                        q.append((nxt, d + 1))\n        return 0";
            case 8 -> "class Solution:\n    def maxProfit(self, prices):\n        min_p = float('inf'); max_p = 0\n        for p in prices:\n            min_p = min(min_p, p)\n            max_p = max(max_p, p - min_p)\n        return max_p";
            case 9 -> "class Solution:\n    def reverseList(self, head):\n        prev = None; curr = head\n        while curr:\n            nxt = curr.next\n            curr.next = prev\n            prev = curr\n            curr = nxt\n        return prev";
            case 10 -> "class Solution:\n    def numIslands(self, grid):\n        if not grid: return 0\n        R, C = len(grid), len(grid[0])\n        cnt = 0\n        def dfs(r, c):\n            if r < 0 or r >= R or c < 0 or c >= C or str(grid[r][c]) != '1': return\n            grid[r][c] = '0'\n            dfs(r+1, c); dfs(r-1, c); dfs(r, c+1); dfs(r, c-1)\n        for r in range(R):\n            for c in range(C):\n                if str(grid[r][c]) == '1':\n                    cnt += 1; dfs(r, c)\n        return cnt";
            case 11 -> "class Solution:\n    def coinChange(self, coins, amount):\n        dp = [float('inf')] * (amount + 1)\n        dp[0] = 0\n        for c in coins:\n            for i in range(c, amount + 1):\n                dp[i] = min(dp[i], dp[i - c] + 1)\n        return dp[amount] if dp[amount] != float('inf') else -1";
            case 12 -> "class Solution:\n    def climbStairs(self, n):\n        a, b = 1, 1\n        for _ in range(n): a, b = b, a + b\n        return a";
            case 13 -> "class Solution:\n    def productExceptSelf(self, nums):\n        res = [1]*len(nums); p = 1\n        for i in range(len(nums)): res[i] = p; p *= nums[i]\n        p = 1\n        for i in range(len(nums)-1, -1, -1): res[i] *= p; p *= nums[i]\n        return res";
            case 14 -> "class Solution:\n    def maxSubArray(self, nums):\n        cur = mx = nums[0]\n        for x in nums[1:]:\n            cur = max(x, cur + x)\n            mx = max(mx, cur)\n        return mx";
            case 15 -> "class Solution:\n    def trap(self, height):\n        if not height: return 0\n        l, r = 0, len(height) - 1\n        l_max, r_max = height[l], height[r]\n        ans = 0\n        while l < r:\n            if l_max < r_max:\n                l += 1\n                l_max = max(l_max, height[l])\n                ans += max(0, l_max - height[l])\n            else:\n                r -= 1\n                r_max = max(r_max, height[r])\n                ans += max(0, r_max - height[r])\n        return ans";
            case 16 -> "class Solution:\n    def isAnagram(self, s, t):\n        return sorted(s) == sorted(t)";
            case 17 -> "from collections import defaultdict\nclass Solution:\n    def groupAnagrams(self, strs):\n        m = defaultdict(list)\n        for s in strs:\n            m[''.join(sorted(s))].append(s)\n        return list(m.values())";
            case 18 -> "from collections import Counter\nclass Solution:\n    def topKFrequent(self, nums, k):\n        return [x for x, _ in Counter(nums).most_common(k)]";
            case 19 -> "class Codec:\n    def encode(self, strs):\n        return ''.join(f'{len(s)}:{s}' for s in strs)\n    def decode(self, s):\n        res, i = [], 0\n        while i < len(s):\n            j = s.find(':', i)\n            length = int(s[i:j])\n            res.append(s[j+1:j+1+length])\n            i = j + 1 + length\n        return res";
            case 20 -> "class Solution:\n    def longestConsecutive(self, nums):\n        s = set(nums); res = 0\n        for x in s:\n            if x - 1 not in s:\n                y = x + 1\n                while y in s: y += 1\n                res = max(res, y - x)\n        return res";
            case 21 -> "class Solution:\n    def isValidSudoku(self, board):\n        rows = [set() for _ in range(9)]\n        cols = [set() for _ in range(9)]\n        boxes = [set() for _ in range(9)]\n        for r in range(9):\n            for c in range(9):\n                val = board[r][c]\n                if val == '.': continue\n                b = (r // 3) * 3 + (c // 3)\n                if val in rows[r] or val in cols[c] or val in boxes[b]: return False\n                rows[r].add(val); cols[c].add(val); boxes[b].add(val)\n        return True";
            case 22 -> "class Solution:\n    def containsDuplicate(self, nums):\n        return len(nums) != len(set(nums))";
            case 23 -> "class Solution:\n    def findDisappearedNumbers(self, nums):\n        s = set(nums)\n        return [i for i in range(1, len(nums) + 1) if i not in s]";
            case 24 -> "class Solution:\n    def intersection(self, nums1, nums2):\n        return list(set(nums1) & set(nums2))";
            case 25 -> "class Solution:\n    def isIsomorphic(self, s, t):\n        return len(set(zip(s, t))) == len(set(s)) == len(set(t))";
            case 26 -> "class Solution:\n    def isPalindrome(self, s):\n        f = [c.lower() for c in s if c.isalnum()]\n        return f == f[::-1]";
            case 27 -> "class Solution:\n    def threeSum(self, nums):\n        nums.sort()\n        res = []\n        for i in range(len(nums) - 2):\n            if i > 0 and nums[i] == nums[i-1]: continue\n            l, r = i + 1, len(nums) - 1\n            while l < r:\n                tot = nums[i] + nums[l] + nums[r]\n                if tot == 0:\n                    res.append([nums[i], nums[l], nums[r]])\n                    while l < r and nums[l] == nums[l+1]: l += 1\n                    while l < r and nums[r] == nums[r-1]: r -= 1\n                    l += 1; r -= 1\n                elif tot < 0: l += 1\n                else: r -= 1\n        return res";
            case 28 -> "class Solution:\n    def maxArea(self, height):\n        l, r = 0, len(height) - 1\n        ans = 0\n        while l < r:\n            ans = max(ans, min(height[l], height[r]) * (r - l))\n            if height[l] < height[r]: l += 1\n            else: r -= 1\n        return ans";
            case 29 -> "from collections import Counter\nclass Solution:\n    def minWindow(self, s, t):\n        if not t or not s: return \"\"\n        target = Counter(t)\n        window = {}\n        have, need = 0, len(target)\n        res, res_len = [-1, -1], float('inf')\n        l = 0\n        for r, c in enumerate(s):\n            window[c] = window.get(c, 0) + 1\n            if c in target and window[c] == target[c]: have += 1\n            while have == need:\n                if (r - l + 1) < res_len:\n                    res = [l, r]; res_len = r - l + 1\n                window[s[l]] -= 1\n                if s[l] in target and window[s[l]] < target[s[l]]: have -= 1\n                l += 1\n        return s[res[0]:res[1]+1] if res_len != float('inf') else \"\"";
            case 30 -> "class Solution:\n    def characterReplacement(self, s, k):\n        count = {}\n        l = 0; max_f = 0; ans = 0\n        for r, c in enumerate(s):\n            count[c] = count.get(c, 0) + 1\n            max_f = max(max_f, count[c])\n            while (r - l + 1) - max_f > k:\n                count[s[l]] -= 1\n                l += 1\n            ans = max(ans, r - l + 1)\n        return ans";
            case 31 -> "from collections import Counter\nclass Solution:\n    def checkInclusion(self, s1, s2):\n        n1, n2 = len(s1), len(s2)\n        if n1 > n2: return False\n        c1 = Counter(s1)\n        c2 = Counter(s2[:n1])\n        if c1 == c2: return True\n        for i in range(n1, n2):\n            c2[s2[i]] += 1\n            c2[s2[i - n1]] -= 1\n            if c2[s2[i - n1]] == 0: del c2[s2[i - n1]]\n            if c1 == c2: return True\n        return False";
            case 32 -> "class Solution:\n    def findMaxAverage(self, nums, k):\n        cur = sum(nums[:k])\n        mx = cur\n        for i in range(k, len(nums)):\n            cur += nums[i] - nums[i - k]\n            mx = max(mx, cur)\n        return mx / float(k)";
            case 33 -> "from collections import Counter\nclass Solution:\n    def findAnagrams(self, s, p):\n        ns, np = len(s), len(p)\n        if ns < np: return []\n        cp = Counter(p)\n        cs = Counter(s[:np])\n        res = []\n        if cs == cp: res.append(0)\n        for i in range(np, ns):\n            cs[s[i]] += 1\n            cs[s[i - np]] -= 1\n            if cs[s[i - np]] == 0: del cs[s[i - np]]\n            if cs == cp: res.append(i - np + 1)\n        return res";
            case 34 -> "from collections import Counter\nclass Solution:\n    def subarraysWithKDistinct(self, nums, k):\n        def atMost(m):\n            count = Counter()\n            l = 0; ans = 0\n            for r, x in enumerate(nums):\n                if count[x] == 0: m -= 1\n                count[x] += 1\n                while m < 0:\n                    count[nums[l]] -= 1\n                    if count[nums[l]] == 0: m += 1\n                    l += 1\n                ans += r - l + 1\n            return ans\n        return atMost(k) - atMost(k - 1)";
            case 35 -> "from collections import Counter\nclass Solution:\n    def totalFruit(self, fruits):\n        count = Counter()\n        l = 0; ans = 0\n        for r, f in enumerate(fruits):\n            count[f] += 1\n            while len(count) > 2:\n                count[fruits[l]] -= 1\n                if count[fruits[l]] == 0: del count[fruits[l]]\n                l += 1\n            ans = max(ans, r - l + 1)\n        return ans";
            case 36 -> "class MinStack:\n    def __init__(self):\n        self.st = []\n        self.min_st = []\n    def push(self, val: int) -> None:\n        self.st.append(val)\n        if not self.min_st or val <= self.min_st[-1]: self.min_st.append(val)\n    def pop(self) -> None:\n        v = self.st.pop()\n        if v == self.min_st[-1]: self.min_st.pop()\n    def top(self) -> int:\n        return self.st[-1]\n    def getMin(self) -> int:\n        return self.min_st[-1]";
            case 37 -> "class Solution:\n    def evalRPN(self, tokens):\n        st = []\n        for t in tokens:\n            if t not in '+-*/': st.append(int(t))\n            else:\n                b, a = st.pop(), st.pop()\n                if t == '+': st.append(a + b)\n                elif t == '-': st.append(a - b)\n                elif t == '*': st.append(a * b)\n                else: st.append(int(a / b))\n        return st[0]";
            case 38 -> "class Solution:\n    def dailyTemperatures(self, temperatures):\n        res = [0] * len(temperatures)\n        st = []\n        for i, t in enumerate(temperatures):\n            while st and temperatures[st[-1]] < t:\n                prev = st.pop()\n                res[prev] = i - prev\n            st.append(i)\n        return res";
            case 39 -> "class Solution:\n    def largestRectangleArea(self, heights):\n        st = []\n        mx = 0\n        h = heights + [0]\n        for i, val in enumerate(h):\n            while st and h[st[-1]] > val:\n                top = st.pop()\n                w = i if not st else i - st[-1] - 1\n                mx = max(mx, h[top] * w)\n            st.append(i)\n        return mx";
            case 40 -> "class MyQueue:\n    def __init__(self):\n        self.s1 = []\n        self.s2 = []\n    def push(self, x: int) -> None:\n        self.s1.append(x)\n    def pop(self) -> int:\n        self._shift()\n        return self.s2.pop()\n    def peek(self) -> int:\n        self._shift()\n        return self.s2[-1]\n    def empty(self) -> bool:\n        return not self.s1 and not self.s2\n    def _shift(self):\n        if not self.s2:\n            while self.s1: self.s2.append(self.s1.pop())";
            case 41 -> "from collections import deque\nclass Solution:\n    def maxSlidingWindow(self, nums, k):\n        if not nums or k <= 0: return []\n        q = deque()\n        res = []\n        for i, x in enumerate(nums):\n            while q and q[0] < i - k + 1: q.popleft()\n            while q and nums[q[-1]] < x: q.pop()\n            q.append(i)\n            if i >= k - 1: res.append(nums[q[0]])\n        return res";
            case 42 -> "class Solution:\n    def asteroidCollision(self, asteroids):\n        st = []\n        for ast in asteroids:\n            destroyed = False\n            while st and ast < 0 and st[-1] > 0:\n                if st[-1] < -ast:\n                    st.pop(); continue\n                elif st[-1] == -ast: st.pop()\n                destroyed = True; break\n            if not destroyed: st.append(ast)\n        return st";
            case 43 -> "class Solution:\n    def search(self, nums, target):\n        l, r = 0, len(nums) - 1\n        while l <= r:\n            m = (l + r) // 2\n            if nums[m] == target: return m\n            elif nums[m] < target: l = m + 1\n            else: r = m - 1\n        return -1";
            case 44 -> "class Solution:\n    def searchMatrix(self, matrix, target):\n        if not matrix or not matrix[0]: return False\n        R, C = len(matrix), len(matrix[0])\n        l, r = 0, R * C - 1\n        while l <= r:\n            m = (l + r) // 2\n            val = matrix[m // C][m % C]\n            if val == target: return True\n            elif val < target: l = m + 1\n            else: r = m - 1\n        return False";
            case 45 -> "class Solution:\n    def minEatingSpeed(self, piles, h):\n        l, r = 1, max(piles)\n        while l <= r:\n            m = (l + r) // 2\n            if sum((p + m - 1) // m for p in piles) <= h: r = m - 1\n            else: l = m + 1\n        return l";
            case 46 -> "class Solution:\n    def findMin(self, nums):\n        l, r = 0, len(nums) - 1\n        while l < r:\n            m = (l + r) // 2\n            if nums[m] > nums[r]: l = m + 1\n            else: r = m\n        return nums[l]";
            case 47 -> "class Solution:\n    def search(self, nums, target):\n        l, r = 0, len(nums) - 1\n        while l <= r:\n            m = (l + r) // 2\n            if nums[m] == target: return m\n            if nums[l] <= nums[m]:\n                if nums[l] <= target < nums[m]: r = m - 1\n                else: l = m + 1\n            else:\n                if nums[m] < target <= nums[r]: l = m + 1\n                else: r = m - 1\n        return -1";
            case 48 -> "import bisect\nclass TimeMap:\n    def __init__(self):\n        self.m = {}\n    def set(self, key: str, value: str, timestamp: int) -> None:\n        if key not in self.m: self.m[key] = ([], [])\n        self.m[key][0].append(timestamp)\n        self.m[key][1].append(value)\n    def get(self, key: str, timestamp: int) -> str:\n        if key not in self.m: return \"\"\n        times, vals = self.m[key]\n        i = bisect.bisect_right(times, timestamp)\n        return vals[i - 1] if i > 0 else \"\"";
            case 49 -> "class Solution:\n    def splitArray(self, nums, k):\n        l, r = max(nums), sum(nums)\n        while l < r:\n            m = (l + r) // 2\n            cur, pieces = 0, 1\n            for x in nums:\n                if cur + x > m: cur = x; pieces += 1\n                else: cur += x\n            if pieces > k: l = m + 1\n            else: r = m\n        return l";
            case 50 -> "class Solution:\n    def mergeTwoLists(self, list1, list2):\n        dummy = ListNode(0); curr = dummy\n        while list1 and list2:\n            if list1.val <= list2.val:\n                curr.next = list1; list1 = list1.next\n            else:\n                curr.next = list2; list2 = list2.next\n            curr = curr.next\n        curr.next = list1 or list2\n        return dummy.next";
            default -> throw new IllegalArgumentException("Unknown problem: " + pid);
        };
    }

    public static String getWrongPythonSolution(int pid) {
        return switch (pid) {
            case 1 -> "class Solution:\n    def twoSum(self, nums, target):\n        return [-1, -1]";
            case 2 -> "class Solution:\n    def isValid(self, s):\n        return False";
            case 3 -> "class Solution:\n    def lengthOfLongestSubstring(self, s):\n        return -1";
            case 4 -> "class Solution:\n    def merge(self, intervals):\n        return []";
            case 5 -> "class Solution:\n    def canFinish(self, numCourses, prerequisites):\n        return False";
            case 6 -> "class Solution:\n    def findMedianSortedArrays(self, nums1, nums2):\n        return -1.0";
            case 7 -> "class Solution:\n    def ladderLength(self, beginWord, endWord, wordList):\n        return -1";
            case 8 -> "class Solution:\n    def maxProfit(self, prices):\n        return -1";
            case 9 -> "class Solution:\n    def reverseList(self, head):\n        return None";
            case 10 -> "class Solution:\n    def numIslands(self, grid):\n        return -1";
            case 11 -> "class Solution:\n    def coinChange(self, coins, amount):\n        return -99";
            case 12 -> "class Solution:\n    def climbStairs(self, n):\n        return -1";
            case 13 -> "class Solution:\n    def productExceptSelf(self, nums):\n        return [0] * len(nums)";
            case 14 -> "class Solution:\n    def maxSubArray(self, nums):\n        return -999";
            case 15 -> "class Solution:\n    def trap(self, height):\n        return -1";
            case 16 -> "class Solution:\n    def isAnagram(self, s, t):\n        return False";
            case 17 -> "class Solution:\n    def groupAnagrams(self, strs):\n        return []";
            case 18 -> "class Solution:\n    def topKFrequent(self, nums, k):\n        return [9999]";
            case 19 -> "class Codec:\n    def encode(self, strs): return 'wrong'\n    def decode(self, s): return ['wrong']";
            case 20 -> "class Solution:\n    def longestConsecutive(self, nums):\n        return -1";
            case 21 -> "class Solution:\n    def isValidSudoku(self, board):\n        return False";
            case 22 -> "class Solution:\n    def containsDuplicate(self, nums):\n        return False";
            case 23 -> "class Solution:\n    def findDisappearedNumbers(self, nums):\n        return []";
            case 24 -> "class Solution:\n    def intersection(self, nums1, nums2):\n        return [9999]";
            case 25 -> "class Solution:\n    def isIsomorphic(self, s, t):\n        return False";
            case 26 -> "class Solution:\n    def isPalindrome(self, s):\n        return False";
            case 27 -> "class Solution:\n    def threeSum(self, nums):\n        return []";
            case 28 -> "class Solution:\n    def maxArea(self, height):\n        return -1";
            case 29 -> "class Solution:\n    def minWindow(self, s, t):\n        return 'WRONG'";
            case 30 -> "class Solution:\n    def characterReplacement(self, s, k):\n        return -1";
            case 31 -> "class Solution:\n    def checkInclusion(self, s1, s2):\n        return False";
            case 32 -> "class Solution:\n    def findMaxAverage(self, nums, k):\n        return -1.0";
            case 33 -> "class Solution:\n    def findAnagrams(self, s, p):\n        return []";
            case 34 -> "class Solution:\n    def subarraysWithKDistinct(self, nums, k):\n        return -1";
            case 35 -> "class Solution:\n    def totalFruit(self, fruits):\n        return -1";
            case 36 -> "class MinStack:\n    def __init__(self): pass\n    def push(self, val): pass\n    def pop(self): pass\n    def top(self): return 0\n    def getMin(self): return 0";
            case 37 -> "class Solution:\n    def evalRPN(self, tokens):\n        return -99";
            case 38 -> "class Solution:\n    def dailyTemperatures(self, temperatures):\n        return [0] * len(temperatures)";
            case 39 -> "class Solution:\n    def largestRectangleArea(self, heights):\n        return -1";
            case 40 -> "class MyQueue:\n    def __init__(self): pass\n    def push(self, x): pass\n    def pop(self): return -1\n    def peek(self): return -1\n    def empty(self): return True";
            case 41 -> "class Solution:\n    def maxSlidingWindow(self, nums, k):\n        return [0]";
            case 42 -> "class Solution:\n    def asteroidCollision(self, asteroids):\n        return [0]";
            case 43 -> "class Solution:\n    def search(self, nums, target):\n        return -99";
            case 44 -> "class Solution:\n    def searchMatrix(self, matrix, target):\n        return False";
            case 45 -> "class Solution:\n    def minEatingSpeed(self, piles, h):\n        return -1";
            case 46 -> "class Solution:\n    def findMin(self, nums):\n        return -99";
            case 47 -> "class Solution:\n    def search(self, nums, target):\n        return -99";
            case 48 -> "class TimeMap:\n    def __init__(self): pass\n    def set(self, k, v, t): pass\n    def get(self, k, t): return 'wrong'";
            case 49 -> "class Solution:\n    def splitArray(self, nums, k):\n        return -1";
            case 50 -> "class Solution:\n    def mergeTwoLists(self, list1, list2):\n        return None";
            default -> throw new IllegalArgumentException("Unknown problem: " + pid);
        };
    }

    private ExecutionRequest buildJavaReq(Long pid, String code, TestCaseRequest tc) {
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, List.of(tc));
        return new ExecutionRequest(20000L + pid, pid, "java", code, contract);
    }

    private ExecutionRequest buildPythonReq(Long pid, String code, TestCaseRequest tc) {
        ProblemContract contract = new ProblemContract(null, 5000, 5000, 512, List.of(tc));
        return new ExecutionRequest(10000L + pid, pid, "PYTHON_3", code, contract);
    }

    // ==========================================
    // 50 JAVA CORRECT TESTS
    // ==========================================
    @ParameterizedTest(name = "Problem {0} Java Correct Solution -> ACCEPTED")
    @ValueSource(ints = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50
    })
    @DisplayName("Verify known-correct Java algorithmic solution is ACCEPTED")
    public void testJavaAccepted(int pid) {
        TestCaseRequest tc = getTestCaseForProblem(pid);
        String code = getCorrectJavaSolution(pid);
        ExecutionResult res = realJudge.execute(buildJavaReq((long) pid, code, tc));
        assertThat(res.status())
            .as("Problem " + pid + " Java expected ACCEPTED but got " + res.status() + " (err: " + res.systemErrorMessage() + ", compOut: " + res.compilerOutput() + ", runErr: " + res.runtimeErrorOutput() + ")")
            .isEqualTo(SubmissionStatus.ACCEPTED);
    }

    // ==========================================
    // 50 JAVA WRONG ANSWER TESTS
    // ==========================================
    @ParameterizedTest(name = "Problem {0} Java Incorrect Solution -> WRONG_ANSWER")
    @ValueSource(ints = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50
    })
    @DisplayName("Verify known-incorrect Java solution is WRONG_ANSWER")
    public void testJavaWrongAnswer(int pid) {
        TestCaseRequest tc = getTestCaseForProblem(pid);
        String code = getWrongJavaSolution(pid);
        ExecutionResult res = realJudge.execute(buildJavaReq((long) pid, code, tc));
        assertThat(res.status())
            .as("Problem " + pid + " Java expected WRONG_ANSWER but got " + res.status())
            .isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    // ==========================================
    // 50 PYTHON CORRECT TESTS
    // ==========================================
    @ParameterizedTest(name = "Problem {0} Python Correct Solution -> ACCEPTED")
    @ValueSource(ints = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50
    })
    @DisplayName("Verify known-correct Python solution is ACCEPTED")
    public void testPythonAccepted(int pid) {
        TestCaseRequest tc = getTestCaseForProblem(pid);
        String code = getCorrectPythonSolution(pid);
        ExecutionResult res = realJudge.execute(buildPythonReq((long) pid, code, tc));
        assertThat(res.status())
            .as("Problem " + pid + " Python expected ACCEPTED but got " + res.status() + " (err: " + res.systemErrorMessage() + ", compOut: " + res.compilerOutput() + ")")
            .isEqualTo(SubmissionStatus.ACCEPTED);
    }

    // ==========================================
    // 50 PYTHON WRONG ANSWER TESTS
    // ==========================================
    @ParameterizedTest(name = "Problem {0} Python Incorrect Solution -> WRONG_ANSWER")
    @ValueSource(ints = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50
    })
    @DisplayName("Verify known-incorrect Python solution is WRONG_ANSWER")
    public void testPythonWrongAnswer(int pid) {
        TestCaseRequest tc = getTestCaseForProblem(pid);
        String code = getWrongPythonSolution(pid);
        ExecutionResult res = realJudge.execute(buildPythonReq((long) pid, code, tc));
        assertThat(res.status())
            .as("Problem " + pid + " Python expected WRONG_ANSWER but got " + res.status())
            .isEqualTo(SubmissionStatus.WRONG_ANSWER);
    }

    // ==========================================
    // FAILURE MODES FOR JAVA & PYTHON
    // ==========================================
    @Test
    @DisplayName("Verify Java failure modes: COMPILATION_ERROR, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED")
    public void testJavaFailureModes() {
        TestCaseRequest tc = getTestCaseForProblem(1);

        // 1. Java Syntax Error -> COMPILATION_ERROR
        ExecutionResult ce = realJudge.execute(buildJavaReq(1L, "class Solution { syntax error here }", tc));
        assertEquals(SubmissionStatus.COMPILATION_ERROR, ce.status());
        assertThat(ce.compilerOutput()).isNotEmpty();

        // 2. Java Runtime Exception -> RUNTIME_ERROR
        ExecutionResult re = realJudge.execute(buildJavaReq(1L, "class Solution { public int[] twoSum(int[] nums, int target) { throw new RuntimeException(\"Boom\"); } }", tc));
        assertEquals(SubmissionStatus.RUNTIME_ERROR, re.status());
        assertThat(re.runtimeErrorOutput()).contains("RuntimeException");

        // 3. Java Infinite Loop -> TIME_LIMIT_EXCEEDED
        ExecutionResult tle = realJudge.execute(buildJavaReq(1L, "class Solution { public int[] twoSum(int[] nums, int target) { while (true) {} } }", tc));
        assertEquals(SubmissionStatus.TIME_LIMIT_EXCEEDED, tle.status());
    }

    @Test
    @DisplayName("Verify Python failure modes: COMPILATION_ERROR, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED, SYSTEM_ERROR")
    public void testPythonFailureModes() {
        TestCaseRequest tc = getTestCaseForProblem(1);

        // 1. Python SyntaxError -> COMPILATION_ERROR
        ExecutionResult ce = realJudge.execute(buildPythonReq(1L, "class Solution:\n    bad syntax here :(", tc));
        assertEquals(SubmissionStatus.COMPILATION_ERROR, ce.status());
        assertThat(ce.compilerOutput()).contains("SyntaxError");

        // 2. Python Runtime Exception -> RUNTIME_ERROR
        ExecutionResult re = realJudge.execute(buildPythonReq(1L, "class Solution:\n    def twoSum(self, nums, target):\n        return 1 / 0", tc));
        assertEquals(SubmissionStatus.RUNTIME_ERROR, re.status());
        assertThat(re.runtimeErrorOutput()).contains("ZeroDivisionError");

        // 3. Python Infinite Loop -> TIME_LIMIT_EXCEEDED
        ExecutionResult tle = realJudge.execute(buildPythonReq(1L, "class Solution:\n    def twoSum(self, nums, target):\n        while True: pass", tc));
        assertEquals(SubmissionStatus.TIME_LIMIT_EXCEEDED, tle.status());

        // 4. Unsupported Problem ID -> SYSTEM_ERROR
        ExecutionResult sys = realJudge.execute(new ExecutionRequest(
            999L, 51L, "PYTHON_3", "class Solution: pass", new ProblemContract(null, 5000, 5000, 512, List.of(tc))
        ));
        assertEquals(SubmissionStatus.SYSTEM_ERROR, sys.status());
        assertThat(sys.systemErrorMessage()).contains("Unsupported problem for real judge: 51");
    }
}
