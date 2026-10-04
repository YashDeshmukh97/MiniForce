package com.miniforce.judge;

import java.util.List;

/**
 * Defines the formal execution contract for a problem.
 * 
 * CONTRACT MATURITY:
 * Currently, only 'ComparatorType' and limits are defined. 
 * Before a problem can be safely executed by a real method-invocation harness, 
 * the following fields are REQUIRED to be added and populated:
 * 
 * - targetMethodName (String): The name of the method to invoke (e.g. "twoSum")
 * - parameterTypes (List<String>): The types of parameters (e.g. ["int[]", "int"])
 * - returnType (String): The expected return type (e.g. "int[]")
 * - invocationStrategy (Enum): e.g. STDIN_STDOUT_HARNESS, METHOD_INVOCATION
 * - inputRepresentation (Enum): e.g. JSON, RAW_STRING
 * - outputRepresentation (Enum): e.g. JSON, RAW_STRING
 * 
 * We do NOT invent these values for the existing 100 problems yet. They must be mapped deterministically.
 */
public record ProblemContract(
    ComparatorType comparator,
    int timeLimitMs,
    int memoryLimitKb,
    int outputLimitKb,
    List<TestCaseRequest> testCases
) {}