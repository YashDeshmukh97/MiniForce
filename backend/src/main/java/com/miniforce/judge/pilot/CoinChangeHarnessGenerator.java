package com.miniforce.judge.pilot;

public class CoinChangeHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        ObjectMapper mapper = new ObjectMapper();\n" +
               "        int[] coins = mapper.readValue(br.readLine(), int[].class);\n" +
               "        int amount = Integer.parseInt(br.readLine().trim());\n" +
               "        Solution solution = new Solution();\n" +
               "        int result = solution.coinChange(coins, amount);\n" +
               "        System.out.println(mapper.writeValueAsString(result));\n" +
               "    }\n" +
               "}\n";
    }
}
