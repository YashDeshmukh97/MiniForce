package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MergeIntervalsInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            JsonNode root = mapper.readTree(rawInput);
            int[][] intervals = new int[root.size()][];
            for (int i = 0; i < root.size(); i++) {
                JsonNode row = root.get(i);
                intervals[i] = new int[row.size()];
                for (int j = 0; j < row.size(); j++) {
                    intervals[i][j] = row.get(j).asInt();
                }
            }
            return new Object[]{intervals};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] args = parse(rawInput);
        int[][] intervals = (int[][]) args[0];
        StringBuilder sb = new StringBuilder();
        sb.append(intervals.length).append("\n");
        for (int[] row : intervals) {
            sb.append(row.length).append("\n");
            for (int val : row) {
                sb.append(val).append("\n");
            }
        }
        return sb.toString();
    }
}
