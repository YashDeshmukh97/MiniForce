package com.miniforce.judge.pilot;

public class ImplementQueueUsingStacksHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        MyQueue queue = null;\n" +
               "        System.out.print(\"[\");\n" +
               "        for (int i = 0; i < n; i++) {\n" +
               "            String cmd = sc.next();\n" +
               "            int numArgs = sc.nextInt();\n" +
               "            if (cmd.equals(\"MyQueue\")) {\n" +
               "                queue = new MyQueue();\n" +
               "                System.out.print(\"null\");\n" +
               "            } else if (cmd.equals(\"push\")) {\n" +
               "                int val = sc.nextInt();\n" +
               "                queue.push(val);\n" +
               "                System.out.print(\"null\");\n" +
               "            } else if (cmd.equals(\"pop\")) {\n" +
               "                System.out.print(queue.pop());\n" +
               "            } else if (cmd.equals(\"peek\")) {\n" +
               "                System.out.print(queue.peek());\n" +
               "            } else if (cmd.equals(\"empty\")) {\n" +
               "                System.out.print(queue.empty());\n" +
               "            }\n" +
               "            if (i < n - 1) System.out.print(\",\");\n" +
               "        }\n" +
               "        System.out.println(\"]\");\n" +
               "    }\n" +
               "}\n";
    }
}
