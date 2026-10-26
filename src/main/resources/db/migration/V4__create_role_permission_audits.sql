create table if not exists role_permission_audits (
    audit_id bigint not null auto_increment,
    role_id bigint not null,
    role_name varchar(255) not null,
    previous_permissions text not null,
    updated_permissions text not null,
    changed_by varchar(255) not null,
    changed_at timestamp(6) not null,
    primary key (audit_id)
);
