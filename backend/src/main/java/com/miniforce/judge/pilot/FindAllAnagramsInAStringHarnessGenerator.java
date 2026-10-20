package com.miniforce.judge.pilot;

public class FindAllAnagramsInAStringHarnessGenerator {
    public static String generate() {
        return "" +
               "import java.util.Scanner;\n" +
               "import java.util.List;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        Scanner sc = new Scanner(System.in);\n" +
               "        if (!sc.hasNextLine()) return;\n" +
               "        String s = sc.nextLine();\n" +
               "        String p = sc.nextLine();\n" +
               "        Solution sol = new Solution();\n" +
               "        List<Integer> res = sol.findAnagrams(s, p);\n" +
               "        System.out.print(\"[\");\n" +
               "        if (res != null) {\n" +
               "            for (int i = 0; i < res.size(); i++) {\n" +
               "                System.out.print(res.get(i));\n" +
               "                if (i < res.size() - 1) System.out.print(\",\");\n" +
               "            }\n" +
               "        }\n" +
               "        System.out.println(\"]\");\n" +
               "    }\n" +
               "}\n";
    }
}
