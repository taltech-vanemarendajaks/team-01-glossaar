--liquibase formatted sql

--changeset glossaar:012 dbms:postgresql
ALTER TABLE categories
    DROP CONSTRAINT categories_name_key;

ALTER TABLE categories
    ADD CONSTRAINT uq_categories_name_user UNIQUE (name, user_id);
