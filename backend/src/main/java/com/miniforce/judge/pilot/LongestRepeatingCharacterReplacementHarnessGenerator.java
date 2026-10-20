package com.miniforce.judge.pilot;

public class LongestRepeatingCharacterReplacementHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextLine()) return;\n" +
               "        String s = sc.nextLine();\n" +
               "        int k = sc.nextInt();\n" +
               "        Solution sol = new Solution();\n" +
               "        int res = sol.characterReplacement(s, k);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
