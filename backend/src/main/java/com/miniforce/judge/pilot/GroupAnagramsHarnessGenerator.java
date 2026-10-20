package com.miniforce.judge.pilot;

public class GroupAnagramsHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        ObjectMapper mapper = new ObjectMapper();\n" +
               "        String[] strs = mapper.readValue(br.readLine(), String[].class);\n" +
               "        Solution solution = new Solution();\n" +
               "        List<List<String>> result = solution.groupAnagrams(strs);\n" +
               "        System.out.println(mapper.writeValueAsString(result));\n" +
               "    }\n" +
               "}\n";
    }
}
