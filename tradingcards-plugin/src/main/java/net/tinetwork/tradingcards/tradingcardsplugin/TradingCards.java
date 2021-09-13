package net.tinetwork.tradingcards.tradingcardsplugin;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import net.milkbowl.vault.economy.Economy;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.CardsCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.DeckCommand;
import net.tinetwork.tradingcards.tradingcardsplugin.config.CardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.DeckConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.*;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.DeckListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.DropListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.MobSpawnListener;
import net.tinetwork.tradingcards.tradingcardsplugin.listeners.PackListener;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.BoosterPackManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.PlayerBlacklist;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.WorldBlacklist;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Random;


public class TradingCards extends TradingCardsPlugin<TradingCard> {
    private final Random random = new Random();

    /* Mobs */
    private ImmutableList<EntityType> hostileMobs;
    private ImmutableList<EntityType> passiveMobs;
    private ImmutableList<EntityType> neutralMobs;
    private ImmutableList<EntityType> bossMobs;

    /* Configs */
    private DeckConfig deckConfig;
    private CardsConfig cardsConfig;

    private GeneralConfig generalConfig;
    private RaritiesConfig raritiesConfig;
    private ChancesConfig chancesConfig;
    private PacksConfig packsConfig;
    private MessagesConfig messagesConfig;
    private SeriesConfig seriesConfig;

    /* Managers */
    private TradingCardManager cardManager;
    private BoosterPackManager packManager;
    private TradingDeckManager deckManager;

    /* Hooks */
    private boolean hasVault;
    private Economy econ = null;


    /* Blacklists */
    private PlayerBlacklist playerBlacklist;
    private WorldBlacklist worldBlacklist;


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
        cacheMobs();
        initConfigs();
        initBlacklist();
        initManagers();
        initListeners();
        initUtils();
        initCommands();

        hookVault();
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
            this.chancesConfig = new ChancesConfig(this);
            this.messagesConfig = new MessagesConfig(this);
            this.packsConfig = new PacksConfig(this);
            this.deckConfig = new DeckConfig(this);
            this.seriesConfig = new SeriesConfig(this);
        } catch (ConfigurateException e) {
            getLogger().severe(e.getMessage());
        }

        cardsConfig = new CardsConfig(this);
    }

    private void initManagers() {
        this.cardManager = new TradingCardManager(this);
        this.packManager = new BoosterPackManager(this);
        this.deckManager = new TradingDeckManager(this);
    }

    private void initCommands() {
        var commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CardsCommand(this, playerBlacklist));
        commandManager.registerCommand(new DeckCommand(this));
        commandManager.getCommandCompletions().registerCompletion("rarities", c -> cardManager.getRarityNames());
        commandManager.getCommandCompletions().registerCompletion("active-rarities", c -> cardManager.getActiveRarityNames());
        commandManager.getCommandCompletions().registerCompletion("cards", c -> cardManager.getRarityCardList(c.getContextValueByName(String.class, "rarity")));
        commandManager.getCommandCompletions().registerCompletion("active-cards", c -> cardManager.getActiveRarityCardList(c.getContextValueByName(String.class, "rarity")));
        commandManager.getCommandCompletions().registerCompletion("packs", c -> packManager.packs().keySet());
        commandManager.enableUnstableAPI("help");
        commandManager.enableUnstableAPI("brigadier");
    }

    public void disableManagers() {
        this.cardManager = null;
        this.packManager = null;
        this.deckManager = null;
    }

    public void reloadManagers() {
        disableManagers();
        this.cardManager = new TradingCardManager(this);
        this.packManager = new BoosterPackManager(this);
        this.deckManager = new TradingDeckManager(this);
    }


    @Override
    public void onDisable() {
        econ = null;
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

    public DeckConfig getDeckConfig() {
        return deckConfig;
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
        pm.registerEvents(new DropListener(this, cardManager), this);
        pm.registerEvents(new PackListener(this), this);
        pm.registerEvents(new MobSpawnListener(this), this);
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
    public boolean isMob(String input) {
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
    public void debug(final String message) {
        if (getGeneralConfig().debugMode()) {
            getLogger().info("DEBUG " + message);
        }
    }

    public String getPrefixedMessage(final String message) {
        return ChatUtil.color(prefixed(message));
    }

    public String prefixed(final String message) {
        return messagesConfig.prefix() + message;
    }

    public void reloadAllConfig() {
        this.deckConfig.reloadConfig();
        this.packsConfig.reloadConfig();
        this.generalConfig.reloadConfig();
        this.messagesConfig.reloadConfig();
        this.raritiesConfig.reloadConfig();
        this.chancesConfig.reloadConfig();
    }


    private String getPrefixedTimerMessage(int hours) {
        return getPrefixedMessage(messagesConfig.timerMessage().replace("%hour%", String.valueOf(hours)));
    }

    public Random getRandom() {
        return random;
    }

}