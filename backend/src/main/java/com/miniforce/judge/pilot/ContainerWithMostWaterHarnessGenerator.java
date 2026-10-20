package com.miniforce.judge.pilot;

public class ContainerWithMostWaterHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        int[] height = new int[n];\n" +
               "        for (int i = 0; i < n; i++) {\n" +
               "            height[i] = sc.nextInt();\n" +
               "        }\n" +
               "        Solution sol = new Solution();\n" +
               "        int res = sol.maxArea(height);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
