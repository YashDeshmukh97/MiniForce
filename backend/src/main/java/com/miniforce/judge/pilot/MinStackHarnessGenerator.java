package com.miniforce.judge.pilot;

public class MinStackHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        MinStack stack = null;\n" +
               "        System.out.print(\"[\");\n" +
               "        for (int i = 0; i < n; i++) {\n" +
               "            String cmd = sc.next();\n" +
               "            int numArgs = sc.nextInt();\n" +
               "            if (cmd.equals(\"MinStack\")) {\n" +
               "                stack = new MinStack();\n" +
               "                System.out.print(\"null\");\n" +
               "            } else if (cmd.equals(\"push\")) {\n" +
               "                int val = sc.nextInt();\n" +
               "                stack.push(val);\n" +
               "                System.out.print(\"null\");\n" +
               "            } else if (cmd.equals(\"pop\")) {\n" +
               "                stack.pop();\n" +
               "                System.out.print(\"null\");\n" +
               "            } else if (cmd.equals(\"top\")) {\n" +
               "                System.out.print(stack.top());\n" +
               "            } else if (cmd.equals(\"getMin\")) {\n" +
               "                System.out.print(stack.getMin());\n" +
               "            }\n" +
               "            if (i < n - 1) System.out.print(\",\");\n" +
               "        }\n" +
               "        System.out.println(\"]\");\n" +
               "    }\n" +
               "}\n";
    }
}
