-- >Role create_table_warehouses

-- !Ups
CREATE TABLE IF NOT EXISTS warehouses
(
    id          bigserial primary key unique,
    name        varchar   NOT NULL,
    description varchar   NOT NULL default '',
    deleted     bool      NOT NULL default false,
    disabled    bool      NOT NULL default false,
    created_at  timestamp NOT NULL default CURRENT_TIMESTAMP,
    updated_at  timestamp NOT NULL default CURRENT_TIMESTAMP
);

-- !Downs
DROP TABLE IF EXISTS warehouses;
