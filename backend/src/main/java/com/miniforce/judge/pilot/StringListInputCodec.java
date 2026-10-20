package com.miniforce.judge.pilot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class StringListInputCodec implements InputCodec {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            List<String> list = mapper.readValue(rawInput.trim(), new TypeReference<List<String>>() {});
            return new Object[]{list};
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        return rawInput.trim() + "\n";
    }
}
