package com.miniforce.judge.pilot;

public class WordLadderHarnessGenerator {
    public static String generate() {
        return "" +
            "import java.util.Scanner;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        try {\n" +
            "            Scanner sc = new Scanner(System.in);\n" +
            "            if (!sc.hasNext()) return;\n" +
            "            String beginWord = sc.next();\n" +
            "            String endWord = sc.next();\n" +
            "            int n = sc.nextInt();\n" +
            "            List<String> wordList = new ArrayList<>();\n" +
            "            for (int i = 0; i < n; i++) {\n" +
            "                wordList.add(sc.next());\n" +
            "            }\n" +
            "            Solution sol = new Solution();\n" +
            "            int result = sol.ladderLength(beginWord, endWord, wordList);\n" +
            "            System.out.print(result);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.exit(1);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    }
}
