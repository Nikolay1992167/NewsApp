--liquibase formatted sql
--changeset Minich:3

CREATE TABLE IF NOT EXISTS news.revisions
(
    id        BIGSERIAL PRIMARY KEY,
    timestamp BIGINT,
    user_id   UUID
);