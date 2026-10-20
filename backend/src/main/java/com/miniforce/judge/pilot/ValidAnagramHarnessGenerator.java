package com.miniforce.judge.pilot;

public class ValidAnagramHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        ObjectMapper mapper = new ObjectMapper();\n" +
               "        String s = mapper.readValue(br.readLine(), String.class);\n" +
               "        String t = mapper.readValue(br.readLine(), String.class);\n" +
               "        Solution solution = new Solution();\n" +
               "        boolean result = solution.isAnagram(s, t);\n" +
               "        System.out.println(mapper.writeValueAsString(result));\n" +
               "    }\n" +
               "}\n";
    }
}
