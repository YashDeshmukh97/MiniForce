package com.miniforce.judge.pilot;

public class EvaluateReversePolishNotationHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        String[] tokens = new String[n];\n" +
               "        for (int i = 0; i < n; i++) tokens[i] = sc.next();\n" +
               "        Solution sol = new Solution();\n" +
               "        int res = sol.evalRPN(tokens);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
