alter table return_requests add column refund_status varchar(32);
alter table return_requests add column refund_provider varchar(32);
alter table return_requests add column refund_idempotency_key varchar(160);
alter table return_requests add column provider_refund_id varchar(160);
alter table return_requests add column refund_executed_at datetime(6);
update return_requests set refund_status = 'NOT_EXECUTED' where refund_status is null;
