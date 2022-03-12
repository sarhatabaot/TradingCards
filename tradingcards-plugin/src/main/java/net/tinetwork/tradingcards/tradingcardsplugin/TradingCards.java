package net.tinetwork.tradingcards.tradingcardsplugin;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.milkbowl.vault.economy.Economy;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.CardsCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.DeckCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.config.CardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.ChancesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.DropTypesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.GeneralConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.MessagesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.PacksConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.RaritiesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.SeriesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.events.DeckEventListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.DeckListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.DropListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.PackListener;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.BoosterPackManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.DropTypeManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.DeckConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.YamlStorage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.SqlStorage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.MariaDbConnectionFactory;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.sql.MySqlConnectionFactory;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.PlayerBlacklist;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.WorldBlacklist;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.Random;


public class TradingCards extends TradingCardsPlugin<TradingCard> {
    private final Random random = new Random();

    /* Mobs */
    private ImmutableSet<EntityType> hostileMobs;
    private ImmutableSet<EntityType> passiveMobs;
    private ImmutableSet<EntityType> neutralMobs;
    private ImmutableSet<EntityType> bossMobs;

    /* Configs */
    private StorageConfig storageConfig;
    private Storage deckStorage;
    private CardsConfig cardsConfig;

    private GeneralConfig generalConfig;
    private RaritiesConfig raritiesConfig;
    private ChancesConfig chancesConfig;
    private PacksConfig packsConfig;
    private MessagesConfig messagesConfig;
    private SeriesConfig seriesConfig;
    private DropTypesConfig dropTypesConfig;

    /* Managers */
    private TradingCardManager cardManager;
    private BoosterPackManager packManager;
    private TradingDeckManager deckManager;
    private DropTypeManager dropTypeManager;

    /* Hooks */
    private boolean hasVault;
    private Economy econ = null;


    /* Blacklists */
    private PlayerBlacklist playerBlacklist;
    private WorldBlacklist worldBlacklist;

    public TradingCards() {
        super();
    }

    protected TradingCards(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public TradingDeckManager getDeckManager() {
        return deckManager;
    }

    @Override
    public TradingCards get() {
        return this;
    }


    @Override
    public void onEnable() {
        Util.init(getLogger());

        cacheMobs();
        initConfigs();

        try {
            this.deckStorage = initStorage();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
        this.deckStorage.init(this);
        initBlacklist();

        initManagers();
        initListeners();
        initUtils();
        initCommands();

        hookVault();
        new Metrics(this, 12940);
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public RaritiesConfig getRaritiesConfig() {
        return raritiesConfig;
    }

    public ChancesConfig getChancesConfig() {
        return chancesConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public PacksConfig getPacksConfig() {
        return packsConfig;
    }

    public SeriesConfig getSeriesConfig() {
        return seriesConfig;
    }

    public DropTypesConfig getDropTypesConfig() {
        return dropTypesConfig;
    }

    public DropTypeManager getDropTypeManager() {
        return dropTypeManager;
    }

    private void initUtils() {
        ChatUtil.init(this);
        CardUtil.init(this);
    }

    private void initBlacklist() {
        try {
            this.playerBlacklist = new PlayerBlacklist(this);
            this.worldBlacklist = new WorldBlacklist(this);
        } catch (ConfigurateException e){
            getLogger().severe(e.getMessage());
        }
    }

    private void initConfigs() {
        try {
            this.generalConfig = new GeneralConfig(this);
            this.raritiesConfig = new RaritiesConfig(this);
            this.seriesConfig = new SeriesConfig(this);
            this.chancesConfig = new ChancesConfig(this);
            this.messagesConfig = new MessagesConfig(this);
            this.packsConfig = new PacksConfig(this);
            this.storageConfig = new StorageConfig(this);
            this.dropTypesConfig = new DropTypesConfig(this);
        } catch (ConfigurateException e) {
            getLogger().severe(e.getMessage());
        }

        this.cardsConfig = new CardsConfig(this);
    }

    private Storage initStorage() throws ConfigurateException {
        StorageType storageType = this.storageConfig.getType();
        getLogger().info("Using storage "+storageType.name());
        switch (storageType){
            case MARIADB -> {
                return new SqlStorage(this,
                        this.storageConfig.getTablePrefix(),
                        new MariaDbConnectionFactory(this.storageConfig));
            }
            case MYSQL -> {
                return new SqlStorage(this,
                        this.storageConfig.getTablePrefix(),
                        new MySqlConnectionFactory(this.storageConfig));
            }
            case YAML -> {
                return new YamlStorage(new DeckConfig(this));
            }
        }
        return new YamlStorage(new DeckConfig(this));
    }

    private void initManagers() {
        this.dropTypeManager = new DropTypeManager(this);
        this.cardManager = new TradingCardManager(this);
        this.packManager = new BoosterPackManager(this);
        this.deckManager = new TradingDeckManager(this);
    }

    private void initCommands() {
        var commandManager = new PaperCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("rarities", c -> cardManager.getRarityNames());
        commandManager.getCommandCompletions().registerCompletion("cards", c -> cardManager.getRarityCardList(c.getContextValueByName(String.class, "rarity")));
        commandManager.getCommandCompletions().registerCompletion("active-cards", c -> cardManager.getActiveRarityCardList(c.getContextValueByName(String.class, "rarity")));
        commandManager.getCommandCompletions().registerCompletion("packs", c -> packManager.packs().keySet());
        commandManager.registerCommand(new CardsCommand(this, playerBlacklist));
        commandManager.registerCommand(new DeckCommand(this));
        commandManager.enableUnstableAPI("help");
        commandManager.enableUnstableAPI("brigadier");
    }

    public void reloadManagers() {
        this.cardManager.initValues();
        this.packManager.initValues();
        this.deckManager = new TradingDeckManager(this);
    }

    @Override
    public void onDisable() {
        econ = null;
        deckManager.closeAllOpenViews();
    }

    @Override
    public TradingCardManager getCardManager() {
        return cardManager;
    }

    @Override
    public PackManager getPackManager() {
        return packManager;
    }

    public boolean isHasVault() {
        return hasVault;
    }

    public Economy getEcon() {
        return econ;
    }

    public Storage getStorage() {
        return deckStorage;
    }

    public CardsConfig getCardsConfig() {
        return cardsConfig;
    }


    private void hookVault() {
        if (this.generalConfig.vaultEnabled()) {
            if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
                this.setupEconomy();
                getLogger().info("Vault hook successful!");
                this.hasVault = true;
            } else {
                getLogger().info("Vault not found, hook unsuccessful!");
            }
        }
    }

    @Override
    public PlayerBlacklist getPlayerBlacklist() {
        return playerBlacklist;
    }

    @Override
    public WorldBlacklist getWorldBlacklist() {
        return worldBlacklist;
    }

    private void initListeners() {
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new DeckEventListener(this), this);
        pm.registerEvents(new DropListener(this), this);
        pm.registerEvents(new PackListener(this), this);
        pm.registerEvents(new DeckListener(this), this);
    }

    private void cacheMobs() {
        this.hostileMobs = ImmutableSet.<EntityType>builder().add(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                EntityType.BLAZE, EntityType.SILVERFISH, EntityType.GHAST, EntityType.SLIME, EntityType.EVOKER, EntityType.VINDICATOR,
                EntityType.VEX, EntityType.SHULKER, EntityType.GUARDIAN, EntityType.MAGMA_CUBE, EntityType.ELDER_GUARDIAN, EntityType.STRAY,
                EntityType.HUSK, EntityType.DROWNED, EntityType.WITCH, EntityType.ZOMBIE_VILLAGER, EntityType.ENDERMITE, EntityType.PILLAGER, EntityType.RAVAGER,
                EntityType.HOGLIN, EntityType.PIGLIN, EntityType.STRIDER, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN, EntityType.WITHER_SKELETON).build();

        this.neutralMobs = ImmutableSet.<EntityType>builder().add(EntityType.ENDERMAN, EntityType.POLAR_BEAR, EntityType.LLAMA, EntityType.WOLF,
                EntityType.DOLPHIN, EntityType.SNOWMAN, EntityType.IRON_GOLEM, EntityType.BEE, EntityType.PANDA, EntityType.FOX).build();

        this.passiveMobs = ImmutableSet.<EntityType>builder().add(EntityType.DONKEY, EntityType.MULE, EntityType.SKELETON_HORSE, EntityType.CHICKEN, EntityType.COW,
                EntityType.SQUID, EntityType.TURTLE, EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SHEEP, EntityType.PIG,
                EntityType.PHANTOM, EntityType.SALMON, EntityType.COD, EntityType.RABBIT, EntityType.VILLAGER, EntityType.BAT,
                EntityType.PARROT, EntityType.HORSE, EntityType.WANDERING_TRADER, EntityType.CAT, EntityType.MUSHROOM_COW, EntityType.TRADER_LLAMA).build();
        this.bossMobs = ImmutableSet.<EntityType>builder().add(EntityType.ENDER_DRAGON, EntityType.WITHER).build();
    }


    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider();
        return econ != null;
    }

    public boolean isMobHostile(EntityType e) {
        return this.hostileMobs.contains(e);
    }

    public boolean isMobNeutral(EntityType e) {
        return this.neutralMobs.contains(e);
    }

    public boolean isMobPassive(EntityType e) {
        return this.passiveMobs.contains(e);
    }

    public boolean isMobBoss(EntityType e) {
        return this.bossMobs.contains(e);
    }

    public String isRarity(String input) {
        try {
            Rarity rarity = getRaritiesConfig().getRarity(input);
            if (getRaritiesConfig().getRarity(input) != null) {
                return rarity.getName().replace("_"," ").toLowerCase();
            }

        } catch (SerializationException e){
            getLogger().severe(e.getMessage());
        }
        return "none;";
    }


    @Override
    public boolean isMob(@NotNull String input) {
        try {
            EntityType type = EntityType.valueOf(input.toUpperCase());
            return isMob(type);
        } catch (IllegalArgumentException var4) {
            return false;
        }
    }

    @Override
    public boolean isMob(EntityType type) {
        return this.hostileMobs.contains(type) || this.neutralMobs.contains(type) || this.passiveMobs.contains(type) || this.bossMobs.contains(type);
    }

    @Override
    public void debug(final Class<?> className, final String message) {
        if (getGeneralConfig().debugMode()) {
            getLogger().info("DEBUG "+className.getSimpleName() + " "+ message);
        }
    }

    public String getPrefixedMessage(final String message) {
        return ChatUtil.color(prefixed(message));
    }

    public String prefixed(final String message) {
        return messagesConfig.prefix() + message;
    }

    private void reloadLists() {
        worldBlacklist.reloadConfig();
        playerBlacklist.reloadConfig();
    }

    private void reloadAllConfigs() {
        this.messagesConfig.reloadConfig();
        this.generalConfig.reloadConfig();
        this.raritiesConfig.reloadConfig();
        this.chancesConfig.reloadConfig();
        this.seriesConfig.reloadConfig();
        this.packsConfig.reloadConfig();

        this.cardsConfig.initValues();
        this.storageConfig.reloadConfig();
    }

    public void reloadPlugin() {
        reloadAllConfigs();
        reloadManagers();
        reloadLists();
    }


    public Random getRandom() {
        return random;
    }




}