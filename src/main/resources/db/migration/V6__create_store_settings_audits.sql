create table if not exists store_settings_audits (
    audit_id bigint not null auto_increment,
    settings_id bigint not null,
    previous_values text not null,
    updated_values text not null,
    changed_by varchar(255) not null,
    changed_at timestamp(6) not null,
    primary key (audit_id)
);
