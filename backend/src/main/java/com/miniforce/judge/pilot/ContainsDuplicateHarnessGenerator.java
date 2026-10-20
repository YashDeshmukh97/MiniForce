package com.miniforce.judge.pilot;

public class ContainsDuplicateHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        int[] nums = new int[n];\n" +
               "        for (int i = 0; i < n; i++) {\n" +
               "            nums[i] = sc.nextInt();\n" +
               "        }\n" +
               "        Solution sol = new Solution();\n" +
               "        boolean res = sol.containsDuplicate(nums);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
