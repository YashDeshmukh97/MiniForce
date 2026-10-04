package com.miniforce.judge.pilot;

public class LongestSubstringHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNext()) return;\n" +
            "            String s = sc.next();\n" +
            "            Solution sol = new Solution();\n" +
            "            int result = sol.lengthOfLongestSubstring(s);\n" +
            "            System.out.print(result);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.exit(1);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    }
}
