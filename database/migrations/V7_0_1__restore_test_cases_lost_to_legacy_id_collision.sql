-- Phase 7.0 reviewed, additive migration. DO NOT run automatically.
--
-- Legacy miniforce.sql declares IDs 27-30 first for problems 14/15 and reuses
-- them for intended cases of problems 16/17 under INSERT IGNORE. The first
-- records win, silently dropping all cases for problem 16 and the public case
-- for problem 17. The mapping is unambiguous in the preserved seed.
--
-- This migration preserves existing IDs 27-30 and semantic ownership. It adds
-- only the missing logical cases with database-generated IDs, and stops if the
-- target database is not exactly the known legacy state.

DELIMITER $$

DROP PROCEDURE IF EXISTS miniforce_phase7_restore_collision_cases$$
CREATE PROCEDURE miniforce_phase7_restore_collision_cases()
BEGIN
    IF (SELECT COUNT(*) FROM problems) <> 100 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Phase 7 migration stopped: expected exactly 100 legacy problems.';
    END IF;

    IF (SELECT COUNT(*) FROM test_cases
        WHERE (id = 27 AND problem_id = 14 AND sample_case = TRUE)
           OR (id = 28 AND problem_id = 14 AND sample_case = FALSE)
           OR (id = 29 AND problem_id = 15 AND sample_case = TRUE)
           OR (id = 30 AND problem_id = 15 AND sample_case = FALSE)) <> 4 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Phase 7 migration stopped: IDs 27-30 do not match the reviewed legacy owners.';
    END IF;

    IF (SELECT COUNT(*) FROM test_cases WHERE problem_id = 16) <> 0
       OR (SELECT COUNT(*) FROM test_cases WHERE problem_id = 17) <> 1
       OR NOT EXISTS (SELECT 1 FROM test_cases WHERE id = 31 AND problem_id = 17 AND sample_case = FALSE) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Phase 7 migration stopped: problems 16/17 are not in the expected legacy state.';
    END IF;

    START TRANSACTION;
    INSERT INTO test_cases(problem_id,input_data,expected_output,sample_case) VALUES
        (16, '"anagram"; "nagaram"', 'true', TRUE),
        (16, '"rat"; "car"', 'false', TRUE),
        (16, '"a"; "ab"', 'false', FALSE),
        (17, '["eat","tea","tan","ate","nat","bat"]', '[["bat"],["nat","tan"],["ate","eat","tea"]]', TRUE);

    IF (SELECT COUNT(*) FROM test_cases WHERE problem_id = 16) <> 3
       OR (SELECT COUNT(*) FROM test_cases WHERE problem_id = 17) <> 2 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Phase 7 migration stopped: restored case counts were not reached.';
    END IF;
    COMMIT;
END$$

CALL miniforce_phase7_restore_collision_cases()$$
DROP PROCEDURE miniforce_phase7_restore_collision_cases$$

DELIMITER ;

SELECT id, problem_id, sample_case
FROM test_cases
WHERE problem_id IN (16, 17)
ORDER BY problem_id, id;
