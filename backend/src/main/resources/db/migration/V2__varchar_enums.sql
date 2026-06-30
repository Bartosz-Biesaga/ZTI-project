ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(64) USING role::text;
ALTER TABLE applications ALTER COLUMN status TYPE VARCHAR(64) USING status::text;

DROP TYPE application_status;
DROP TYPE role;
