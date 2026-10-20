package com.miniforce.judge.pilot;

public class Problem46HarnessGenerator {
    public static String generate(String userCode) {
        return "import java.util.*;\n" +
"\n" +
"public class Main {\n" +
"    public static void main(String[] args) {\n" +
"        Scanner sc = new Scanner(System.in);\n" +
"        int n = sc.nextInt();\n" +
"        int[] nums = new int[n];\n" +
"        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();\n" +
"        int res = new Solution().findMin(nums);\n" +
"        System.out.println(res);\n" +
"    }\n" +
"}\n" +
"";
    }
}
