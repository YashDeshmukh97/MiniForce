package com.miniforce.judge.pilot;

public class BooleanExactComparator implements OutputComparator {
    public boolean compare(String expected, String actual) {
        if (expected == null || actual == null) return false;
        String e = expected.trim();
        String a = actual.trim();
        if ((!e.equals("true") && !e.equals("false")) || (!a.equals("true") && !a.equals("false"))) {
            return false;
        }
        return e.equals(a);
    }
}

