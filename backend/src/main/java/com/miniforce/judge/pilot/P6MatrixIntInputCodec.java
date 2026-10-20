package com.miniforce.judge.pilot;

public class P6MatrixIntInputCodec implements InputCodec {
    @Override
    public Object[] parse(String rawInput) {
        // format: "[[1,3,5,7],[10,11,16,20],[23,30,34,60]]; 3"
        String[] parts = rawInput.split(";");
        String matrixPart = parts[0].trim();
        int target = Integer.parseInt(parts[1].trim());

        if (matrixPart.startsWith("[[")) matrixPart = matrixPart.substring(2, matrixPart.length() - 2);
        else if (matrixPart.equals("[]")) {
            return new Object[]{new int[0][0], target};
        }

        String[] rowStrs = matrixPart.split("\\],\\[");
        int rows = rowStrs.length;
        int cols = 0;
        int[][] matrix = null;
        
        for (int i = 0; i < rows; i++) {
            String rowStr = rowStrs[i].trim();
            if (rowStr.isEmpty()) {
                if (matrix == null) matrix = new int[rows][0];
                continue;
            }
            String[] tokens = rowStr.split(",");
            if (matrix == null) {
                cols = tokens.length;
                matrix = new int[rows][cols];
            }
            for (int j = 0; j < tokens.length; j++) {
                matrix[i][j] = Integer.parseInt(tokens[j].trim());
            }
        }
        if (matrix == null) matrix = new int[0][0];

        return new Object[]{matrix, target};
    }

    @Override
    public String serializeForHarness(String rawInput) {
        Object[] parsed = parse(rawInput);
        int[][] matrix = (int[][]) parsed[0];
        int target = (int) parsed[1];
        
        StringBuilder sb = new StringBuilder();
        int rows = matrix.length;
        int cols = rows > 0 ? matrix[0].length : 0;
        sb.append(rows).append("\n");
        sb.append(cols).append("\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(matrix[i][j]).append(" ");
            }
            sb.append("\n");
        }
        sb.append(target).append("\n");
        return sb.toString();
    }
}
