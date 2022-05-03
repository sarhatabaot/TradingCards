-- -------------------------------
-- Alter Table `${prefix}cards`
-- -------------------------------
ALTER TABLE `${prefix}cards`
    ADD CONSTRAINT DF_CARDS_SHINY
    DEFAULT 0
    FOR is_shiny;

ALTER TABLE `${prefix}cards`
    ADD CONSTRAINT DF_CARDS_BUY
    DEFAULT 0.0
    FOR buy_price;

ALTER TABLE `${prefix}cards`
    ADD CONSTRAINT DF_CARDS_SELL
    DEFAULT 0.0
    FOR sell_price;

ALTER TABLE `${prefix}cards`
    ADD CONTRAINT DF_CARDS_INFO
    DEFAULT ""
    FOR info;

ALTER TABLE `${prefix}cards`
    ADD CONTRAINT DF_CARDS_CUSTOM_MODEL
    DEFAULT 100000
    FOR custom_model_data;