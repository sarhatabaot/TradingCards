package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigFile;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;
import java.util.List;

public class ChancesConfig extends SimpleConfigFile {
    private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get("settings/chances",".yml")).build();
    private CommentedConfigurationNode rootNode;
    private List<String> raritiesId;

    private int hostileChance;
    private int neutralChance;
    private int passiveChance;
    private int bossChance;
    private boolean bossDrop;
    private int bossDropRarity;
    private int shinyVersionChance;

    public ChancesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "chances.yml", "settings");
        this.rootNode = loader.load();

        this.hostileChance = rootNode.node("hostile-chance").getInt(20000);
        this.neutralChance = rootNode.node("neutral-chance").getInt(5000);
        this.passiveChance = rootNode.node("passive-chance").getInt(1000);
        this.bossChance = rootNode.node("boss-chance").getInt(100000);
        this.bossDrop = rootNode.node("boss-drop").getBoolean(false);
        this.bossDropRarity = rootNode.node("boss-drop-rarity").getInt(5000);
        this.shinyVersionChance = rootNode.node("shiny-version-chance").getInt(1000);
    }

    public int hostileChance() {
        return hostileChance;
    }

    public int neutralChance() {
        return neutralChance;
    }

    public int passiveChance() {
        return passiveChance;
    }

    public int bossChance() {
        return bossChance;
    }

    public boolean bossDrop() {
        return bossDrop;
    }

    public int bossDropRarity() {
        return bossDropRarity;
    }

    public int shinyVersionChance() {
        return shinyVersionChance;
    }

    public ChanceEntry getChance(final String rarityId) {
        return rootNode.node(rarityId).get(ChanceEntry.class);
    }

    //TODO deserialize using configurate
    public class ChanceEntry {
        private String id;
        private int hostile;
        private int neutral;
        private int passive;
        private int boss;

        public String getId() {
            return id;
        }

        public int getHostile() {
            return hostile;
        }

        public int getNeutral() {
            return neutral;
        }

        public int getPassive() {
            return passive;
        }

        public int getBoss() {
            return boss;
        }
    }
}
