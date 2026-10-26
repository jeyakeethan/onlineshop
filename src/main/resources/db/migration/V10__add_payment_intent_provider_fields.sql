alter table orders add column payment_provider varchar(32);
alter table orders add column payment_currency varchar(3);
update orders set payment_provider = 'mock' where payment_provider is null;
update orders set payment_currency = 'USD' where payment_currency is null;
