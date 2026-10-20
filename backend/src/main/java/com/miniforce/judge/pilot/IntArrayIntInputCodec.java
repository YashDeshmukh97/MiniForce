package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IntArrayIntInputCodec implements InputCodec {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            String[] parts = rawInput.split(";", 2);
            int[] arr = mapper.readValue(parts[0].trim(), int[].class);
            int val = Integer.parseInt(parts[1].trim());
            return new Object[]{arr, val};
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse inputs: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        String[] parts = rawInput.split(";", 2);
        return parts[0].trim() + "\n" + parts[1].trim() + "\n";
    }
}
