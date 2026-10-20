package com.miniforce.judge.pilot;

public class Problem49HarnessGenerator {
    public static String generate(String userCode) {
        return "import java.util.*;\n" +
"\n" +
"public class Main {\n" +
"    public static void main(String[] args) {\n" +
"        Scanner sc = new Scanner(System.in);\n" +
"        String line = sc.nextLine().trim();\n" +
"        int[] nums = new int[0];\n" +
"        if (!line.isEmpty()) {\n" +
"            String[] parts = line.split(\",\");\n" +
"            nums = new int[parts.length];\n" +
"            for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i].trim());\n" +
"        }\n" +
"        int k = sc.nextInt();\n" +
"        int res = new Solution().splitArray(nums, k);\n" +
"        System.out.println(res);\n" +
"    }\n" +
"}\n" +
"";
    }
}
