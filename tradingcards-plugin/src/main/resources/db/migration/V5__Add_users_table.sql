-- --------------------------
-- Table `${prefix}users`
-- --------------------------
CREATE TABLE IF NOT EXISTS `{prefix}users` (
    `id`           INT         NOT NULL AUTO_INCREMENT,
    `uuid`         VARCHAR(36) NOT NULL,
    `username`     VARCHAR(16) NOT NULL
    PRIMARY KEY (id)
) DEFAULT CHARSET = utf8mb4;