package com.miniforce.judge.pilot;

public class ValidAnagramHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        String s = br.readLine();\n" +
               "        String t = br.readLine();\n" +
               "        if (s == null) s = \"\";\n" +
               "        if (t == null) t = \"\";\n" +
               "        s = s.trim();\n" +
               "        t = t.trim();\n" +
               "        if (s.startsWith(\"\\\"\") && s.endsWith(\"\\\"\") && s.length() >= 2) s = s.substring(1, s.length() - 1);\n" +
               "        if (t.startsWith(\"\\\"\") && t.endsWith(\"\\\"\") && t.length() >= 2) t = t.substring(1, t.length() - 1);\n" +
               "        Solution solution = new Solution();\n" +
               "        boolean result = solution.isAnagram(s, t);\n" +
               "        System.out.println(result);\n" +
               "    }\n" +
               "}\n";
    }
}
