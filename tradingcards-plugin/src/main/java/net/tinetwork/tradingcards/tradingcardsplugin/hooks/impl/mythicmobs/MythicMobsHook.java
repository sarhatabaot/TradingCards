package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs;

import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.PluginHook;


public class MythicMobsHook extends PluginHook {
    public MythicMobsHook(TradingCards tradingCards) {
        super(tradingCards);
    }

    @Override
    public YamlConfigurateFile<TradingCards> getConfig() {
        return null;
    }

    @Override
    public String getPluginName() {
        return "MythicMobs";
    }

    @Override
    protected void onRegister() {

    }
}
