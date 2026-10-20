package com.miniforce.judge.pilot;

public class Problem44HarnessGenerator {
    public static String generate(String userCode) {
        return "import java.util.*;\n" +
"\n" +
"public class Main {\n" +
"    public static void main(String[] args) {\n" +
"        Scanner sc = new Scanner(System.in);\n" +
"        int rows = sc.nextInt();\n" +
"        int cols = sc.nextInt();\n" +
"        int[][] mat = new int[rows][cols];\n" +
"        for (int i = 0; i < rows; i++) {\n" +
"            for (int j = 0; j < cols; j++) mat[i][j] = sc.nextInt();\n" +
"        }\n" +
"        int target = sc.nextInt();\n" +
"        boolean res = new Solution().searchMatrix(mat, target);\n" +
"        System.out.println(res);\n" +
"    }\n" +
"}\n" +
"";
    }
}
