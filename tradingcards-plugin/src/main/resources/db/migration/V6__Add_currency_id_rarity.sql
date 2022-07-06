-- -------------------------------
-- Alter Table `${prefix}rarities`
-- -------------------------------
ALTER TABLE `${prefix}rarities`
    ADD currency_id VARCHAR(30)
    DEFAULT NULL;