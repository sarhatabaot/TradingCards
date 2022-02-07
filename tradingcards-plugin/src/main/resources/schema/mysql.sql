-- TradingCards MySql Schema

CREATE TABLE IF NOT EXISTS`{prefix}decks` (
    id             INT AUTO_INCREMENT NOT NULL,
    uuid           VARCHAR(36)        NOT NULL,
    deck_number    INT                NOT NULL,
    card_id        VARCHAR(200)       NOT NULL,
    rarity_id      VARCHAR(200)       NOT NULL,
    amount         INT                NOT NULL,
    is_shiny       BOOL               NOT NULL,
    PRIMARY KEY (id)
) DEFAULT CHARSET = utf8mb4;