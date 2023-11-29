-- liquibase formatted sql

-- changeset belchikdm:1
CREATE TABLE comment
(

    id         BIGSERIAL PRIMARY KEY,
    created_at timestamp,
    text       varchar(255),
    pk_ad      bigint,
    author_id  bigint

);