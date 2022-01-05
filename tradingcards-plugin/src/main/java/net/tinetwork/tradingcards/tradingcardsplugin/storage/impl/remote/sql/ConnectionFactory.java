package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql;


import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
    void init(TradingCards plugin);
    void shutdown() throws Exception;
    Connection getConnection() throws SQLException;
    String getType();
}
