package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author sarhatabaot
 */
public abstract class ExecuteQuery<T,R> {
    private final SqlStorage sqlStorage;
    private final Settings settings;

    public ExecuteQuery(final SqlStorage sqlStorage, final Settings settings) {
        this.sqlStorage = sqlStorage;
        this.settings = settings;
    }

    public T prepareAndRunQuery() {
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            DSLContext dslContext = DSL.using(connection, sqlStorage.getType().getDialect(), settings);
            return onRunQuery(dslContext);
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
        return empty();
    }

    public abstract T onRunQuery(DSLContext dslContext) throws SQLException;

    public abstract T getQuery(@NotNull final R result) throws SQLException;

    public abstract T empty();

}
