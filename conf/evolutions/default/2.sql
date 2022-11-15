-- >Role create_products_orders

-- !Ups
CREATE TABLE IF NOT EXISTS products
(
    id         bigserial primary key unique,
    price      numeric   not null default 0.0,
    name       varchar   not null,
    stock      bigint    not null default 0,
    disabled   bool      not null default false,
    created_at timestamp not null default CURRENT_TIMESTAMP,
    updated_at timestamp not null default CURRENT_TIMESTAMP
);

-- !Downs
DROP TABLE IF EXISTS products;
