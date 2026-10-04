package com.miniforce.judge.pilot;

public class ValidParenthesesInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        if (rawInput == null) {
            throw new IllegalArgumentException("Malformed input: empty");
        }
        // DB string doesn't have quotes for these test cases
        return new Object[]{rawInput.trim()};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        if (rawInput == null) return "";
        return rawInput.trim();
    }
}
