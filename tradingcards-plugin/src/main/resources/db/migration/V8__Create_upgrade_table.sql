-- --------------------------
-- Table `${prefix}upgrades`
-- --------------------------
CREATE TABLE IF NOT EXISTS `{prefix}upgrades` (
    `upgrade_id`       VARCHAR(200)       NOT NULL,
) DEFAULT CHARSET = utf8mb4;

-- --------------------------
-- Table `${prefix}upgrades_required`
-- --------------------------
CREATE TABLE IF NOT EXISTS `{prefix}upgrades_required` (
    `upgrade_id`       VARCHAR(200)       NOT NULL,
    `series_id`        VARCHAR(200)       NOT NULL,
    `amount`           INT                NOT NULL,
    `rarity_id`        VARCHAR(200)       NOT NULL,
    PRIMARY KEY (`upgrade_id`)
) DEFAULT CHARSET = utf8mb4;

-- --------------------------
-- Table `${prefix}upgrades_result`
-- --------------------------
CREATE TABLE IF NOT EXISTS `{prefix}upgrades_result` (
    `upgrade_id`       VARCHAR(200)       NOT NULL,
    `series_id`        VARCHAR(200)       NOT NULL,
    `amount`           INT                NOT NULL,
    `rarity_id`        VARCHAR(200)       NOT NULL,
    PRIMARY KEY (`upgrade_id`)
) DEFAULT CHARSET = utf8mb4;