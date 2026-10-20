package com.miniforce.judge.pilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MedianSortedArraysInputCodec implements InputCodec {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object[] parse(String rawInput) {
        try {
            String[] parts = rawInput.split(";", 2);
            JsonNode root1 = mapper.readTree(parts[0].trim());
            int[] nums1 = new int[root1.size()];
            for (int i = 0; i < root1.size(); i++) nums1[i] = root1.get(i).asInt();
            
            JsonNode root2 = mapper.readTree(parts[1].trim());
            int[] nums2 = new int[root2.size()];
            for (int i = 0; i < root2.size(); i++) nums2[i] = root2.get(i).asInt();

            return new Object[]{nums1, nums2};
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed input: " + rawInput, e);
        }
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] args = parse(rawInput);
        int[] nums1 = (int[]) args[0];
        int[] nums2 = (int[]) args[1];
        StringBuilder sb = new StringBuilder();
        sb.append(nums1.length).append("\n");
        for (int val : nums1) sb.append(val).append("\n");
        sb.append(nums2.length).append("\n");
        for (int val : nums2) sb.append(val).append("\n");
        return sb.toString();
    }
}
