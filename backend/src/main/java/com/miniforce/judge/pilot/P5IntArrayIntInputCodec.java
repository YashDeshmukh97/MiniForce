package com.miniforce.judge.pilot;

public class P5IntArrayIntInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        String[] parts = rawInput.split(";", 2);
        String arrPart = parts[0].trim();
        if (arrPart.startsWith("[")) arrPart = arrPart.substring(1, arrPart.length() - 1);
        int[] arr;
        if (arrPart.isEmpty()) {
            arr = new int[0];
        } else {
            String[] tokens = arrPart.split(",");
            arr = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                arr[i] = Integer.parseInt(tokens[i].trim());
            }
        }
        int val = Integer.parseInt(parts[1].trim());
        return new Object[]{arr, val};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        int[] arr = (int[]) parsed[0];
        int val = (int) parsed[1];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(",");
        }
        sb.append("\n").append(val).append("\n");
        return sb.toString();
    }
}
