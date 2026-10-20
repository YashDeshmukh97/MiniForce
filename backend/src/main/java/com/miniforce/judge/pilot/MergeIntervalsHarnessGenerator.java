package com.miniforce.judge.pilot;

public class MergeIntervalsHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNextInt()) return;\n" +
            "            int n = sc.nextInt();\n" +
            "            int[][] intervals = new int[n][];\n" +
            "            for (int i = 0; i < n; i++) {\n" +
            "                int m = sc.nextInt();\n" +
            "                intervals[i] = new int[m];\n" +
            "                for (int j = 0; j < m; j++) {\n" +
            "                    intervals[i][j] = sc.nextInt();\n" +
            "                }\n" +
            "            }\n" +
            "            Solution sol = new Solution();\n" +
            "            int[][] result = sol.merge(intervals);\n" +
            "            System.out.print(\"[\");\n" +
            "            if (result != null) {\n" +
            "                for (int i = 0; i < result.length; i++) {\n" +
            "                    System.out.print(\"[\");\n" +
            "                    if (result[i] != null) {\n" +
            "                        for (int j = 0; j < result[i].length; j++) {\n" +
            "                            System.out.print(result[i][j]);\n" +
            "                            if (j < result[i].length - 1) System.out.print(\",\");\n" +
            "                        }\n" +
            "                    }\n" +
            "                    System.out.print(\"]\");\n" +
            "                    if (i < result.length - 1) System.out.print(\",\");\n" +
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
