package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.towny;


import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.PluginHook;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.ConfigurateException;

public class TownyHook extends PluginHook {
    private TownyConfig townyConfig;

    public TownyHook(TradingCards tradingCards){
        super(tradingCards);
        try {
            this.townyConfig = new TownyConfig(tradingCards);
        } catch (ConfigurateException e) {
            tradingCards.getLogger().severe(e.getMessage());
        }


    }

    @Override
    public YamlConfigurateFile<TradingCards> getConfig() {
        return townyConfig;
    }

    @Override
    public String getPluginName() {
        return "Towny";
    }

    @Override
    public void onRegister() {
        if (!townyConfig.enabled())
            return;

        if (townyConfig.createDefaults()) {
            createDefaultSeries();
            createDefaultType();
        }

        Bukkit.getPluginManager().registerEvents(new TownyListener(tradingCards, townyConfig), tradingCards);
    }

    private void createDefaultSeries() {
        final Storage<TradingCard> storage = tradingCards.getStorage();
        storage.createSeries("towny");
        storage.editSeriesDisplayName("towny", "Towny");
        storage.editSeriesMode("towny", Mode.ACTIVE);
        storage.editSeriesColors("towny", new ColorSeries("&a","&b","&e", "&c", "&b"));
    }

    private void createDefaultType() {
        final Storage<TradingCard> storage = tradingCards.getStorage();
        storage.createCustomType("town","passive");
        storage.editCustomTypeDisplayName("town", "Town");

        storage.createCustomType("nation","passive");
        storage.editCustomTypeDisplayName("nation", "Nation");
    }


}
