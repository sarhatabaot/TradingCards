-- TradingCards MySql Schema

CREATE TABLE IF NOT EXISTS `{prefix}decks` (
    id             INT AUTO_INCREMENT NOT NULL,
    uuid           VARCHAR(36)        NOT NULL,
    deck_number    INT                NOT NULL,
    card_id        VARCHAR(200)       NOT NULL,
    rarity_id      VARCHAR(200)       NOT NULL,
    amount         INT                NOT NULL,
    is_shiny       BOOL               NOT NULL,
    PRIMARY KEY (id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}rarities` (
    rarity_id      VARCHAR(200)       NOT NULL, --Foreign key for rewards
    display_name   TINYTEXT           NOT NULL,
    default_color  VARCHAR(36)        NOT NULL,
    buy_price      DOUBLE             NOT NULL,
    sell_price     DOUBLE             NOT NULL,
    PRIMARY KEY (rarity_id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}rewards` (
    rarity_id     VARCHAR(200)       NOT NULL, --This should just be the rarity_id, we don't even need a dedicated rewards_id. (We need one in the table but not in the rarities table)
    command       TINYTEXT           NOT NULL,
    order_number  INT                NOT NULL,
    PRIMARY KEY (rarity_id)
    FOREIGN KEY (rarity_id) REFERENCES `{prefix}rarities`(rarity_id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}series` (
    series_id    VARCHAR(200)                           NOT NULL, --Foreign key for series colors (We should define as a foreign key)
    display_name TINYTEXT                               NOT NULL,
    series_mode  ENUM('ACTIVE','DISABLED','SCHEDULED')  NOT NULL,
    PRIMARY KEY (series_id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}series_colors` (
   series_id    VARCHAR(200)       NOT NULL,
   color        VARCHAR(10)        NOT NULL,
   `type`       VARCHAR(10)        NOT NULL,
   info         VARCHAR(10)        NOT NULL,
   about        VARCHAR(10)        NOT NULL,
   rarity       VARCHAR(10)        NOT NULL,
   PRIMARY KEY (series_id)
   FOREIGN KEY (series_id) REFERENCES `{prefix}series`(series_id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}custom_types` (
    type_id         VARCHAR(200)       NOT NULL,
    display_name    TINYTEXT           NOT NULL,
    drop_type       ENUM('boss','hostile','neutral','passive','all')
    PRIMARY KEY (type_id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}packs` (
    pack_id         VARCHAR(200)       NOT NULL, -- Foreign key for packs_content
    display_name    TINYTEXT           NOT NULL,
    price           DOUBLE             NOT NULL,
    permission      VARCHAR(200)       NOT NULL,
    PRIMARY KEY (pack_id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}packs_content` (
    order_number     INT                NOT NULL,
    pack_id          VARCHAR(200)       NOT NULL,
    rarity_id        VARCHAR(200)       NOT NULL, -- foreign key from rarities
    card_amount      VARCHAR(200)       NOT NULL,
    series_id        VARCHAR(200)       NOT NULL, -- foreign key from series
    PRIMARY KEY (order_number),
    FOREIGN KEY (rarity_id) REFERENCES `{prefix}rarities`(rarity_id),
    FOREIGN KEY (series_id) REFERENCES `{prefix}series`(series_id)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `{prefix}cards` (
    card_id             VARCHAR(200)        NOT NULL,
    display_name        TINYTEXT,
    rarity_id           VARCHAR(200)        NOT NULL, -- foreign key from rarities
    has_shiny           BOOL                NOT NULL,
    series_id           VARCHAR(200)        NOT NULL, -- foreign key from series
    info                TEXT,
    custom_model_data   INT,
    buy_price           DOUBLE,
    sell_price          DOUBLE,
    PRIMARY KEY (card_id),
    FOREIGN KEY (rarity_id) REFERENCES `{prefix}rarities`(rarity_id),
    FOREIGN KEY (series_id) REFERENCES `{prefix}series`(series_id)
) DEFAULT CHARSET = utf8mb4;