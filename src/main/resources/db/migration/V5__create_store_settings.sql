create table if not exists store_settings (
    settings_id bigint not null,
    store_name varchar(255) not null,
    logo_url varchar(255),
    contact_email varchar(255),
    contact_phone varchar(255),
    currency varchar(3) not null,
    shipping_policy text,
    return_policy text,
    privacy_policy text,
    terms_policy text,
    primary key (settings_id)
);
