CREATE TYPE role AS ENUM ('CANDIDATE', 'COMPANY', 'ADMIN');
CREATE TYPE application_status AS ENUM ('NEW', 'SCREENING', 'INTERVIEW', 'OFFER', 'REJECTED');

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role        role NOT NULL
);

CREATE TABLE candidates (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL UNIQUE REFERENCES users (id),
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE companies (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users (id),
    name    VARCHAR(255) NOT NULL
);

CREATE TABLE skills (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE job_offers (
    id          BIGSERIAL PRIMARY KEY,
    company_id  BIGINT NOT NULL REFERENCES companies (id),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    min_salary  DOUBLE PRECISION,
    max_salary  DOUBLE PRECISION,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE applications (
    id            BIGSERIAL PRIMARY KEY,
    candidate_id  BIGINT NOT NULL REFERENCES candidates (id),
    job_offer_id  BIGINT NOT NULL REFERENCES job_offers (id),
    status        application_status NOT NULL DEFAULT 'NEW',
    company_notes TEXT,
    applied_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_application_candidate_offer UNIQUE (candidate_id, job_offer_id)
);

CREATE TABLE job_offer_skill (
    offer_id BIGINT NOT NULL REFERENCES job_offers (id) ON DELETE CASCADE,
    skill_id BIGINT NOT NULL REFERENCES skills (id) ON DELETE CASCADE,
    PRIMARY KEY (offer_id, skill_id)
);

CREATE TABLE candidate_skill (
    candidate_id BIGINT NOT NULL REFERENCES candidates (id) ON DELETE CASCADE,
    skill_id     BIGINT NOT NULL REFERENCES skills (id) ON DELETE CASCADE,
    PRIMARY KEY (candidate_id, skill_id)
);

CREATE INDEX idx_job_offers_company_id ON job_offers (company_id);
CREATE INDEX idx_applications_candidate_id ON applications (candidate_id);
CREATE INDEX idx_applications_job_offer_id ON applications (job_offer_id);
CREATE INDEX idx_job_offer_skill_skill_id ON job_offer_skill (skill_id);
CREATE INDEX idx_candidate_skill_skill_id ON candidate_skill (skill_id);
