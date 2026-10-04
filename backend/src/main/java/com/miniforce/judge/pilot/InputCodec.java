package com.miniforce.judge.pilot;

public interface InputCodec {
    Object[] parse(String rawInput);
    String serializeForHarness(String rawInput);
}
