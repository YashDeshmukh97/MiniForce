package com.miniforce.judge.pilot;

public class ThreeSumHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "import java.util.List;\n" +
               "" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        int[] nums = new int[n];\n" +
               "        for (int i = 0; i < n; i++) {\n" +
               "            nums[i] = sc.nextInt();\n" +
               "        }\n" +
               "        Solution sol = new Solution();\n" +
               "        List<List<Integer>> res = sol.threeSum(nums);\n" +
               "        System.out.print(\"[\");\n" +
               "        if (res != null) {\n" +
               "            for (int i = 0; i < res.size(); i++) {\n" +
               "                System.out.print(\"[\");\n" +
               "                List<Integer> list = res.get(i);\n" +
               "                if (list != null) {\n" +
               "                    for (int j = 0; j < list.size(); j++) {\n" +
               "                        System.out.print(list.get(j));\n" +
               "                        if (j < list.size() - 1) System.out.print(\",\");\n" +
               "                    }\n" +
               "                }\n" +
               "                System.out.print(\"]\");\n" +
               "                if (i < res.size() - 1) System.out.print(\",\");\n" +
               "            }\n" +
               "        }\n" +
               "        System.out.println(\"]\");\n" +
               "    }\n" +
               "}\n";
    }
}
