package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TwoStringInputCodec implements InputCodec {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            String[] parts = rawInput.split(";", 2);
            String s1 = mapper.readValue(parts[0].trim(), String.class);
            String s2 = mapper.readValue(parts[1].trim(), String.class);
            return new Object[]{s1, s2};
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
