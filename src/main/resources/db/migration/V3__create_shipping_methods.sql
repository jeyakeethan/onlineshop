CREATE TABLE shipping_methods (
    method_id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(32) NOT NULL,
    label VARCHAR(100) NOT NULL,
    fee DECIMAL(12, 2) NOT NULL,
    free_shipping_threshold DECIMAL(12, 2) NULL,
    estimate VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (method_id),
    CONSTRAINT uk_shipping_methods_code UNIQUE (code)
);