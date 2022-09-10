package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.github.sarhatabaot.kraken.core.config.Transformation;
import net.tinetwork.tradingcards.api.config.settings.GeneralConfigurate;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.transformations.GeneralTransformations;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.General;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

public class GeneralConfig extends GeneralConfigurate {
    private boolean debugMode;

    private boolean useDefaultCardsFile;

    //Cards
    private Material cardMaterial;
    private String cardPrefix;
    private String shinyName;

    //Decks
    private boolean deckInCreative;
    private boolean useDeckItem;
    private Material deckMaterial;
    private String deckPrefix;

    private int deckCustomModelData;
    private boolean dropDeckItems;
    private int deckRows;

    //Packs
    private Material packMaterial;
    private String packPrefix;


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

    private boolean treasuryEnabled;

    private boolean spawnerBlock;
    private String spawnerMobName;
    private int infoLineLength;

    private String colorPackName;
    private String colorPackLore;
    private String colorPackNormal;
    private String colorListHaveCard;
    private String colorListHaveCardShiny;
    private String colorRarityCompleted;

    private String displayTitle;
    private String displayShinyTitle;
    private String displaySeries;
    private String displayType;
    private String displayInfo;
    private String displayAbout;

    private ItemStack blankCard;
    private ItemStack blankBoosterPack;
    private ItemStack blankDeck;

    public GeneralConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator,"general.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.debugMode = rootNode.node("debug-mode").getBoolean(General.DEBUG_MODE);

        this.useDefaultCardsFile = rootNode.node("use-default-cards-file").getBoolean(General.USE_DEFAULT_CARDS_FILE);

        //Cards
        this.cardMaterial= rootNode.node("card-material").get(Material.class, Material.PAPER);
        this.cardPrefix = rootNode.node("card-prefix").getString(General.CARD_PREFIX);
        this.shinyName = rootNode.node("shiny-name").getString(General.SHINY_NAME);

        //Decks
        this.deckInCreative = rootNode.node("decks-in-creative").getBoolean(General.DECKS_IN_CREATIVE);
        this.useDeckItem = rootNode.node("use-deck-item").getBoolean(General.USE_DECK_ITEM);
        this.deckCustomModelData = rootNode.node("deck-custom-model-data").getInt(-1);

        this.deckRows = rootNode.node("deck-rows").getInt(General.DECK_ROWS);

        this.deckMaterial = rootNode.node("deck-material").get(Material.class, Material.BOOK);
        this.deckPrefix = rootNode.node("deck-prefix").getString(General.DECK_PREFIX);
        this.dropDeckItems = rootNode.node("drop-deck-items").getBoolean(General.DROP_DECK_ITEMS);

        //Packs
        this.packMaterial = rootNode.node("booster-pack-material").get(Material.class, Material.BOOK);
        this.packPrefix = rootNode.node("booster-pack-prefix").getString(General.BOOSTER_PACK_PREFIX);

        //Player Drops
        this.playerOpRarity = rootNode.node("player-op-rarity").getString(General.PLAYER_OP_RARITY);
        this.playerSeries = rootNode.node("player-series").getString(General.PLAYER_SERIES);
        this.playerType = rootNode.node("player-type").getString(General.PLAYER_TYPE);
        this.playerHasShinyVersion = rootNode.node("player-has-shiny-version").getBoolean(General.PLAYER_HAS_SHINY_VERSION);
        this.playerDropsCard = rootNode.node("player-drops-card").getBoolean(General.PLAYER_DROPS_CARD);
        this.playerDropsCardRarity = rootNode.node("player-drops-card-rarity").getInt(General.PLAYER_DROPS_CARD_RARITY);
        //Rewards
        this.allowRewards = rootNode.node("allow-rewards").getBoolean(General.ALLOW_REWARDS);
        this.rewardBroadcast = rootNode.node("reward-broadcast").getBoolean(General.REWARD_BROADCAST);
        this.eatShinyCards = rootNode.node("eat-shiny-cards").getBoolean(General.EAT_SHINY_CARDS);

        //Vault
        this.vaultEnabled = rootNode.node("vault-enabled").getBoolean(General.PluginSupport.Vault.VAULT_ENABLED);
        this.closedEconomy = rootNode.node("closed-economy").getBoolean(General.PluginSupport.Vault.CLOSED_ECONOMY);
        this.serverAccount = rootNode.node("server-account").getString(General.PluginSupport.Vault.SERVER_ACCOUNT);

        this.treasuryEnabled = rootNode.node("treasury-enabled").getBoolean(false);
        //Misc
        //todo, we should add a tag to the animals instead of renaming them
        this.spawnerBlock = rootNode.node("spawner-block").getBoolean(General.SPAWNER_BLOCK);

        this.infoLineLength = rootNode.node("info-line-length").getInt(General.INFO_LINE_LENGTH);

        final ConfigurationNode colorNode = rootNode.node("colors");
        //Colors
        //colors-packs
        final ConfigurationNode colorPacksNode = colorNode.node("packs");
        this.colorPackName = colorPacksNode.node("booster-pack-name").getString(General.Colors.Packs.BOOSTER_PACK_NAME);
        this.colorPackLore = colorPacksNode.node("booster-pack-lore").getString(General.Colors.Packs.BOOSTER_PACK_LORE);
        this.colorPackNormal = colorPacksNode.node("booster-pack-normal-cards").getString(General.Colors.Packs.BOOSTER_PACK_NORMAL_CARDS);
        //colors-lists
        final ConfigurationNode colorListsNode = colorNode.node("lists");
        this.colorListHaveCard = colorListsNode.node("list-have-card").getString(General.Colors.Lists.LIST_HAVE_CARD);
        this.colorListHaveCardShiny = colorListsNode.node("list-have-shiny-card").getString(General.Colors.Lists.LIST_HAVE_SHINY_CARD);
        this.colorRarityCompleted = colorListsNode.node("list-rarity-complete").getString(General.Colors.Lists.LIST_RARITY_COMPLETE);

        //Display
        final ConfigurationNode displayNode = rootNode.node("display");
        this.displayTitle = displayNode.node("title").getString(General.Display.TITLE);
        this.displayShinyTitle = displayNode.node("shiny-title").getString(General.Display.SHINY_TITLE);
        this.displaySeries = displayNode.node("series").getString(General.Display.SERIES);
        this.displayType = displayNode.node("type").getString(General.Display.TYPE);
        this.displayInfo = displayNode.node("info").getString(General.Display.INFO);
        this.displayAbout = displayNode.node("about").getString(General.Display.ABOUT);
    }


    @Override
    protected void builderOptions() {
        //nothing to add
    }

    @Override
    protected Transformation getTransformation() {
        return new GeneralTransformations();
    }

    public ItemStack blankCard() {
        if(blankCard == null)
            this.blankCard = new ItemStack(cardMaterial());
        return blankCard;
    }

    public ItemStack blankBoosterPack() {
        if(blankBoosterPack == null)
            this.blankBoosterPack = new ItemStack(new ItemStack(packMaterial()));
        return blankBoosterPack;
    }

    public ItemStack blankDeck() {
        if(blankDeck == null)
            this.blankDeck = new ItemStack(deckMaterial());
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

    public int deckCustomModelData() {
        return deckCustomModelData;
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

    public boolean deckInCreative() {
        return deckInCreative;
    }

    public boolean useDeckItem() {
        return useDeckItem;
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

    public String colorPackName() {
        return colorPackName;
    }

    public String colorPackLore() {
        return colorPackLore;
    }

    public String colorPackNormal() {
        return colorPackNormal;
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

    public boolean useDefaultCardsFile(){
        return useDefaultCardsFile;
    }

    public int deckRows() {
        return deckRows;
    }

    public boolean treasuryEnabled() {
        return treasuryEnabled;
    }
}
