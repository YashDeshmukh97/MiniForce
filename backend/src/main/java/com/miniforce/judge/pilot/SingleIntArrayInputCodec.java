package com.miniforce.judge.pilot;

public class SingleIntArrayInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Malformed input: empty");
        }
        
        String arrPart = rawInput.trim();
        if (!arrPart.startsWith("[") || !arrPart.endsWith("]")) {
            throw new IllegalArgumentException("Malformed input: expected array inside []");
        }
        
        String inner = arrPart.substring(1, arrPart.length() - 1).trim();
        int[] nums;
        if (inner.isEmpty()) {
            nums = new int[0];
        } else {
            String[] strNums = inner.split(",");
            nums = new int[strNums.length];
            for (int i = 0; i < strNums.length; i++) {
                nums[i] = Integer.parseInt(strNums[i].trim());
            }
        }
        
        return new Object[]{nums};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] args = parse(rawInput);
        int[] nums = (int[]) args[0];
        
        StringBuilder sb = new StringBuilder();
        sb.append(nums.length).append("\n");
        for (int n : nums) {
            sb.append(n).append("\n");
        }
        return sb.toString();
    }
}
