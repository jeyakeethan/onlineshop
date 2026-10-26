set @notifications_exists = (select count(*) from information_schema.tables
    where table_schema = database() and table_name = 'notifications');
set @read_column_exists = (select count(*) from information_schema.columns
    where table_schema = database() and table_name = 'notifications' and column_name = 'read');
set @sql = if(@notifications_exists = 1 and @read_column_exists = 1,
    'alter table notifications change column `read` is_read bit not null',
    'select 1');
prepare migration_statement from @sql;
execute migration_statement;
deallocate prepare migration_statement;
