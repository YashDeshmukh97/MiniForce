package com.miniforce.judge.pilot;

public class MinimumWindowSubstringHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextLine()) return;\n" +
               "        String s = sc.nextLine();\n" +
               "        String t = sc.nextLine();\n" +
               "        Solution sol = new Solution();\n" +
               "        String res = sol.minWindow(s, t);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
