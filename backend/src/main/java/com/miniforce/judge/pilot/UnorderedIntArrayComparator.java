package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

public class UnorderedIntArrayComparator implements OutputComparator {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean compare(String expected, String actual) {
        try {
            int[] exp = mapper.readValue(expected, int[].class);
            int[] act = mapper.readValue(actual, int[].class);
            if (exp.length != act.length) return false;
            Arrays.sort(exp);
            Arrays.sort(act);
            return Arrays.equals(exp, act);
        } catch (Exception e) {
            return false;
        }
    }
}
