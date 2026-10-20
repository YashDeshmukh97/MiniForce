package com.miniforce.judge.pilot;

public class DailyTemperaturesHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        int[] temperatures = new int[n];\n" +
               "        for (int i = 0; i < n; i++) temperatures[i] = sc.nextInt();\n" +
               "        Solution sol = new Solution();\n" +
               "        int[] res = sol.dailyTemperatures(temperatures);\n" +
               "        System.out.print(\"[\");\n" +
               "        if (res != null) {\n" +
               "            for (int i = 0; i < res.length; i++) {\n" +
               "                System.out.print(res[i]);\n" +
               "                if (i < res.length - 1) System.out.print(\",\");\n" +
               "            }\n" +
               "        }\n" +
               "        System.out.println(\"]\");\n" +
               "    }\n" +
               "}\n";
    }
}
