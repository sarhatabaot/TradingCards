CREATE TABLE IF NOT EXISTS `{prefix}packs_trade` (
    `id`            INT                 NOT NULL AUTO_INCREMENT,
    line_number      INT                NOT NULL,
    pack_id          VARCHAR(200)       NOT NULL,
    rarity_id        VARCHAR(200)       NOT NULL,
    card_amount      VARCHAR(200)       NOT NULL,
    series_id        VARCHAR(200)       NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;