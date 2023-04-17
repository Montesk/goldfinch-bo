-- >Role create_table_warehouses_product

-- !Ups
CREATE TABLE IF NOT EXISTS warehouses_product
(
    id           bigserial primary key unique,
    product_id   bigint    REFERENCES products (id) NOT NULL,
    warehouse_id bigint    REFERENCES warehouses (id) NOT NULL,
    deleted      bool      NOT NULL default false,
    disabled     bool      NOT NULL default false,
    created_at   timestamp NOT NULL default CURRENT_TIMESTAMP,
    updated_at   timestamp NOT NULL default CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS warehouses_product_warehouse_product ON warehouses_product (product_id, warehouse_id);

-- !Downs
DROP INDEX IF EXISTS warehouses_product_warehouse_product;
DROP TABLE IF EXISTS warehouses_product;
