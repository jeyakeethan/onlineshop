create table if not exists payment_reconciliation_mismatches (
    id bigint not null auto_increment,
    reconciliation_key varchar(220) not null,
    provider varchar(32) not null,
    resource_type varchar(32) not null,
    resource_id varchar(160) not null,
    internal_status varchar(32) not null,
    provider_status varchar(32) not null,
    detected_at datetime(6) not null,
    resolved_at datetime(6) null,
    primary key (id),
    unique key uk_payment_reconciliation_key (reconciliation_key)
);
