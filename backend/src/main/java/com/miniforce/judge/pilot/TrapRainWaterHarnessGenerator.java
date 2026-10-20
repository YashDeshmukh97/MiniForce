package com.miniforce.judge.pilot;

public class TrapRainWaterHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        ObjectMapper mapper = new ObjectMapper();\n" +
               "        // Skip length line for single array if SingleIntArrayInputCodec outputs it\n" +
               "        br.readLine();\n" +
               "        List<Integer> list = new ArrayList<>();\n" +
               "        String line;\n" +
               "        while ((line = br.readLine()) != null) {\n" +
               "            list.add(Integer.parseInt(line.trim()));\n" +
               "        }\n" +
               "        int[] height = new int[list.size()];\n" +
               "        for(int i = 0; i < list.size(); i++) height[i] = list.get(i);\n" +
               "        Solution solution = new Solution();\n" +
               "        int result = solution.trap(height);\n" +
               "        System.out.println(mapper.writeValueAsString(result));\n" +
               "    }\n" +
               "}\n";
    }
}
