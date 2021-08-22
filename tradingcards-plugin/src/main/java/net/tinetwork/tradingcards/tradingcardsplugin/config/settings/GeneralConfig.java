package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.bukkit.Material;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.List;

public class GeneralConfig extends SimpleConfigurate {
    private boolean debugMode;

    //Cards
    private Material cardMaterial;
    private String cardPrefix;
    private String shinyName;

    //Decks
    private boolean deckInCreative;
    private boolean useDeckItem;
    private boolean useLargeDecks;
    private Material deckMaterial;
    private String deckPrefix;
    private boolean dropDeckItems;

    //Packs
    private Material packMaterial;
    private String packPrefix;

    //Schedule
    private boolean scheduleCards;
    private boolean scheduleCardsNatural;
    private String scheduleCardMob;
    private String scheduleCardRarity;
    private int scheduleCardTimeInHours;

    //Player
    private String playerOpRarity;
    private String playerSeries;
    private String playerType;
    private boolean playerHasShinyVersion;
    private boolean playerDropsCard;
    private int playerDropsCardRarity;

    //Rewards
    private boolean allowRewards;
    private boolean rewardBroadcast;
    private boolean eatShinyCards;

    //Vault
    private boolean vaultEnabled;
    private boolean closedEconomy;
    private String serverAccount;

    private boolean spawnerBlock;
    private String spawnerMobName;
    private int infoLineLength;

    //Colors
    private String colorSeries;
    private String colorType;
    private String colorInfo;
    private String colorAbout;
    private String colorRarity;
    private String colorPackName;
    private String colorPackLore;
    private String colorPackNormal;
    private String colorPackSpecial;
    private String colorPackExtra;
    private String colorListHaveCard;
    private String colorListHaveCardShiny;
    private String colorRarityCompleted;

    public String displayTitle;
    public String displayShinyTitle;
    public String displaySeries;
    public String displayType;
    public String displayInfo;
    public String displayAbout;

    private List<String> activeSeries;
    public GeneralConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator,"general.yml", "settings");

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

        //Packs
        this.packMaterial = rootNode.node("boosterpack-material").get(Material.class, Material.BOOK);
        this.packPrefix = rootNode.node("booster-pack-prefix").getString("&7[&fPack&7]&f ");

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
        this.playerDropsCard = rootNode.node("player-drops-card").getBoolean(true);
        this.playerDropsCardRarity = rootNode.node("player-drops-card-rarity").getInt(1000000);
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

        final ConfigurationNode colorNode = rootNode.node("colors");
        //Colors
        this.colorSeries = colorNode.node("series").getString();
        this.colorType = colorNode.node("type").getString();
        this.colorInfo = colorNode.node("info").getString();
        this.colorAbout = colorNode.node("about").getString();
        this.colorRarity = colorNode.node("rarity").getString();
        this.colorPackName = colorNode.node("booster-pack-name").getString();
        this.colorPackLore = colorNode.node("booster-pack-lore").getString();
        this.colorPackNormal = colorNode.node("booster-pack-normal-cards").getString();
        this.colorPackSpecial = colorNode.node("booster-pack-special-cards").getString();
        this.colorPackExtra = colorNode.node("booster-pack-extra-cards").getString();
        this.colorListHaveCard = colorNode.node("list-have-card").getString();
        this.colorListHaveCardShiny = colorNode.node("list-have-shiny-card").getString();
        this.colorRarityCompleted = colorNode.node("list-rarity-complete").getString();

        //Display
        final ConfigurationNode displayNode = rootNode.node("display");
        this.displayTitle = displayNode.node("title").getString();
        this.displayShinyTitle = displayNode.node("shiny-title").getString();
        this.displaySeries = displayNode.node("series").getString();
        this.displayType = displayNode.node("type").getString();
        this.displayInfo = displayNode.node("info").getString();
        this.displayAbout = displayNode.node("about").getString();

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

    public boolean playerDropsCard() {
        return playerDropsCard;
    }

    public int playerDropsCardRarity() {
        return playerDropsCardRarity;
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

    public Material packMaterial() {
        return packMaterial;
    }

    public String getColorSeries() {
        return colorSeries;
    }

    public String getColorType() {
        return colorType;
    }

    public String getColorInfo() {
        return colorInfo;
    }

    public String getColorAbout() {
        return colorAbout;
    }

    public String getColorRarity() {
        return colorRarity;
    }

    public String getColorPackName() {
        return colorPackName;
    }

    public String getColorPackLore() {
        return colorPackLore;
    }

    public String getColorPackNormal() {
        return colorPackNormal;
    }

    public String getColorPackSpecial() {
        return colorPackSpecial;
    }

    public String getColorPackExtra() {
        return colorPackExtra;
    }

    public String getColorListHaveCard() {
        return colorListHaveCard;
    }

    public String getColorListHaveCardShiny() {
        return colorListHaveCardShiny;
    }

    public String getColorRarityCompleted() {
        return colorRarityCompleted;
    }

    public String packPrefix() {
        return packPrefix;
    }
}
