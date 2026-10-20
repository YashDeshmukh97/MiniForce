package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class P6ClassOperationsStringInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        String[] parts = rawInput.split(";", 2);
        if (parts.length != 2) throw new IllegalArgumentException("Expected commands and args separated by semicolon");
        
        try {
            JsonNode cmdNode = mapper.readTree(parts[0].trim());
            JsonNode argsNode = mapper.readTree(parts[1].trim());
            
            return new Object[]{cmdNode, argsNode};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        JsonNode cmdNode = (JsonNode) parsed[0];
        JsonNode argsNode = (JsonNode) parsed[1];
        
        StringBuilder sb = new StringBuilder();
        int n = cmdNode.size();
        sb.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            sb.append(cmdNode.get(i).asText()).append(" ");
        }
        sb.append("\n");
        int totalArgs = 0;
        for (int i = 0; i < n; i++) {
            totalArgs += argsNode.get(i).size();
        }
        sb.append(totalArgs).append("\n");
        for (int i = 0; i < n; i++) {
            JsonNode args = argsNode.get(i);
            for (int j = 0; j < args.size(); j++) {
                sb.append(args.get(j).asText()).append(" ");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}
