-- !Ups
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS deleted bool NOT NULL DEFAULT false;

-- !Downs
ALTER TABLE products
    DROP COLUMN IF EXISTS deleted;
