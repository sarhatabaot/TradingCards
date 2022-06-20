package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author sarhatabaot
 */
public abstract class ExecuteUpdate {
    private final SqlStorage sqlStorage;
    private final Settings settings;

    public ExecuteUpdate(final SqlStorage sqlStorage, final Settings settings) {
        this.sqlStorage = sqlStorage;
        this.settings = settings;
    }
    //todo future
    public void executeUpdate() {
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            DSLContext dslContext = DSL.using(connection, sqlStorage.getType().getDialect(), settings);
            onRunUpdate(dslContext);
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
    }

    protected abstract void onRunUpdate(DSLContext dslContext);
}
