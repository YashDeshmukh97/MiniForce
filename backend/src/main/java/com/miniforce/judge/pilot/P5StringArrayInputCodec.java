package com.miniforce.judge.pilot;

public class P5StringArrayInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        String arrPart = rawInput.trim();
        if (arrPart.startsWith("[")) arrPart = arrPart.substring(1, arrPart.length() - 1);
        String[] arr;
        if (arrPart.isEmpty()) {
            arr = new String[0];
        } else {
            String[] tokens = arrPart.split(",");
            arr = new String[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                String s = tokens[i].trim();
                if (s.startsWith("\"")) s = s.substring(1, s.length() - 1);
                arr[i] = s;
            }
        }
        return new Object[]{arr};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        String[] arr = (String[]) parsed[0];
        StringBuilder sb = new StringBuilder();
        sb.append(arr.length).append("\n");
        for (String s : arr) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}
