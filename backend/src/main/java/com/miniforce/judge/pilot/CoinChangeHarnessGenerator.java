package com.miniforce.judge.pilot;

public class CoinChangeHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        String line1 = br.readLine();\n" +
               "        if (line1 == null) return;\n" +
               "        line1 = line1.trim();\n" +
               "        if (line1.startsWith(\"[\") && line1.endsWith(\"]\")) {\n" +
               "            line1 = line1.substring(1, line1.length() - 1).trim();\n" +
               "        }\n" +
               "        int[] coins;\n" +
               "        if (line1.isEmpty()) {\n" +
               "            coins = new int[0];\n" +
               "        } else {\n" +
               "            String[] parts = line1.split(\",\");\n" +
               "            coins = new int[parts.length];\n" +
               "            for (int i = 0; i < parts.length; i++) {\n" +
               "                coins[i] = Integer.parseInt(parts[i].trim());\n" +
               "            }\n" +
               "        }\n" +
               "        String line2 = br.readLine();\n" +
               "        int amount = (line2 != null && !line2.trim().isEmpty()) ? Integer.parseInt(line2.trim()) : 0;\n" +
               "        Solution solution = new Solution();\n" +
               "        int result = solution.coinChange(coins, amount);\n" +
               "        System.out.println(result);\n" +
               "    }\n" +
               "}\n";
    }
}
