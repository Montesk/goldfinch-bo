-- >Role create_table_deliveries

-- !Ups
CREATE TABLE IF NOT EXISTS deliveries
(
    id         bigserial primary key unique,
    product_id bigint REFERENCES products (id) NOT NULL,
    quantity   bigint                          NOT NULL,
    deleted    bool                            NOT NULL default false,
    created_at timestamp                       not null default CURRENT_TIMESTAMP,
    updated_at timestamp                       not null default CURRENT_TIMESTAMP
);

-- !Downs
DROP TABLE IF EXISTS deliveries;
