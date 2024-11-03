package net.tinetwork.tradingcards.tradingcardsplugin.hooks;


import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.Bukkit;

public abstract class PluginHook {
    protected final TradingCards tradingCards;

    protected PluginHook(TradingCards tradingCards) {
        this.tradingCards = tradingCards;
    }

    public abstract YamlConfigurateFile<TradingCards> getConfig();

    public abstract String getPluginName();

    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }

    public void register() {
        if (!canRegister()) {
            return;
        }

        onRegister();
    }

    protected abstract void onRegister();

}
