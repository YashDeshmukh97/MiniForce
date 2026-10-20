package com.miniforce.judge.pilot;

public class Problem48HarnessGenerator {
    public static String generate(String userCode) {
        return "import java.util.*;\n" +
"\n" +
"public class Main {\n" +
"    public static void main(String[] args) {\n" +
"        Scanner sc = new Scanner(System.in);\n" +
"        int n = sc.nextInt();\n" +
"        String[] ops = new String[n];\n" +
"        for (int i = 0; i < n; i++) ops[i] = sc.next();\n" +
"        int argCount = sc.nextInt();\n" +
"        List<String> argList = new ArrayList<>();\n" +
"        for (int i = 0; i < argCount; i++) argList.add(sc.next());\n" +
"        TimeMap obj = null;\n" +
"        System.out.print(\"[\");\n" +
"        int argIdx = 0;\n" +
"        for (int i = 0; i < n; i++) {\n" +
"            String op = ops[i];\n" +
"            if (op.equals(\"TimeMap\")) {\n" +
"                obj = new TimeMap();\n" +
"                System.out.print(\"null\");\n" +
"            } else if (op.equals(\"set\")) {\n" +
"                String key = argList.get(argIdx++);\n" +
"                String val = argList.get(argIdx++);\n" +
"                int time = Integer.parseInt(argList.get(argIdx++));\n" +
"                obj.set(key, val, time);\n" +
"                System.out.print(\"null\");\n" +
"            } else if (op.equals(\"get\")) {\n" +
"                String key = argList.get(argIdx++);\n" +
"                int time = Integer.parseInt(argList.get(argIdx++));\n" +
"                String res = obj.get(key, time);\n" +
"                if (res == null || res.isEmpty()) System.out.print(\"\\\"\\\"\");\n" +
"                else System.out.print(\"\\\"\" + res + \"\\\"\");\n" +
"            }\n" +
"            if (i < n - 1) System.out.print(\",\");\n" +
"        }\n" +
"        System.out.println(\"]\");\n" +
"    }\n" +
"}\n" +
"";
    }
}
