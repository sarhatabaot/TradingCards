package net.tinetwork.tradingcards.tradingcardsplugin.storage;

import org.jooq.SQLDialect;

/**
 * @author sarhatabaot
 */
public enum StorageType {
    YAML(null),
    MYSQL(SQLDialect.MYSQL),
    MARIADB(SQLDialect.MARIADB);
    private final SQLDialect dialect;

    StorageType(final SQLDialect dialect) {
        this.dialect = dialect;
    }

    public SQLDialect getDialect() {
        return dialect;
    }
}
