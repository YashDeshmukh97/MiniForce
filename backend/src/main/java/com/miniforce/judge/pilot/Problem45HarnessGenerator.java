package com.miniforce.judge.pilot;

public class Problem45HarnessGenerator {
    public static String generate(String userCode) {
        return "import java.util.*;\n" +
"\n" +
"public class Main {\n" +
"    public static void main(String[] args) {\n" +
"        Scanner sc = new Scanner(System.in);\n" +
"        String line = sc.nextLine().trim();\n" +
"        int[] piles = new int[0];\n" +
"        if (!line.isEmpty()) {\n" +
"            String[] parts = line.split(\",\");\n" +
"            piles = new int[parts.length];\n" +
"            for (int i = 0; i < parts.length; i++) piles[i] = Integer.parseInt(parts[i].trim());\n" +
"        }\n" +
"        int h = sc.nextInt();\n" +
"        int res = new Solution().minEatingSpeed(piles, h);\n" +
"        System.out.println(res);\n" +
"    }\n" +
"}\n" +
"";
    }
}
