package com.miniforce.judge.pilot;

public class LongestConsecutiveHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNextInt()) return;\n" +
            "            int n = sc.nextInt();\n" +
            "            int[] nums = new int[n];\n" +
            "            for (int i = 0; i < n; i++) {\n" +
            "                nums[i] = sc.nextInt();\n" +
            "            }\n" +
            "            Solution sol = new Solution();\n" +
            "            int result = sol.longestConsecutive(nums);\n" +
            "            System.out.print(result);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.exit(1);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    }
}
