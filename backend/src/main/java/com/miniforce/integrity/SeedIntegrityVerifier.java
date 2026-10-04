package com.miniforce.integrity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Read-only verifier for the legacy MySQL schema/seed file.
 *
 * <p>This class does not connect to MySQL and never changes the seed. It exists
 * because {@code INSERT IGNORE} can otherwise hide critical seed collisions.
 * A non-zero exit code means a human must review the data before it is used as
 * a clean database baseline.</p>
 */
public final class SeedIntegrityVerifier {
    private SeedIntegrityVerifier() {}

    public static void main(String[] args) throws IOException {
        boolean baseline = args.length > 0 && "--baseline".equals(args[0]);
        String requestedPath = baseline && args.length > 1 ? args[1] : (!baseline && args.length > 0 ? args[0] : "database/miniforce.sql");
        SeedIntegrityReport report = inspect(resolveSeed(Path.of(requestedPath)));
        System.out.print(baseline ? report.renderBaseline() : report.render());
        if (!baseline && report.hasCriticalFailures()) {
            System.exit(1);
        }
    }

    public static SeedIntegrityReport inspect(Path seedPath) throws IOException {
        String sql = Files.readString(seedPath);
        List<ProblemSeed> problems = new ArrayList<>();
        List<TopicSeed> topics = new ArrayList<>();
        List<TopicMapping> mappings = new ArrayList<>();
        List<TestCaseSeed> testCases = new ArrayList<>();

        for (String statement : splitStatements(sql)) {
            String normalized = statement.toLowerCase(Locale.ROOT);
            if (normalized.contains("into problems(")) {
                for (List<String> fields : valueRows(statement)) {
                    if (fields.size() >= 8) {
                        problems.add(new ProblemSeed(longValue(fields.get(0)), textValue(fields.get(1)), textValue(fields.get(2)),
                                textValue(fields.get(3)), textValue(fields.get(4)), textValue(fields.get(5)), textValue(fields.get(6))));
                    }
                }
            } else if (normalized.contains("into topics(")) {
                for (List<String> fields : valueRows(statement)) {
                    if (fields.size() >= 2) topics.add(new TopicSeed(longValue(fields.get(0)), textValue(fields.get(1))));
                }
            } else if (normalized.contains("into problem_topics(")) {
                for (List<String> fields : valueRows(statement)) {
                    if (fields.size() >= 2) mappings.add(new TopicMapping(longValue(fields.get(0)), longValue(fields.get(1))));
                }
            } else if (normalized.contains("into test_cases(")) {
                for (List<String> fields : valueRows(statement)) {
                    if (fields.size() >= 5) {
                        testCases.add(new TestCaseSeed(longValue(fields.get(0)), longValue(fields.get(1)), textValue(fields.get(2)),
                                textValue(fields.get(3)), Boolean.parseBoolean(fields.get(4).trim())));
                    }
                }
            }
        }
        return SeedIntegrityReport.from(problems, topics, mappings, testCases);
    }

    private static Path resolveSeed(Path requested) {
        if (Files.exists(requested)) return requested;
        Path current = Path.of("").toAbsolutePath();
        for (int depth = 0; depth < 5 && current != null; depth++, current = current.getParent()) {
            Path candidate = current.resolve(requested);
            if (Files.exists(candidate)) return candidate;
        }
        return requested;
    }

    private static List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        boolean quoted = false;
        int start = 0;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '\'') {
                if (quoted && i + 1 < sql.length() && sql.charAt(i + 1) == '\'') i++;
                else quoted = !quoted;
            } else if (c == ';' && !quoted) {
                statements.add(sql.substring(start, i + 1));
                start = i + 1;
            }
        }
        return statements;
    }

    private static List<List<String>> valueRows(String statement) {
        int values = statement.toLowerCase(Locale.ROOT).indexOf("values");
        if (values < 0) return List.of();
        String valuesPart = statement.substring(values + "values".length());
        List<List<String>> rows = new ArrayList<>();
        boolean quoted = false;
        int depth = 0;
        int rowStart = -1;
        for (int i = 0; i < valuesPart.length(); i++) {
            char c = valuesPart.charAt(i);
            if (c == '\'') {
                if (quoted && i + 1 < valuesPart.length() && valuesPart.charAt(i + 1) == '\'') i++;
                else quoted = !quoted;
                continue;
            }
            if (quoted) continue;
            if (c == '(') {
                if (depth == 0) rowStart = i + 1;
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0 && rowStart >= 0) {
                    rows.add(splitFields(valuesPart.substring(rowStart, i)));
                    rowStart = -1;
                }
            }
        }
        return rows;
    }

    private static List<String> splitFields(String row) {
        List<String> fields = new ArrayList<>();
        boolean quoted = false;
        int start = 0;
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == '\'') {
                if (quoted && i + 1 < row.length() && row.charAt(i + 1) == '\'') i++;
                else quoted = !quoted;
            } else if (c == ',' && !quoted) {
                fields.add(row.substring(start, i).trim());
                start = i + 1;
            }
        }
        fields.add(row.substring(start).trim());
        return fields;
    }

    private static long longValue(String value) { return Long.parseLong(value.trim()); }

    private static String textValue(String value) {
        String result = value.trim();
        if (result.length() >= 2 && result.startsWith("'") && result.endsWith("'")) {
            result = result.substring(1, result.length() - 1).replace("''", "'");
        }
        return result;
    }

    public record ProblemSeed(long id, String slug, String title, String difficulty, String description, String starterCode, String constraints) {}
    public record TopicSeed(long id, String name) {}
    public record TopicMapping(long problemId, long topicId) {}
    public record TestCaseSeed(long id, long problemId, String inputData, String expectedOutput, boolean sampleCase) {}

    public record SeedIntegrityReport(
            List<ProblemSeed> problems,
            List<TopicSeed> topics,
            List<TopicMapping> mappings,
            List<TestCaseSeed> sourceTestCases,
            Map<Long, List<TestCaseSeed>> duplicateTestCaseIds,
            List<TestCaseSeed> effectiveTestCases,
            List<String> requiredProblemFieldFailures,
            List<TopicMapping> invalidTopicMappings,
            List<TestCaseSeed> orphanTestCases,
            List<String> duplicateLogicalCases,
            Set<Long> missingPublicCases,
            Set<Long> missingHiddenCases,
            List<String> criticalFailures) {

        static SeedIntegrityReport from(List<ProblemSeed> problems, List<TopicSeed> topics, List<TopicMapping> mappings, List<TestCaseSeed> tests) {
            Set<Long> problemIds = problems.stream().map(ProblemSeed::id).collect(Collectors.toCollection(LinkedHashSet::new));
            Set<Long> topicIds = topics.stream().map(TopicSeed::id).collect(Collectors.toCollection(LinkedHashSet::new));
            List<String> fields = problems.stream().filter(p -> blank(p.slug()) || blank(p.title()) || blank(p.difficulty()) || blank(p.description()) || blank(p.starterCode()) || blank(p.constraints()))
                    .map(p -> "problem " + p.id() + " has a missing required field").toList();
            List<TopicMapping> invalidMappings = mappings.stream().filter(m -> !problemIds.contains(m.problemId()) || !topicIds.contains(m.topicId())).toList();
            List<TestCaseSeed> orphanTests = tests.stream().filter(t -> !problemIds.contains(t.problemId())).toList();
            Map<Long, List<TestCaseSeed>> byId = tests.stream().collect(Collectors.groupingBy(TestCaseSeed::id, TreeMap::new, Collectors.toList()));
            Map<Long, List<TestCaseSeed>> duplicateIds = byId.entrySet().stream().filter(e -> e.getValue().size() > 1)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
            List<TestCaseSeed> effective = byId.values().stream().map(rows -> rows.get(0)).toList();
            Map<String, Long> logicalCounts = tests.stream().collect(Collectors.groupingBy(t -> t.problemId() + "\u0000" + t.inputData() + "\u0000" + t.expectedOutput() + "\u0000" + t.sampleCase(), LinkedHashMap::new, Collectors.counting()));
            List<String> duplicateLogical = logicalCounts.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).toList();
            Set<Long> effectiveProblemIds = effective.stream().map(TestCaseSeed::problemId).collect(Collectors.toSet());
            Set<Long> publicProblemIds = effective.stream().filter(TestCaseSeed::sampleCase).map(TestCaseSeed::problemId).collect(Collectors.toSet());
            Set<Long> hiddenProblemIds = effective.stream().filter(t -> !t.sampleCase()).map(TestCaseSeed::problemId).collect(Collectors.toSet());
            Set<Long> missingPublic = problemIds.stream().filter(id -> !publicProblemIds.contains(id)).collect(Collectors.toCollection(LinkedHashSet::new));
            Set<Long> missingHidden = problemIds.stream().filter(id -> !hiddenProblemIds.contains(id)).collect(Collectors.toCollection(LinkedHashSet::new));
            List<String> critical = new ArrayList<>();
            if (problems.size() != 100 || !problemIds.equals(expectedProblemIds())) critical.add("expected exactly problem IDs 1-100");
            if (problems.size() != problemIds.size()) critical.add("duplicate problem IDs");
            if (problems.stream().map(ProblemSeed::slug).collect(Collectors.toSet()).size() != problems.size()) critical.add("duplicate problem slugs");
            if (!fields.isEmpty()) critical.add("required problem fields are missing");
            if (!invalidMappings.isEmpty()) critical.add("invalid problem-topic mapping");
            if (!orphanTests.isEmpty()) critical.add("orphan test case");
            if (!duplicateIds.isEmpty()) critical.add("duplicate test-case IDs: " + duplicateIds.keySet());
            if (!missingPublic.isEmpty()) critical.add("problems without effective public cases: " + missingPublic);
            return new SeedIntegrityReport(List.copyOf(problems), List.copyOf(topics), List.copyOf(mappings), List.copyOf(tests), Map.copyOf(duplicateIds), List.copyOf(effective),
                    List.copyOf(fields), List.copyOf(invalidMappings), List.copyOf(orphanTests), List.copyOf(duplicateLogical), Set.copyOf(missingPublic), Set.copyOf(missingHidden), List.copyOf(critical));
        }

        public boolean hasCriticalFailures() { return !criticalFailures.isEmpty(); }

        public String render() {
            StringBuilder out = new StringBuilder("MiniForce seed integrity report\n");
            out.append("Problems: ").append(problems.size()).append("; topics: ").append(topics.size()).append("; topic mappings: ").append(mappings.size()).append('\n');
            out.append("Test cases declared: ").append(sourceTestCases.size()).append("; effective after legacy INSERT IGNORE semantics: ").append(effectiveTestCases.size()).append('\n');
            out.append("Duplicate test-case IDs: ").append(duplicateTestCaseIds.keySet()).append('\n');
            duplicateTestCaseIds.forEach((id, rows) -> out.append("  ID ").append(id).append(" -> ").append(rows.stream().map(t -> "problem " + t.problemId() + " (" + (t.sampleCase() ? "public" : "hidden") + ")").collect(Collectors.joining(", "))).append('\n'));
            out.append("Missing effective public cases: ").append(missingPublicCases).append('\n');
            out.append("Missing effective hidden cases: ").append(missingHiddenCases).append('\n');
            out.append("Orphan test cases: ").append(orphanTestCases.size()).append("; invalid topic mappings: ").append(invalidTopicMappings.size()).append("; duplicate logical cases: ").append(duplicateLogicalCases.size()).append('\n');
            out.append("RESULT: ").append(hasCriticalFailures() ? "FAIL" : "PASS").append('\n');
            if (hasCriticalFailures()) criticalFailures.forEach(issue -> out.append("  CRITICAL: ").append(issue).append('\n'));
            return out.toString();
        }

        public String renderBaseline() {
            StringBuilder out = new StringBuilder("MiniForce Phase 7 identity baseline (read-only)\nPROBLEMS\n");
            problems.stream().sorted(java.util.Comparator.comparingLong(ProblemSeed::id)).forEach(p -> out.append(p.id()).append('|').append(p.slug()).append('\n'));
            out.append("TEST_CASE_SOURCE_RELATIONSHIPS\n");
            sourceTestCases.forEach(t -> out.append(t.id()).append('|').append(t.problemId()).append('|').append(t.sampleCase() ? "PUBLIC" : "HIDDEN").append('\n'));
            out.append("TOPIC_MAPPINGS\n");
            mappings.forEach(m -> out.append(m.problemId()).append('|').append(m.topicId()).append('\n'));
            return out.toString();
        }

        private static boolean blank(String value) { return value == null || value.isBlank(); }
        private static Set<Long> expectedProblemIds() {
            Set<Long> expected = new LinkedHashSet<>();
            for (long id = 1; id <= 100; id++) expected.add(id);
            return expected;
        }
    }
}
