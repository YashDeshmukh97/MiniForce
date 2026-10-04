ALTER TABLE problems ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT TRUE;
UPDATE problems SET is_public = FALSE WHERE id > 50;
ALTER TABLE problems ADD COLUMN starter_code_python TEXT;
