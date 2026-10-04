package com.miniforce.judge.pilot;

public record PilotResult(
    String verdict,
    String actualOutput,
    String systemError
) {}
