CREATE TABLE shipping_zones (
    zone_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    priority INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (zone_id),
    INDEX idx_shipping_zones_priority (priority, zone_id)
);

CREATE TABLE shipping_zone_regions (
    zone_id BIGINT NOT NULL,
    display_order INT NOT NULL,
    region_code VARCHAR(16) NOT NULL,
    PRIMARY KEY (zone_id, display_order),
    CONSTRAINT fk_shipping_zone_regions_zone
        FOREIGN KEY (zone_id) REFERENCES shipping_zones (zone_id) ON DELETE CASCADE
);

CREATE TABLE shipping_zone_postal_prefixes (
    zone_id BIGINT NOT NULL,
    display_order INT NOT NULL,
    postal_prefix VARCHAR(16) NOT NULL,
    PRIMARY KEY (zone_id, display_order),
    CONSTRAINT fk_shipping_zone_postal_prefixes_zone
        FOREIGN KEY (zone_id) REFERENCES shipping_zones (zone_id) ON DELETE CASCADE
);