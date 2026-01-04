ALTER TABLE platforms
ADD COLUMN store_id bigint NOT NULL REFERENCES stores (id) ON DELETE CASCADE;

ALTER TABLE platforms DROP CONSTRAINT platforms_name_key;

ALTER TABLE platforms ADD CONSTRAINT platforms_name_store_key UNIQUE (name, store_id);
