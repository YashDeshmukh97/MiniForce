package com.miniforce.judge.pilot;

public class TwoSumHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNextInt()) return;\n" +
            "            int n = sc.nextInt();\n" +
            "            int[] nums = new int[n];\n" +
            "            for (int i = 0; i < n; i++) {\n" +
            "                nums[i] = sc.nextInt();\n" +
            "            }\n" +
            "            int target = sc.nextInt();\n" +
            "            Solution sol = new Solution();\n" +
            "            int[] result = sol.twoSum(nums, target);\n" +
            "            System.out.print(\"[\");\n" +
            "            if (result != null) {\n" +
            "                for (int i = 0; i < result.length; i++) {\n" +
            "                    System.out.print(result[i]);\n" +
            "                    if (i < result.length - 1) {\n" +
            "                        System.out.print(\",\");\n" +
            "                    }\n" +
            "                }\n" +
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
