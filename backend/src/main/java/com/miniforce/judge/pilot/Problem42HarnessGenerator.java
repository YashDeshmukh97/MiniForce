package com.miniforce.judge.pilot;

public class Problem42HarnessGenerator {
    public static String generate(String userCode) {
        return "import java.util.*;\n" +
"\n" +
"public class Main {\n" +
"    public static void main(String[] args) {\n" +
"        Scanner sc = new Scanner(System.in);\n" +
"        int n = sc.nextInt();\n" +
"        int[] ast = new int[n];\n" +
"        for (int i = 0; i < n; i++) ast[i] = sc.nextInt();\n" +
"        int[] res = new Solution().asteroidCollision(ast);\n" +
"        if (res == null) res = new int[0];\n" +
"        System.out.print(\"[\");\n" +
"        for (int i = 0; i < res.length; i++) {\n" +
"            System.out.print(res[i]);\n" +
"            if (i < res.length - 1) System.out.print(\",\");\n" +
"        }\n" +
"        System.out.println(\"]\");\n" +
"    }\n" +
"}\n" +
"";
    }
}
