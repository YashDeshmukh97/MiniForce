package com.miniforce.judge.pilot;

public class ValidSudokuHarnessGenerator {
    
    public String generateHarness(String userCode) {
        return "" +
               "import java.util.Scanner;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextInt()) return;\n" +
               "        int n = sc.nextInt();\n" +
               "        char[][] board = new char[n][];\n" +
               "        for (int i = 0; i < n; i++) {\n" +
               "            int m = sc.nextInt();\n" +
               "            board[i] = new char[m];\n" +
               "            for (int j = 0; j < m; j++) {\n" +
               "                board[i][j] = sc.next().charAt(0);\n" +
               "            }\n" +
               "        }\n" +
               "        Solution sol = new Solution();\n" +
               "        boolean res = sol.isValidSudoku(board);\n" +
               "        System.out.println(res);\n" +
               "    }\n" +
               "}\n";
    }
}
