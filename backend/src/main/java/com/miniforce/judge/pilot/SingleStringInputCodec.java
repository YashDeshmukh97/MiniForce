package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SingleStringInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            JsonNode root = mapper.readTree(rawInput);
            return new Object[]{root.asText()};
        } catch (Exception e) {
            // fallback if it's not valid JSON
            return new Object[]{rawInput.trim()};
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        return (String) parsed[0];
    }
}
