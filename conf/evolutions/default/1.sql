-- !Ups
CREATE TABLE IF NOT EXISTS orders (
    id bigserial primary key unique,
    status varchar not null default 'new',
    customer_id bigint not null,
    created_at timestamp not null default CURRENT_TIMESTAMP,
    updated_at timestamp not null default CURRENT_TIMESTAMP
);

-- !Downs
DROP TABLE IF EXISTS orders;