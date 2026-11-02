package com.miniforce.judge.pilot;

public class GroupAnagramsHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "import java.util.regex.Matcher;\n" +
               "import java.util.regex.Pattern;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        String line = br.readLine();\n" +
               "        List<String> list = new ArrayList<>();\n" +
               "        if (line != null) {\n" +
               "            Matcher m = Pattern.compile(\"\\\"([^\\\"]*)\\\"\").matcher(line);\n" +
               "            while (m.find()) {\n" +
               "                list.add(m.group(1));\n" +
               "            }\n" +
               "        }\n" +
               "        String[] strs = list.toArray(new String[0]);\n" +
               "        Solution solution = new Solution();\n" +
               "        List<List<String>> result = solution.groupAnagrams(strs);\n" +
               "        StringBuilder sb = new StringBuilder(\"[\");\n" +
               "        if (result != null) {\n" +
               "            for (int i = 0; i < result.size(); i++) {\n" +
               "                sb.append(\"[\");\n" +
               "                List<String> group = result.get(i);\n" +
               "                if (group != null) {\n" +
               "                    for (int j = 0; j < group.size(); j++) {\n" +
               "                        sb.append(\"\\\"\").append(group.get(j)).append(\"\\\"\");\n" +
               "                        if (j < group.size() - 1) sb.append(\",\");\n" +
               "                    }\n" +
               "                }\n" +
               "                sb.append(\"]\");\n" +
               "                if (i < result.size() - 1) sb.append(\",\");\n" +
               "            }\n" +
               "        }\n" +
               "        sb.append(\"]\");\n" +
               "        System.out.println(sb.toString());\n" +
               "    }\n" +
               "}\n";
    }
}
