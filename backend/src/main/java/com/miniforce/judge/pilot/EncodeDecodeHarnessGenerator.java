package com.miniforce.judge.pilot;

public class EncodeDecodeHarnessGenerator {
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
               "        List<String> input = new ArrayList<>();\n" +
               "        if (line != null) {\n" +
               "            Matcher m = Pattern.compile(\"\\\"([^\\\"]*)\\\"\").matcher(line);\n" +
               "            while (m.find()) {\n" +
               "                input.add(m.group(1));\n" +
               "            }\n" +
               "        }\n" +
               "        Codec codec = new Codec();\n" +
               "        String encoded = codec.encode(input);\n" +
               "        List<String> decoded = codec.decode(encoded);\n" +
               "        StringBuilder sb = new StringBuilder(\"[\");\n" +
               "        if (decoded != null) {\n" +
               "            for (int i = 0; i < decoded.size(); i++) {\n" +
               "                sb.append(\"\\\"\").append(decoded.get(i)).append(\"\\\"\");\n" +
               "                if (i < decoded.size() - 1) sb.append(\",\");\n" +
               "            }\n" +
               "        }\n" +
               "        sb.append(\"]\");\n" +
               "        System.out.println(sb.toString());\n" +
               "    }\n" +
               "}\n";
    }
}
