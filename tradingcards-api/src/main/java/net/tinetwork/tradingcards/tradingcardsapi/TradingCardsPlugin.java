package net.tinetwork.tradingcards.tradingcardsapi;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TradingCardsPlugin extends JavaPlugin {


    public abstract void debug(String message);

    public abstract boolean isMob(String string);

    public abstract boolean isMob(EntityType type);
}
