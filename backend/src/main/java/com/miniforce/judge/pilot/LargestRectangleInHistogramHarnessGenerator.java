package com.miniforce.judge.pilot;

public class LargestRectangleInHistogramHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        int[] heights = new int[n];\n" +
               "        for (int i = 0; i < n; i++) heights[i] = sc.nextInt();\n" +
               "        Solution sol = new Solution();\n" +
               "        int res = sol.largestRectangleArea(heights);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
