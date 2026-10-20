package com.miniforce.judge.pilot;

public class P6TwoIntArrayInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        // format: "[1,2,4]; [1,3,4]"
        String[] parts = rawInput.split(";");
        String a1 = parts[0].trim();
        String a2 = parts[1].trim();

        if (a1.startsWith("[")) a1 = a1.substring(1, a1.length() - 1);
        if (a2.startsWith("[")) a2 = a2.substring(1, a2.length() - 1);
        
        int[] arr1 = new int[0];
        if (!a1.isEmpty()) {
            String[] tokens = a1.split(",");
            arr1 = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                arr1[i] = Integer.parseInt(tokens[i].trim());
            }
        }
        
        int[] arr2 = new int[0];
        if (!a2.isEmpty()) {
            String[] tokens = a2.split(",");
            arr2 = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                arr2[i] = Integer.parseInt(tokens[i].trim());
            }
        }
        return new Object[]{arr1, arr2};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        int[] arr1 = (int[]) parsed[0];
        int[] arr2 = (int[]) parsed[1];
        
        StringBuilder sb = new StringBuilder();
        sb.append(arr1.length).append("\n");
        for (int x : arr1) sb.append(x).append(" ");
        sb.append("\n");
        sb.append(arr2.length).append("\n");
        for (int x : arr2) sb.append(x).append(" ");
        sb.append("\n");
        return sb.toString();
    }
}
