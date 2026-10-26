set @notifications_exists = (select count(*) from information_schema.tables
    where table_schema = database() and table_name = 'notifications');
set @sql = if(@notifications_exists = 1,
    'alter table notifications add column deduplication_key varchar(255)',
    'select 1');
prepare migration_statement from @sql;
execute migration_statement;
deallocate prepare migration_statement;
set @sql = if(@notifications_exists = 1,
    'create unique index uk_notifications_deduplication_key on notifications (deduplication_key)',
    'select 1');
prepare migration_statement from @sql;
execute migration_statement;
deallocate prepare migration_statement;

set @email_deliveries_exists = (select count(*) from information_schema.tables
    where table_schema = database() and table_name = 'email_deliveries');
set @sql = if(@email_deliveries_exists = 1,
    'alter table email_deliveries add column deduplication_key varchar(255)',
    'select 1');
prepare migration_statement from @sql;
execute migration_statement;
deallocate prepare migration_statement;
set @sql = if(@email_deliveries_exists = 1,
    'alter table email_deliveries add column subject varchar(255)',
    'select 1');
prepare migration_statement from @sql;
execute migration_statement;
deallocate prepare migration_statement;
set @sql = if(@email_deliveries_exists = 1,
    'alter table email_deliveries add column body text',
    'select 1');
prepare migration_statement from @sql;
execute migration_statement;
deallocate prepare migration_statement;
set @sql = if(@email_deliveries_exists = 1,
    'create unique index uk_email_deliveries_deduplication_key on email_deliveries (deduplication_key)',
    'select 1');
prepare migration_statement from @sql;
execute migration_statement;
deallocate prepare migration_statement;
