package com.miniforce.judge.pilot;

public class PermutationInStringHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextLine()) return;\n" +
               "        String s1 = sc.nextLine();\n" +
               "        String s2 = sc.nextLine();\n" +
               "        Solution sol = new Solution();\n" +
               "        boolean res = sol.checkInclusion(s1, s2);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
