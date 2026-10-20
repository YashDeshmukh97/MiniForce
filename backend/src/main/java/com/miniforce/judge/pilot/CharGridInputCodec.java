package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CharGridInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            JsonNode root = mapper.readTree(rawInput);
            char[][] grid = new char[root.size()][];
            for (int i = 0; i < root.size(); i++) {
                JsonNode row = root.get(i);
                grid[i] = new char[row.size()];
                for (int j = 0; j < row.size(); j++) {
                    // Check if it's text or int
                    if (row.get(j).isTextual()) {
                        grid[i][j] = row.get(j).asText().charAt(0);
                    } else {
                        grid[i][j] = (char) (row.get(j).asInt() + '0');
                    }
                }
            }
            return new Object[]{grid};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] args = parse(rawInput);
        char[][] grid = (char[][]) args[0];
        StringBuilder sb = new StringBuilder();
        sb.append(grid.length).append("\n");
        for (char[] row : grid) {
            sb.append(row.length).append("\n");
            for (char val : row) {
                sb.append(val).append("\n");
            }
        }
        return sb.toString();
    }
}
