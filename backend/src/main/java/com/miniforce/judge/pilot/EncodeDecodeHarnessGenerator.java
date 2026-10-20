package com.miniforce.judge.pilot;

public class EncodeDecodeHarnessGenerator {
    public static String generate() {
        return "import java.util.*;\n" +
               "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
               "import com.fasterxml.jackson.core.type.TypeReference;\n" +
               "import java.io.BufferedReader;\n" +
               "import java.io.InputStreamReader;\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) throws Exception {\n" +
               "        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n" +
               "        ObjectMapper mapper = new ObjectMapper();\n" +
               "        List<String> input = mapper.readValue(br.readLine(), new TypeReference<List<String>>() {});\n" +
               "        Codec codec = new Codec();\n" +
               "        String encoded = codec.encode(input);\n" +
               "        List<String> decoded = codec.decode(encoded);\n" +
               "        System.out.println(mapper.writeValueAsString(decoded));\n" +
               "    }\n" +
               "}\n";
    }
}
