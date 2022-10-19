-- -------------------------------
-- Alter Table `${prefix}cards`
-- -------------------------------
ALTER TABLE `${prefix}cards`
    ALTER `has_shiny`
    SET DEFAULT 0;

ALTER TABLE `${prefix}cards`
    ALTER `buy_price`
    SET DEFAULT 0.0;

ALTER TABLE `${prefix}cards`
    ALTER `sell_price`
    SET DEFAULT 0.0;

ALTER TABLE `${prefix}cards`
    ALTER `custom_model_data`
    SET DEFAULT 100000;