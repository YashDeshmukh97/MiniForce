ALTER TABLE submissions
ADD COLUMN failing_test_case_id BIGINT NULL,
ADD COLUMN runtime_error_output MEDIUMTEXT NULL,
ADD COLUMN system_error_message MEDIUMTEXT NULL;