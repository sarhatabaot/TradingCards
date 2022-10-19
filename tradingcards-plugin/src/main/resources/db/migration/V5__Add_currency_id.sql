-- -------------------------------
-- Alter Table `${prefix}cards`
-- -------------------------------
ALTER TABLE `${prefix}cards`
    ADD `currency_id` VARCHAR(30)
    DEFAULT 'tc-internal-default';


-- -------------------------------
-- Alter Table `${prefix}packs`
-- -------------------------------
ALTER TABLE `${prefix}packs`
    ADD `currency_id` VARCHAR(30)
    DEFAULT 'tc-internal-default';
