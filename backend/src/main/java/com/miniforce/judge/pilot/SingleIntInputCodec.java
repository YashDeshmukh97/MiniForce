package com.miniforce.judge.pilot;

public class SingleIntInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Malformed input: empty");
        }
        int val = Integer.parseInt(rawInput.trim());
        return new Object[]{val};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] args = parse(rawInput);
        int val = (int) args[0];
        return val + "\n";
    }
}
