create table if not exists payment_webhook_events (
    id bigint not null auto_increment,
    event_id varchar(160) not null,
    provider varchar(32) not null,
    payment_intent_id varchar(100) not null,
    outcome varchar(32) not null,
    processed_at datetime(6) not null,
    primary key (id),
    unique key uk_payment_webhook_event_id (event_id)
);
