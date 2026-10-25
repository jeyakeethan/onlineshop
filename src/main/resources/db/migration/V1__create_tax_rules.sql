CREATE TABLE tax_rules (
    rule_id BIGINT NOT NULL AUTO_INCREMENT,
    region VARCHAR(16) NOT NULL,
    rate DECIMAL(7, 4) NOT NULL,
    tax_inclusive BOOLEAN NOT NULL DEFAULT FALSE,
    effective_from DATETIME(6) NOT NULL,
    effective_to DATETIME(6) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (rule_id),
    INDEX idx_tax_rules_region_effective (region, effective_from, effective_to)
);