package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigFile;
import org.bukkit.Material;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;
import java.util.List;

public class GeneralConfig extends SimpleConfigFile {
    private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get("settings/general",".yml")).build();
    private CommentedConfigurationNode rootNode;

    private boolean debugMode;

    private Material cardMaterial;
    private String cardPrefix;
    private String shinyName;

    private boolean deckInCreative;
    private boolean useDeckItem;
    private boolean useLargeDecks;
    private Material deckMaterial;
    private String deckPrefix;
    private boolean dropDeckItems;

    private boolean scheduleCards;
    private boolean scheduleCardsNatural;
    private String scheduleCardMob;
    private String scheduleCardRarity;
    private int scheduleCardTimeInHours;

    private String playerOpRarity;
    private String playerSeries;
    private String playerType;
    private boolean playerHasShinyVersion;

    private boolean allowRewards;
    private boolean rewardBroadcast;
    private boolean eatShinyCards;

    private boolean vaultEnabled;
    private boolean closedEconomy;
    private String serverAccount;

    private boolean spawnerBlock;
    private String spawnerMobName;
    private int infoLineLength;

    private List<String> activeSeries;
    public GeneralConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "general.yml", "settings");
        this.rootNode = loader.load();

        this.debugMode = rootNode.node("debug-mode").getBoolean(false);

        //Cards
        this.cardMaterial= rootNode.node("card-material").get(Material.class, Material.PAPER);
        this.cardPrefix = rootNode.node("card-prefix").getString("Card ");
        this.shinyName = rootNode.node("shiny-name").getString("Shiny");

        //Decks
        this.deckInCreative = rootNode.node("decks-in-creative").getBoolean(false);
        this.useDeckItem = rootNode.node("use-deck-item").getBoolean(true);
        //We should change this to deck size, or num of rows in deck
        this.useLargeDecks = rootNode.node("use-large-decks").getBoolean(false);
        this.deckMaterial = rootNode.node("deck-material").get(Material.class, Material.BOOK);
        this.deckPrefix = rootNode.node("deck-prefix").getString("&7[&fDeck&7]&f ");
        this.dropDeckItems = rootNode.node("drop-deck-items").getBoolean(true);

        //Schedule
        this.scheduleCards = rootNode.node("schedule-cards").getBoolean(false);
        this.scheduleCardsNatural = rootNode.node("schedule-cards-natural").getBoolean(false);
        this.scheduleCardMob = rootNode.node("schedule-card-mob").getString("ZOMBIE");
        this.scheduleCardRarity = rootNode.node("schedule-card-rarity").getString("Common");
        this.scheduleCardTimeInHours = rootNode.node("schedule-card-time-in-hours").getInt(1);

        //Player Drops
        this.playerOpRarity = rootNode.node("player-op-rarity").getString("Legendary");
        this.playerSeries = rootNode.node("player-series").getString("Legacy");
        this.playerType = rootNode.node("player-type").getString("Player");
        this.playerHasShinyVersion = rootNode.node("player-has-shiny-version").getBoolean(true);

        //Rewards
        this.allowRewards = rootNode.node("allow-rewards").getBoolean(true);
        this.rewardBroadcast = rootNode.node("reward-broadcast").getBoolean(true);
        this.eatShinyCards = rootNode.node("eat-shiny-cards").getBoolean(false);

        //Vault
        this.vaultEnabled = rootNode.node("vault-enabled").getBoolean(true);
        this.closedEconomy = rootNode.node("closed-economy").getBoolean(false);
        this.serverAccount = rootNode.node("server-account").getString("TradingCards-Bank");

        //Misc
        this.spawnerBlock = rootNode.node("spawner-block").getBoolean(true);
        this.spawnerMobName = rootNode.node("spawner-mob-name").getString("Spawned Mob");
        this.infoLineLength = rootNode.node("info-line-length").getInt(25);

        //Series
        this.activeSeries = rootNode.node("active-series").getList(String.class, List.of("2021"));
    }

    public Material deckMaterial() {
        return deckMaterial;
    }

    public String deckPrefix() {
        return deckPrefix;
    }

    public boolean dropDeckItems() {
        return dropDeckItems;
    }

    public boolean scheduleCards() {
        return scheduleCards;
    }

    public boolean scheduleCardsNatural() {
        return scheduleCardsNatural;
    }

    public String scheduleCardMob() {
        return scheduleCardMob;
    }

    public String scheduleCardRarity() {
        return scheduleCardRarity;
    }

    public int scheduleCardTimeInHours() {
        return scheduleCardTimeInHours;
    }

    public String playerOpRarity() {
        return playerOpRarity;
    }

    public String playerSeries() {
        return playerSeries;
    }

    public String playerType() {
        return playerType;
    }

    public boolean playerHasShinyVersion() {
        return playerHasShinyVersion;
    }

    public boolean allowRewards() {
        return allowRewards;
    }

    public boolean rewardBroadcast() {
        return rewardBroadcast;
    }

    public boolean eatShinyCards() {
        return eatShinyCards;
    }

    public boolean vaultEnabled() {
        return vaultEnabled;
    }

    public boolean closedEconomy() {
        return closedEconomy;
    }

    public String serverAccount() {
        return serverAccount;
    }

    public boolean spawnerBlock() {
        return spawnerBlock;
    }

    public String spawnerMobName() {
        return spawnerMobName;
    }

    public int infoLineLength() {
        return infoLineLength;
    }

    public List<String> activeSeries() {
        return activeSeries;
    }

    public boolean deckInCreative() {
        return deckInCreative;
    }

    public boolean useDeckItem() {
        return useDeckItem;
    }

    public boolean useLargeDecks() {
        return useLargeDecks;
    }

    public boolean debugMode() {
        return debugMode;
    }

    public String cardPrefix() {
        return cardPrefix;
    }

    public String shinyName() {
        return shinyName;
    }

    public Material cardMaterial() {
        return cardMaterial;
    }
    @Override
    public void reloadConfig()  {
        try {
            this.rootNode = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
}