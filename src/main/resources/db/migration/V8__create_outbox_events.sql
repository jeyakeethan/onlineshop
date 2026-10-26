create table if not exists outbox_events (
    event_id bigint not null auto_increment,
    deduplication_key varchar(255) not null,
    event_type varchar(80) not null,
    status varchar(80) not null,
    payload text not null,
    attempts int not null,
    created_at timestamp(6) not null,
    next_attempt_at timestamp(6) not null,
    published_at timestamp(6),
    last_error varchar(500),
    primary key (event_id),
    unique key uk_outbox_deduplication_key (deduplication_key)
);
