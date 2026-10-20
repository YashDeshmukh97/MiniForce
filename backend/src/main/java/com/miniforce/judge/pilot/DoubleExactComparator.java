package com.miniforce.judge.pilot;

public class DoubleExactComparator implements OutputComparator {
    @Override
    public boolean compare(String expected, String actual) {
        if (expected == null || actual == null) return false;
        try {
            double e = Double.parseDouble(expected.trim());
            double a = Double.parseDouble(actual.trim());
            if (Double.isNaN(e) || Double.isNaN(a) || Double.isInfinite(e) || Double.isInfinite(a)) {
                return false;
            }
            return Math.abs(e - a) < 1e-5;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
