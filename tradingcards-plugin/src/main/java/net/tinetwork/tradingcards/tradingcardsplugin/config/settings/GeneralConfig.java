package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.List;

public class GeneralConfig extends SimpleConfigurate {
    private final boolean debugMode;

    //Cards
    private final Material cardMaterial;
    private final String cardPrefix;
    private final String shinyName;

    //Decks
    private final boolean deckInCreative;
    private final boolean useDeckItem;
    private final boolean useLargeDecks;
    private final Material deckMaterial;
    private final String deckPrefix;
    private final boolean dropDeckItems;

    //Packs
    private final Material packMaterial;
    private final String packPrefix;

    //Schedule
    private final boolean scheduleCards;
    private final boolean scheduleCardsNatural;
    private final String scheduleCardMob;
    private final String scheduleCardRarity;
    private final int scheduleCardTimeInHours;

    //Player
    private final String playerOpRarity;
    private final String playerSeries;
    private final String playerType;
    private final boolean playerHasShinyVersion;
    private final boolean playerDropsCard;
    private final int playerDropsCardRarity;

    //Rewards
    private final boolean allowRewards;
    private final boolean rewardBroadcast;
    private final boolean eatShinyCards;

    //Vault
    private final boolean vaultEnabled;
    private final boolean closedEconomy;
    private final String serverAccount;

    private final boolean spawnerBlock;
    private final String spawnerMobName;
    private final int infoLineLength;

    //Colors
    private final String colorSeries;
    private final String colorType;
    private final String colorInfo;
    private final String colorAbout;
    private final String colorRarity;
    private final String colorPackName;
    private final String colorPackLore;
    private final String colorPackNormal;
    private final String colorPackSpecial;
    private final String colorPackExtra;
    private final String colorListHaveCard;
    private final String colorListHaveCardShiny;
    private final String colorRarityCompleted;

    private final String displayTitle;
    private final String displayShinyTitle;
    private final String displaySeries;
    private final String displayType;
    private final String displayInfo;
    private final String displayAbout;

    private ItemStack blankCard;
    private ItemStack blankBoosterPack;
    private ItemStack blankDeck;

    private final List<String> activeSeries;
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
        this.packMaterial = rootNode.node("booster-pack-material").get(Material.class, Material.BOOK);
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

    @Override
    protected void registerTypeSerializer() {
        //No custom type serializer to register
    }

    public ItemStack blankCard() {
        if(blankCard == null)
            this.blankCard = new ItemStack(plugin.getGeneralConfig().cardMaterial());
        return blankCard;
    }

    public ItemStack blankBoosterPack() {
        if(blankBoosterPack == null)
            this.blankBoosterPack = new ItemStack(new ItemStack(plugin.getGeneralConfig().packMaterial()));
        return blankBoosterPack;
    }

    public ItemStack blankDeck() {
        if(blankDeck == null)
            this.blankDeck = new ItemStack(plugin.getGeneralConfig().deckMaterial());
        return blankDeck;
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

    public String colorType() {
        return colorType;
    }

    public String colorInfo() {
        return colorInfo;
    }

    public String colorAbout() {
        return colorAbout;
    }

    public String colorRarity() {
        return colorRarity;
    }

    public String colorPackName() {
        return colorPackName;
    }

    public String colorPackLore() {
        return colorPackLore;
    }

    public String colorPackNormal() {
        return colorPackNormal;
    }

    public String colorPackSpecial() {
        return colorPackSpecial;
    }

    public String colorPackExtra() {
        return colorPackExtra;
    }

    public String colorListHaveCard() {
        return colorListHaveCard;
    }

    public String colorListHaveCardShiny() {
        return colorListHaveCardShiny;
    }

    public String colorRarityCompleted() {
        return colorRarityCompleted;
    }

    public String packPrefix() {
        return packPrefix;
    }

    public String colorSeries() {
        return colorSeries;
    }

    public String displayTitle() {
        return displayTitle;
    }

    public String displayShinyTitle() {
        return displayShinyTitle;
    }

    public String displaySeries() {
        return displaySeries;
    }

    public String displayType() {
        return displayType;
    }

    public String displayInfo() {
        return displayInfo;
    }

    public String displayAbout() {
        return displayAbout;
    }
}
