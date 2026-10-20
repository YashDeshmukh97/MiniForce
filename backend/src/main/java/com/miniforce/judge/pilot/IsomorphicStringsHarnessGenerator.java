package com.miniforce.judge.pilot;

public class IsomorphicStringsHarnessGenerator {
    
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
               "        boolean res = sol.isIsomorphic(s, t);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
