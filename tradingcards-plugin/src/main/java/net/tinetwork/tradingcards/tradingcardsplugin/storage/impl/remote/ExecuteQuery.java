package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public abstract class ExecuteQuery<T> {
    private final SqlStorage sqlStorage;

    public ExecuteQuery(final SqlStorage sqlStorage) {
        this.sqlStorage = sqlStorage;
    }

    public T runQuery(final String sql, Map<String, String> values, Map<String, String> where, Map<String, String> set) {
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStorage.getStatementProcessor().apply(sql, values, where, set))) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return getQuery(resultSet);
                }
            }
        } catch (SQLException e) {
            Util.logSevereException(e);
        }

        return returnNull();
    }

    public T runQuery(final String sql, Map<String, String> values, Map<String, String> where) {
        return runQuery(sql, values, where, null);
    }

    public T prepareAndRunQuery() {
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL);
            return onRunQuery(dslContext);
        } catch (SQLException e) {
            Util.logSevereException(e);
        }
        return returnNull();
    }

    public abstract T onRunQuery(DSLContext dslContext) throws SQLException;

    public abstract T getQuery(ResultSet resultSet) throws SQLException;

    public abstract T getQuery(@NotNull final List<Record> result) throws SQLException;

    public abstract T returnNull();
}
