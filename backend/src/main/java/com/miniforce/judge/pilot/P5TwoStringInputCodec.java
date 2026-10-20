package com.miniforce.judge.pilot;

public class P5TwoStringInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        String[] parts = rawInput.split(";", 2);
        String s1 = parts[0].trim();
        if (s1.startsWith("\"")) s1 = s1.substring(1, s1.length() - 1);
        String s2 = parts[1].trim();
        if (s2.startsWith("\"")) s2 = s2.substring(1, s2.length() - 1);
        return new Object[]{s1, s2};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        return parsed[0] + "\n" + parsed[1] + "\n";
    }
}
