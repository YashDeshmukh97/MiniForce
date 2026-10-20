package com.miniforce.judge.pilot;

public class MedianSortedArraysHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNextInt()) return;\n" +
            "            int n1 = sc.nextInt();\n" +
            "            int[] nums1 = new int[n1];\n" +
            "            for (int i = 0; i < n1; i++) nums1[i] = sc.nextInt();\n" +
            "            int n2 = sc.nextInt();\n" +
            "            int[] nums2 = new int[n2];\n" +
            "            for (int i = 0; i < n2; i++) nums2[i] = sc.nextInt();\n" +
            "            Solution sol = new Solution();\n" +
            "            double result = sol.findMedianSortedArrays(nums1, nums2);\n" +
            "            System.out.print(result);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.exit(1);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    }
}
