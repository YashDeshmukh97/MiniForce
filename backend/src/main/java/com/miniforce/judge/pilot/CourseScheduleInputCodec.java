package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CourseScheduleInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            String[] parts = rawInput.split(";", 2);
            int numCourses = Integer.parseInt(parts[0].trim());
            JsonNode root = mapper.readTree(parts[1].trim());
            int[][] prerequisites = new int[root.size()][];
            for (int i = 0; i < root.size(); i++) {
                JsonNode row = root.get(i);
                prerequisites[i] = new int[row.size()];
                for (int j = 0; j < row.size(); j++) {
                    prerequisites[i][j] = row.get(j).asInt();
                }
            }
            return new Object[]{numCourses, prerequisites};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] args = parse(rawInput);
        int numCourses = (int) args[0];
        int[][] prerequisites = (int[][]) args[1];
        StringBuilder sb = new StringBuilder();
        sb.append(numCourses).append("\n");
        sb.append(prerequisites.length).append("\n");
        for (int[] row : prerequisites) {
            sb.append(row.length).append("\n");
            for (int val : row) {
                sb.append(val).append("\n");
            }
        }
        return sb.toString();
    }
}
