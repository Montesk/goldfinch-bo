-- >Role add_columns_uuid_description_to_products_table

-- !Ups
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid();

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS description varchar;

-- !Downs
ALTER TABLE products
    DROP COLUMN IF EXISTS uuid;

ALTER TABLE products
    DROP column IF EXISTS description;