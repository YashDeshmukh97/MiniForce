package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringIntInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        String[] parts = rawInput.split(";");
        if (parts.length != 2) throw new IllegalArgumentException("Expected string and int separated by semicolon");
        
        try {
            JsonNode strNode = mapper.readTree(parts[0].trim());
            JsonNode intNode = mapper.readTree(parts[1].trim());
            
            String s = strNode.asText();
            int k = intNode.asInt();
            
            return new Object[]{s, k};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        String s = (String) parsed[0];
        int k = (Integer) parsed[1];
        
        return s + "\n" + k + "\n";
    }
}
