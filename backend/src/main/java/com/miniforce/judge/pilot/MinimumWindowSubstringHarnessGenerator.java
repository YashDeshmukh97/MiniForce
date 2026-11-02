package com.miniforce.judge.pilot;

public class MinimumWindowSubstringHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextLine()) return;\n" +
               "        String s = sc.nextLine().trim();\n" +
               "        String t = sc.hasNextLine() ? sc.nextLine().trim() : \"\";\n" +
               "        if (s.startsWith(\"\\\"\") && s.endsWith(\"\\\"\") && s.length() >= 2) s = s.substring(1, s.length() - 1);\n" +
               "        if (t.startsWith(\"\\\"\") && t.endsWith(\"\\\"\") && t.length() >= 2) t = t.substring(1, t.length() - 1);\n" +
               "        Solution sol = new Solution();\n" +
               "        String res = sol.minWindow(s, t);\n" +
               "        System.out.println(res != null ? res : \"\");\n" +
               "    }\n" +
               "}\n";
    }
}
