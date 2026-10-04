package com.miniforce.judge.pilot;

import java.util.List;

public record Problem1Contract(
    long problemId,
    String targetMethodName,
    List<String> parameterTypes,
    String returnType,
    String invocationStrategy,
    String outputRepresentation,
    String comparatorType
) {
    public static final Problem1Contract INSTANCE = new Problem1Contract(
        1L,
        "twoSum",
        List.of("int[]", "int"),
        "int[]",
        "METHOD",
        "JSON",
        "ARRAY_EXACT"
    );
}
