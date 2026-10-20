package com.miniforce.judge.pilot;

public class IntersectionHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n1 = sc.nextInt();\n" +
               "        int[] nums1 = new int[n1];\n" +
               "        for (int i = 0; i < n1; i++) nums1[i] = sc.nextInt();\n" +
               "        int n2 = sc.nextInt();\n" +
               "        int[] nums2 = new int[n2];\n" +
               "        for (int i = 0; i < n2; i++) nums2[i] = sc.nextInt();\n" +
               "        Solution sol = new Solution();\n" +
               "        int[] res = sol.intersection(nums1, nums2);\n" +
               "        System.out.print(\"[\");\n" +
               "        if (res != null) {\n" +
               "            for (int i = 0; i < res.length; i++) {\n" +
               "                System.out.print(res[i]);\n" +
               "                if (i < res.length - 1) System.out.print(\",\");\n" +
               "            }\n" +
               "        }\n" +
               "        System.out.println(\"]\");\n" +
               "    }\n" +
               "}\n";
    }
}
