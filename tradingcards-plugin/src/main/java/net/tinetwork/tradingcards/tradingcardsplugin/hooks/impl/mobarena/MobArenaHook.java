package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mobarena;


import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.PluginHook;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.ConfigurateException;

public class MobArenaHook extends PluginHook {
    private MobArenaConfig mobArenaConfig;

    public MobArenaHook(TradingCards tradingCards) {
        super(tradingCards);

        try {
            this.mobArenaConfig = new MobArenaConfig(tradingCards);
        } catch (ConfigurateException e) {
            tradingCards.getLogger().severe(e.getMessage());
        }
    }


    @Override
    public YamlConfigurateFile<TradingCards> getConfig() {
        return mobArenaConfig;
    }

    @Override
    public String getPluginName() {
        return "MobArena";
    }

    @Override
    public void onRegister() {
        if (!mobArenaConfig.enabled()) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(new MobArenaListener(tradingCards, mobArenaConfig), tradingCards);
    }
}
