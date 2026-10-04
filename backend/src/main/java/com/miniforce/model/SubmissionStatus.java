package com.miniforce.model;

import java.util.Set;

public enum SubmissionStatus {
    QUEUED,
    COMPILING,
    RUNNING,
    ACCEPTED,
    WRONG_ANSWER,
    COMPILATION_ERROR,
    RUNTIME_ERROR,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    SYSTEM_ERROR;

    public boolean canTransitionTo(SubmissionStatus next) {
        if (this == next) return true;
        
        // Active -> SYSTEM_ERROR is always allowed for catastrophic failures
        if (next == SYSTEM_ERROR && !isTerminal()) {
            return true;
        }

        switch (this) {
            case QUEUED:
                return next == COMPILING;
            case COMPILING:
                return next == RUNNING || next == COMPILATION_ERROR;
            case RUNNING:
                return next == ACCEPTED || next == WRONG_ANSWER || 
                       next == RUNTIME_ERROR || next == TIME_LIMIT_EXCEEDED || 
                       next == MEMORY_LIMIT_EXCEEDED;
            default:
                // Terminal states cannot transition to anything else
                return false;
        }
    }

    public boolean isTerminal() {
        return this != QUEUED && this != COMPILING && this != RUNNING;
    }
}