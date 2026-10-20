package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StringArrayInputCodec implements InputCodec {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            String[] arr = mapper.readValue(rawInput.trim(), String[].class);
            return new Object[]{arr};
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        return rawInput.trim() + "\n";
    }
}
