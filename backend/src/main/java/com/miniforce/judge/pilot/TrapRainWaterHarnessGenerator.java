package com.miniforce.judge.pilot;

public class TrapRainWaterHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        String firstLine = br.readLine();\n" +
               "        if (firstLine == null || firstLine.trim().isEmpty()) return;\n" +
               "        int n = Integer.parseInt(firstLine.trim());\n" +
               "        int[] height = new int[n];\n" +
               "        for (int i = 0; i < n; i++) {\n" +
               "            String l = br.readLine();\n" +
               "            if (l != null && !l.trim().isEmpty()) {\n" +
               "                height[i] = Integer.parseInt(l.trim());\n" +
               "            }\n" +
               "        }\n" +
               "        Solution solution = new Solution();\n" +
               "        int result = solution.trap(height);\n" +
               "        System.out.println(result);\n" +
               "    }\n" +
               "}\n";
    }
}
