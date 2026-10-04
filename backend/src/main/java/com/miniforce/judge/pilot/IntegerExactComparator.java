package com.miniforce.judge.pilot;

public class IntegerExactComparator implements OutputComparator {
    public boolean compare(String expected, String actual) {
        if (expected == null || actual == null) return false;
        try {
            int e = Integer.parseInt(expected.trim());
            int a = Integer.parseInt(actual.trim());
            return e == a;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}

