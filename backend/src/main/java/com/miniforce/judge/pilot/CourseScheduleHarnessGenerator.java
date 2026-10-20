package com.miniforce.judge.pilot;

public class CourseScheduleHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "import java.util.*;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNextInt()) return;\n" +
            "            int numCourses = sc.nextInt();\n" +
            "            int n = sc.nextInt();\n" +
            "            int[][] prerequisites = new int[n][];\n" +
            "            for (int i = 0; i < n; i++) {\n" +
            "                int m = sc.nextInt();\n" +
            "                prerequisites[i] = new int[m];\n" +
            "                for (int j = 0; j < m; j++) {\n" +
            "                    prerequisites[i][j] = sc.nextInt();\n" +
            "                }\n" +
            "            }\n" +
            "            Solution sol = new Solution();\n" +
            "            boolean result = sol.canFinish(numCourses, prerequisites);\n" +
            "            System.out.print(result);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.exit(1);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    }
}
