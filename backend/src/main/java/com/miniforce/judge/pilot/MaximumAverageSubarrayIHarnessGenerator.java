package com.miniforce.judge.pilot;

public class MaximumAverageSubarrayIHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextLine()) return;\n" +
               "        String arrStr = sc.nextLine().trim();\n" +
               "        if (arrStr.startsWith(\"[\")) arrStr = arrStr.substring(1, arrStr.length() - 1);\n" +
               "        int[] nums = new int[0];\n" +
               "        if (!arrStr.isEmpty()) {\n" +
               "            String[] parts = arrStr.split(\",\");\n" +
               "            nums = new int[parts.length];\n" +
               "            for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());\n" +
               "        }\n" +
               "        int k = sc.nextInt();\n" +
               "        Solution sol = new Solution();\n" +
               "        double res = sol.findMaxAverage(nums, k);\n" +
               "        System.out.printf(\"%.5f\\n\", res);\n" +
               "    }\n" +
               "}\n";
    }
}
