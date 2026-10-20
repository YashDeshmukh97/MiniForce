package com.miniforce.judge.pilot;

public class ReverseLinkedListHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "class ListNode {\n" +
            "    int val;\n" +
            "    ListNode next;\n" +
            "    ListNode() {}\n" +
            "    ListNode(int val) { this.val = val; }\n" +
            "    ListNode(int val, ListNode next) { this.val = val; this.next = next; }\n" +
            "}\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNextInt()) return;\n" +
            "            int n = sc.nextInt();\n" +
            "            ListNode dummy = new ListNode(0);\n" +
            "            ListNode curr = dummy;\n" +
            "            for (int i = 0; i < n; i++) {\n" +
            "                curr.next = new ListNode(sc.nextInt());\n" +
            "                curr = curr.next;\n" +
            "            }\n" +
            "            Solution sol = new Solution();\n" +
            "            ListNode result = sol.reverseList(dummy.next);\n" +
            "            System.out.print(\"[\");\n" +
            "            while (result != null) {\n" +
            "                System.out.print(result.val);\n" +
            "                if (result.next != null) System.out.print(\",\");\n" +
            "                result = result.next;\n" +
            "            }\n" +
            "            System.out.print(\"]\");\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.exit(1);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    }
}
