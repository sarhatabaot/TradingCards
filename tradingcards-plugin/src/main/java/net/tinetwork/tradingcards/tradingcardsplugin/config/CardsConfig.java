package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardsConfig {
    private final File cardsFolder;
    private final TradingCards plugin;

    private List<SimpleCardsConfig> cardConfigs;
    public CardsConfig(final TradingCards plugin) {
        this.plugin = plugin;

        createCardsFolder(plugin);
        if(plugin.getGeneralConfig().useDefaultCardsFile())
            createDefaultCardConfig(plugin);

        this.cardsFolder = new File(plugin.getDataFolder().getPath() + File.separator + "cards");

        initValues();
    }

    public void initValues() {
        this.cardConfigs = new ArrayList<>();
        if(cardsFolder.listFiles() == null) {
            plugin.getLogger().warning("There are no files in the cards folder.");
            return;
        }

        for (File file : cardsFolder.listFiles()) {
            plugin.debug(CardsConfig.class,"File name: " + file.getName());
            if (file.getName().endsWith(".yml")) {
                try {
                    cardConfigs.add(new SimpleCardsConfig(plugin, file.getName()));
                    plugin.debug(CardsConfig.class,"Added: " + file.getName());
                } catch (ConfigurateException e) {
                    plugin.getLogger().severe(e.getMessage());
                }
            }
        }
    }

    private void createCardsFolder(final @NotNull TradingCards plugin) {
        final File cardsFolder = new File(plugin.getDataFolder() + File.separator + "cards");
        if (!cardsFolder.exists())
            cardsFolder.mkdir();
    }

    private void createDefaultCardConfig(final TradingCards plugin) {
        try {
            new SimpleCardsConfig(plugin, "cards.yml").saveDefaultConfig();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
    }

    public List<SimpleCardsConfig> getCardConfigs() {
        return cardConfigs;
    }
}
