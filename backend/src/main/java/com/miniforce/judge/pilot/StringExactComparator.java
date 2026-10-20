package com.miniforce.judge.pilot;

public class StringExactComparator implements OutputComparator {
    @Override
    public boolean compare(String expected, String actual) {
        if (expected == null || actual == null) return false;
        
        // Strip quotes if present (because actual might be json-serialized, but expected has quotes in DB)
        String expStr = expected.trim();
        String actStr = actual.trim();
        if (expStr.startsWith("\"") && expStr.endsWith("\"") && expStr.length() >= 2) {
            expStr = expStr.substring(1, expStr.length() - 1);
        }
        if (actStr.startsWith("\"") && actStr.endsWith("\"") && actStr.length() >= 2) {
            actStr = actStr.substring(1, actStr.length() - 1);
        }
        
        return expStr.equals(actStr);
    }
}
