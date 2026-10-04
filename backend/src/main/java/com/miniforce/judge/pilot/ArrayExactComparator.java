package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArrayExactComparator implements OutputComparator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public boolean compare(String expected, String actual) {
        if (expected == null || actual == null) return false;
        try {
            JsonNode expectedNode = mapper.readTree(expected);
            JsonNode actualNode = mapper.readTree(actual);
            return expectedNode.equals(actualNode);
        } catch (Exception e) {
            return false;
        }
    }
}

