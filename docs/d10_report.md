### PHASE D.10 STATUS
BLOCKED

### DATA AUDIT

Problem 2:
- ID: 2
- Slug: valid-parentheses
- Starter Code: `class Solution { public boolean isValid(String s) { return false; } }`
- Target Method: `isValid`
- Parameter Types: `String`
- Return Type: `boolean`
- Invocation Style: Instance method
- Test Cases in SQL:
  - `(3, 2, '()[]{}', 'true', TRUE)`
  - `(4, 2, '(]', 'false', FALSE)`
- Deterministic: YES (Supported by BOOLEAN_EXACT)

Problem 3:
- ID: 3
- Slug: longest-substring-without-repeating-characters
- Starter Code: `class Solution { public int lengthOfLongestSubstring(String s) { return 0; } }`
- Target Method: `lengthOfLongestSubstring`
- Parameter Types: `String`
- Return Type: `int`
- Invocation Style: Instance method
- Test Cases in SQL:
  - `(5, 3, 'abcabcbb', '3', TRUE)`
  - `(6, 3, 'bbbbb', '1', FALSE)`
- Deterministic: YES (Supported by INTEGER_EXACT)

Problem 17:
- ID: 17
- Slug: group-anagrams
- Target Method: `groupAnagrams`
- Parameter Types: `String[]`
- Return Type: `List<List<String>>`
- Test Cases in SQL:
  - `(30, 17, '["eat","tea","tan","ate","nat","bat"]', '[["bat"],["nat","tan"],["ate","eat","tea"]]', TRUE)`
  - `(31, 17, '[""]', '[[""]]', FALSE)`
- Deterministic: NO. The output ordering of groups and the elements within groups is inherently ambiguous, but the `expected_output` is stored as a strict literal string. A simple ARRAY_EXACT comparison will fail depending on the submission's hash order.

### DUPLICATE TEST-ID IMPACT
Test IDs 27, 28, 29, and 30 are explicitly duplicated in the database seed (`miniforce.sql`).
- ID 27: Claimed by Problem 14, duplicated by Problem 16
- ID 28: Claimed by Problem 14, duplicated by Problem 16
- ID 29: Claimed by Problem 15, duplicated by Problem 16
- ID 30: Claimed by Problem 15, duplicated by Problem 17

Because the seed script uses `INSERT IGNORE`, the insertions for Problem 16 and Problem 17 silently fail for these IDs. 
This means Problem 17 is missing its primary sample test case (ID 30) in the live database. This creates an unresolved data ambiguity.

### CONTRACTS
(Skipped due to STOP condition)

### INPUT CODECS
(Skipped due to STOP condition)

### HARNESS
(Skipped due to STOP condition)

### COMPARATORS
(Skipped due to STOP condition)

### REAL JUDGE ROUTING
(Skipped due to STOP condition)

### PROBLEM 1
(Unmodified, baseline preserved)

### PROBLEM 2
(Audit passed, but implementation halted due to Phase D.10 STOP conditions)

### PROBLEM 3
(Audit passed, but implementation halted due to Phase D.10 STOP conditions)

### PROBLEM 17
(BLOCKED - Ambiguous output ordering and missing test data due to ID collision)

### SECURITY
(Unmodified)

### DATABASE/DATA CHANGES
(None. The database remains strictly READ ONLY.)

### TEST RESULTS
(Baseline preserved - 57/57 tests passing. No new tests added due to STOP condition)

### FILES CHANGED
(None)

### REMAINING LIMITATIONS
- The duplicate test case IDs (27-30) in `miniforce.sql` must be resolved before Problem 17 can be safely tested.
- We must define a deep, unordered `Set<Multiset<String>>` comparator for Problem 17, as exact string matching is fundamentally unsafe for anagram groupings.
