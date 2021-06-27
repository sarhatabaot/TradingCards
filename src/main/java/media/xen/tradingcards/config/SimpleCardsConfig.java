package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import media.xen.tradingcards.core.SimpleConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

/**
 * @author sarhatabaot
 */
public class SimpleCardsConfig extends SimpleConfig {
    private final ConfigurationSection cards;
    public SimpleCardsConfig(final TradingCards plugin, final String fileName) {
        super(plugin, fileName, "cards");
        this.cards = getConfig().getConfigurationSection("Cards");
        reloadConfig();
        plugin.debug("Created: "+fileName);
    }
    public ConfigurationSection getCardSection(final String rarity, final String name) {
        return cards.getConfigurationSection(rarity+"."+name);
    }
    public String getSeries(final String rarity, final String name) {
        return getCardSection(rarity,name).getString("Series", "");
    }

    public String getAbout(final String rarity, final String name) {
        return getCardSection(rarity,name).getString("About", "");
    }

    public String getType(final String rarity,final String name) {
        return getCardSection(rarity,name).getString("Type", "");
    }

    public String getInfo(final String rarity, final String name) {
        return getCardSection(rarity, name).getString("Info", "");
    }

    public String getCost(final String rarity, final String name) {
        if(getCardSection(rarity, name).contains("Buy-Price"))
            return String.valueOf(getCardSection(rarity, name).getDouble("Buy-Price"));
        return "None";
    }

    public double getBuyPrice(final String rarity, final String name) {
        return getCardSection(rarity,name).getDouble("Buy-Price",0.0D);
    }


    public double getSellPrice(final String rarity, final String name) {
        return getCardSection(rarity,name).getDouble("Sell-Price",0.0D);
    }

    public boolean hasShiny(final String rarity, final String name) {
        return getCardSection(rarity, name).getBoolean("Has-Shiny-Version", false);
    }

    public ConfigurationSection getCards() {
        return cards;
    }

    @Override
    public void saveDefaultConfig() {
        if (this.file == null) {
            this.file = new File(folder, fileName);
        }

        if (!this.file.exists()) {
            plugin.saveResource(fileName, false);
        }

        reloadConfig();
    }
}
