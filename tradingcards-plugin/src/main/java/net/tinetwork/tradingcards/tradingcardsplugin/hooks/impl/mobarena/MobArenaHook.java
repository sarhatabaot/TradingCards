package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mobarena;


import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.PluginHook;

public class MobArenaHook extends PluginHook {
    public MobArenaHook(TradingCards tradingCards) {
        super(tradingCards);
    }


    @Override
    public YamlConfigurateFile<TradingCards> getConfig() {
        return null;
    }

    @Override
    public String getPluginName() {
        return "MobArena";
    }

    @Override
    public void onRegister() {

    }
}
