package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TwoIntArrayInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        String[] parts = rawInput.split(";");
        if (parts.length != 2) throw new IllegalArgumentException("Expected two int arrays separated by semicolon");
        
        try {
            JsonNode arr1Node = mapper.readTree(parts[0].trim());
            JsonNode arr2Node = mapper.readTree(parts[1].trim());
            
            int[] arr1 = new int[arr1Node.size()];
            for (int i = 0; i < arr1Node.size(); i++) {
                arr1[i] = arr1Node.get(i).asInt();
            }
            
            int[] arr2 = new int[arr2Node.size()];
            for (int i = 0; i < arr2Node.size(); i++) {
                arr2[i] = arr2Node.get(i).asInt();
            }
            
            return new Object[]{arr1, arr2};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        int[] arr1 = (int[]) parsed[0];
        int[] arr2 = (int[]) parsed[1];
        
        StringBuilder sb = new StringBuilder();
        sb.append(arr1.length).append("\n");
        for (int v : arr1) sb.append(v).append("\n");
        
        sb.append(arr2.length).append("\n");
        for (int v : arr2) sb.append(v).append("\n");
        
        return sb.toString();
    }
}
