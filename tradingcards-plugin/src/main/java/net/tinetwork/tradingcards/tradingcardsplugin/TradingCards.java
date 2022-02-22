package net.tinetwork.tradingcards.tradingcardsplugin;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import net.milkbowl.vault.economy.Economy;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.manager.RarityManager;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.CardsCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.CreateCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.DeckCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.EditCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditPack;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditRarity;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditSeries;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditType;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.ChancesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.GeneralConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.MessagesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.StorageConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.events.DeckEventListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.DeckListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.DropListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.PackListener;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.BoosterPackManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.DropTypeManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingRarityManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingSeriesManager;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class TradingCards extends TradingCardsPlugin<TradingCard> {
    private final Random random = new Random();

    /* Mobs */
    private ImmutableList<EntityType> hostileMobs;
    private ImmutableList<EntityType> passiveMobs;
    private ImmutableList<EntityType> neutralMobs;
    private ImmutableList<EntityType> bossMobs;

    /* Configs */
    private Storage<TradingCard> storage;

    /* Local Settings */
    private StorageConfig storageConfig;
    private GeneralConfig generalConfig;
    private MessagesConfig messagesConfig;
    private ChancesConfig chancesConfig;

    /* Managers */
    private TradingCardManager cardManager;
    private BoosterPackManager packManager;
    private TradingDeckManager deckManager;
    private DropTypeManager dropTypeManager;
    private TradingRarityManager rarityManager;
    private TradingSeriesManager seriesManager;

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
            this.storage = initStorage();
        } catch (ConfigurateException e) {
            Util.logSevereException(e);
        }
        this.storage.init(this);
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

    public ChancesConfig getChancesConfig() {
        return chancesConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public DropTypeManager getDropTypeManager() {
        return dropTypeManager;
    }

    @Override
    public RarityManager getRarityManager() {
        return this.rarityManager;
    }

    private void initUtils() {
        ChatUtil.init(this);
        CardUtil.init(this);
    }

    private void initBlacklist() {
        try {
            this.playerBlacklist = new PlayerBlacklist(this);
            this.worldBlacklist = new WorldBlacklist(this);
        } catch (ConfigurateException e) {
            getLogger().severe(e.getMessage());
        }
    }

    private void initConfigs() {
        try {
            this.generalConfig = new GeneralConfig(this);
            this.chancesConfig = new ChancesConfig(this);
            this.messagesConfig = new MessagesConfig(this);
            this.storageConfig = new StorageConfig(this);
        } catch (ConfigurateException e) {
            getLogger().severe(e.getMessage());
        }
    }

    @Contract(" -> new")
    private @NotNull Storage<TradingCard> initStorage() throws ConfigurateException {
        StorageType storageType = this.storageConfig.getType();
        getLogger().info("Using storage " + storageType.name());
        switch (storageType) {
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
            //YAML is the default
            default -> {
                return new YamlStorage(this);
            }

        }
    }

    //The order is important. Decks & Packs must load after cards load.
    //Cards must load after rarity, droptype & series.
    private void initManagers() {
        this.rarityManager = new TradingRarityManager(this);
        this.dropTypeManager = new DropTypeManager(this);
        this.seriesManager = new TradingSeriesManager(this);

        this.cardManager = new TradingCardManager(this);
        this.packManager = new BoosterPackManager(this);
        this.deckManager = new TradingDeckManager(this);
    }

    private void initCommands() {
        var commandManager = new PaperCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("rarities", c -> rarityManager.getRarities().stream().map(Rarity::getName).toList());
        commandManager.getCommandCompletions().registerCompletion("cards", c -> cardManager.getRarityCardListNames(c.getContextValueByName(String.class, "rarity")));
        commandManager.getCommandCompletions().registerCompletion("active-cards", c -> cardManager.getActiveRarityCardList(c.getContextValueByName(String.class, "rarity")));
        commandManager.getCommandCompletions().registerCompletion("packs", c -> packManager.getPacks().stream().map(Pack::id).toList());
        commandManager.getCommandCompletions().registerCompletion("default-types", c -> dropTypeManager.getDefaultTypes());
        commandManager.getCommandCompletions().registerCompletion("custom-types", c -> dropTypeManager.getTypes().keySet());
        commandManager.getCommandCompletions().registerCompletion("all-types", c -> Stream.concat(dropTypeManager.getDefaultTypes().stream(), dropTypeManager.getTypes().keySet().stream()).toList());
        commandManager.getCommandCompletions().registerCompletion("series", c -> seriesManager.getAllSeries().stream().map(Series::getName).toList());
        commandManager.getCommandCompletions().registerCompletion("series-colors", c -> List.of("info=", "about=", "type=", "series=", "rarity="));
        commandManager.getCommandCompletions().registerCompletion("edit-type", c -> Stream.of(EditType.values()).map(Enum::name).toList());
        commandManager.getCommandCompletions().registerCompletion("edit-pack", c -> Stream.of(EditPack.values()).map(Enum::name).toList());
        commandManager.getCommandCompletions().registerCompletion("edit-series", c -> Stream.of(EditSeries.values()).map(Enum::name).toList());
        commandManager.getCommandCompletions().registerCompletion("edit-rarity", c -> Stream.of(EditRarity.values()).map(Enum::name).toList());
        commandManager.getCommandCompletions().registerCompletion("edit-card", c -> Stream.of(EditCard.values()).map(Enum::name).toList());
        commandManager.getCommandCompletions().registerCompletion(
                "edit-type-value", c -> switch (c.getContextValueByName(EditType.class, "editType")) {
                    case TYPE -> dropTypeManager.getDefaultTypes();
                    case DISPLAY_NAME -> Collections.singleton("");
                });
        commandManager.getCommandCompletions().registerCompletion(
                "edit-pack-value", c -> switch (c.getContextValueByName(EditPack.class, "editPack")) {
                    case PRICE, PERMISSION, DISPLAY_NAME -> Collections.singleton("");
                    case CONTENTS -> IntStream.rangeClosed(0, packManager.getPack(c.getContextValueByName(String.class, "packId")).getPackEntryList().size() - 1)
                            .boxed().map(String::valueOf).toList();

                }
        );
        commandManager.getCommandCompletions().registerCompletion(
                "edit-series-value", c -> switch (c.getContextValueByName(EditSeries.class,"editSeries")) {
                    case MODE -> Arrays.stream(Mode.values()).map(Enum::name).toList();
                    case DISPLAY_NAME -> Collections.singleton("");
                    case COLORS -> List.of("info=", "about=", "type=", "series=", "rarity=");
                }
        );
        commandManager.getCommandCompletions().registerCompletion(
                "edit-rarity-value", c -> switch (c.getContextValueByName(EditRarity.class,"editRarity")) {
                    case BUY_PRICE, SELL_PRICE, DEFAULT_COLOR, DISPLAY_NAME, REMOVE_ALL_REWARDS -> Collections.singleton("");
                    case ADD_REWARD, REMOVE_REWARD -> IntStream.rangeClosed(0, Objects.requireNonNullElse(rarityManager.getRarity(c.getContextValueByName(String.class, "rarityId")),TradingRarityManager.EMPTY_RARITY).getRewards().size() - 1)
                            .boxed().map(String::valueOf).toList();
                }
        );
        commandManager.getCommandCompletions().registerCompletion(
                "edit-card-value", c -> switch (c.getContextValueByName(EditCard.class,"editCard")) {
                    case DISPLAY_NAME, SELL_PRICE, BUY_PRICE, INFO, CUSTOM_MODEL_DATA -> Collections.singleton("");
                    case SERIES -> seriesManager.getAllSeries().stream().map(Series::getName).toList();
                    case TYPE -> Stream.concat(dropTypeManager.getDefaultTypes().stream(), dropTypeManager.getTypes().keySet().stream()).toList();
                }
        );

        commandManager.registerCommand(new CardsCommand(this, playerBlacklist));
        commandManager.registerCommand(new EditCommand(this));
        commandManager.registerCommand(new CreateCommand(this));
        commandManager.registerCommand(new DeckCommand(this));
        commandManager.enableUnstableAPI("help");
        commandManager.enableUnstableAPI("brigadier");
    }

    public void reloadManagers() {
        //todo reload all the other managers as well
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

    public Storage<TradingCard> getStorage() {
        return storage;
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
        this.hostileMobs = ImmutableList.<EntityType>builder().add(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                EntityType.BLAZE, EntityType.SILVERFISH, EntityType.GHAST, EntityType.SLIME, EntityType.EVOKER, EntityType.VINDICATOR,
                EntityType.VEX, EntityType.SHULKER, EntityType.GUARDIAN, EntityType.MAGMA_CUBE, EntityType.ELDER_GUARDIAN, EntityType.STRAY,
                EntityType.HUSK, EntityType.DROWNED, EntityType.WITCH, EntityType.ZOMBIE_VILLAGER, EntityType.ENDERMITE, EntityType.PILLAGER, EntityType.RAVAGER,
                EntityType.HOGLIN, EntityType.PIGLIN, EntityType.STRIDER, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN).build();

        this.neutralMobs = ImmutableList.<EntityType>builder().add(EntityType.ENDERMAN, EntityType.POLAR_BEAR, EntityType.LLAMA, EntityType.WOLF, EntityType.DOLPHIN,
                EntityType.DOLPHIN, EntityType.SNOWMAN, EntityType.IRON_GOLEM, EntityType.BEE, EntityType.PANDA, EntityType.FOX).build();

        this.passiveMobs = ImmutableList.<EntityType>builder().add(EntityType.DONKEY, EntityType.MULE, EntityType.SKELETON_HORSE, EntityType.CHICKEN, EntityType.COW,
                EntityType.SQUID, EntityType.TURTLE, EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SHEEP, EntityType.PIG,
                EntityType.PHANTOM, EntityType.SALMON, EntityType.COD, EntityType.RABBIT, EntityType.VILLAGER, EntityType.BAT,
                EntityType.PARROT, EntityType.HORSE, EntityType.WANDERING_TRADER, EntityType.CAT, EntityType.MUSHROOM_COW, EntityType.TRADER_LLAMA).build();
        this.bossMobs = ImmutableList.<EntityType>builder().add(EntityType.ENDER_DRAGON, EntityType.WITHER).build();
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
            getLogger().info("DEBUG " + className.getSimpleName() + " " + message);
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
        this.storageConfig.reloadConfig();

        this.storage.reload();
        this.chancesConfig.reloadConfig();
    }

    public void reloadPlugin() {
        reloadAllConfigs();
        reloadManagers();
        reloadLists();
    }

    public Random getRandom() {
        return random;
    }

    public TradingSeriesManager getSeriesManager() {
        return seriesManager;
    }
}