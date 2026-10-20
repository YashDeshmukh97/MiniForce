package com.miniforce.judge.pilot;

public class Problem50HarnessGenerator {
    public static String generate(String userCode) {
        return "import java.util.*;\n" +
"\n" +
"class ListNode {\n" +
"    int val;\n" +
"    ListNode next;\n" +
"    ListNode() {}\n" +
"    ListNode(int val) { this.val = val; }\n" +
"    ListNode(int val, ListNode next) { this.val = val; this.next = next; }\n" +
"}\n" +
"\n" +
"public class Main {\n" +
"    public static void main(String[] args) {\n" +
"        Scanner sc = new Scanner(System.in);\n" +
"        int n1 = sc.nextInt();\n" +
"        ListNode dummy1 = new ListNode(0);\n" +
"        ListNode curr = dummy1;\n" +
"        for (int i = 0; i < n1; i++) {\n" +
"            curr.next = new ListNode(sc.nextInt());\n" +
"            curr = curr.next;\n" +
"        }\n" +
"        int n2 = sc.nextInt();\n" +
"        ListNode dummy2 = new ListNode(0);\n" +
"        curr = dummy2;\n" +
"        for (int i = 0; i < n2; i++) {\n" +
"            curr.next = new ListNode(sc.nextInt());\n" +
"            curr = curr.next;\n" +
"        }\n" +
"        ListNode res = new Solution().mergeTwoLists(dummy1.next, dummy2.next);\n" +
"        System.out.print(\"[\");\n" +
"        while(res != null) {\n" +
"            System.out.print(res.val);\n" +
"            if (res.next != null) System.out.print(\",\");\n" +
"            res = res.next;\n" +
"        }\n" +
"        System.out.println(\"]\");\n" +
"    }\n" +
"}\n" +
"";
    }
}
