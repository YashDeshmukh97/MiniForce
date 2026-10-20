package com.miniforce.judge.pilot;

public class NumIslandsHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNextInt()) return;\n" +
            "            int n = sc.nextInt();\n" +
            "            char[][] grid = new char[n][];\n" +
            "            for (int i = 0; i < n; i++) {\n" +
            "                int m = sc.nextInt();\n" +
            "                grid[i] = new char[m];\n" +
            "                for (int j = 0; j < m; j++) {\n" +
            "                    grid[i][j] = sc.next().charAt(0);\n" +
            "                }\n" +
            "            }\n" +
            "            Solution sol = new Solution();\n" +
            "            int result = sol.numIslands(grid);\n" +
            "            System.out.print(result);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.exit(1);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    }
}
