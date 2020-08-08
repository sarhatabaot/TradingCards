package media.xen.tradingcards;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;

public class TradingCards
        extends JavaPlugin
        implements Listener,
        CommandExecutor {
    private static TradingCards INSTANCE;
    private Map<String, Database> databases = new HashMap<>();
    List < EntityType > hostileMobs = new ArrayList();
    List < EntityType > passiveMobs = new ArrayList();
    List < EntityType > neutralMobs = new ArrayList();
    List < EntityType > bossMobs = new ArrayList();
    public static Permission permRarities = new Permission("cards.rarity");
    boolean hasVault;
    boolean hasMobArena;
    boolean hasMythicMobs;
    private FileConfiguration deckData = null;
    private File deckDataFile = null;
    private FileConfiguration messagesData = null;
    public ArenaMaster am;
    private File messagesDataFile = null;
    private FileConfiguration cardsData = null;
    private File cardsDataFile = null;
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    Random r = new Random();
    int taskid;
    boolean usingSqlite;

    public void onEnable() {
        getDataFolder().mkdirs();
        INSTANCE = this;
        List < EntityType > safeHostileMobs = Arrays.asList(
                EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.BLAZE, EntityType.SILVERFISH, EntityType.GHAST, EntityType.SLIME, EntityType.EVOKER, EntityType.VINDICATOR, EntityType.VEX, EntityType.SHULKER, EntityType.GUARDIAN, EntityType.MAGMA_CUBE, EntityType.ELDER_GUARDIAN, EntityType.STRAY, EntityType.HUSK, EntityType.DROWNED, EntityType.WITCH, EntityType.ZOGLIN, EntityType.HOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.ENDERMITE);
        List < EntityType > safeNeutralMobs = Arrays.asList(
                EntityType.ENDERMAN, EntityType.POLAR_BEAR, EntityType.LLAMA, EntityType.WOLF, EntityType.BEE, EntityType.DOLPHIN, EntityType.PANDA, EntityType.PIGLIN, EntityType.ZOMBIFIED_PIGLIN, EntityType.DOLPHIN, EntityType.SNOWMAN, EntityType.IRON_GOLEM);
        List < EntityType > safePassiveMobs = Arrays.asList(
                EntityType.DONKEY, EntityType.MULE, EntityType.SKELETON_HORSE, EntityType.CHICKEN, EntityType.COW, EntityType.SQUID, EntityType.WANDERING_TRADER, EntityType.TURTLE, EntityType.STRIDER, EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SHEEP, EntityType.PIG, EntityType.PHANTOM, EntityType.SALMON, EntityType.COD, EntityType.RABBIT, EntityType.VILLAGER, EntityType.BAT, EntityType.PARROT, EntityType.HORSE);
        List < EntityType > safeBossMobs = Arrays.asList(
                EntityType.ENDER_DRAGON, EntityType.WITHER);
        this.hostileMobs.addAll(safeHostileMobs);
        this.neutralMobs.addAll(safeNeutralMobs);
        this.passiveMobs.addAll(safePassiveMobs);
        this.bossMobs.addAll(safeBossMobs);
        getConfig().options().copyDefaults(true);
        getServer().getPluginManager().addPermission(permRarities);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("cards").setExecutor(this);
        reloadCustomConfig();
        saveDefaultDeckFile();
        reloadDeckData();
        saveDefaultMessagesFile();
        reloadMessagesData();
        saveDefaultCardsFile();
        reloadCardsData();
        if (getConfig().getBoolean("General.SQLite")) {
            usingSqlite = true;
            initializeDatabase("trading_cards", "CREATE TABLE IF NOT EXISTS cards(`id` INTEGER NOT NULL PRIMARY KEY, `rarity` varchar(255), `about` varchar(255), `series` varchar(255), `name` varchar(255), `type` varchar(255), `info` varchar(255), `price` int); CREATE TABLE IF NOT EXISTS decks(`id` INTEGER NOT NULL PRIMARY KEY, `uuid` varchar(512), `deckID` int, `card` int, `isShiny` int, `count` int)");
            System.out.println("[TradingCards] SQLite is enabled!");
            sqliteTransfer();
        }
        else {
            usingSqlite = false;
            System.out.println("[TradingCards] Legacy YML mode is enabled!");
        }
        if (getConfig().getBoolean("PluginSupport.Towny.Towny-Enabled")) {
            if (getServer().getPluginManager().getPlugin("Towny") != null) {
                getServer().getPluginManager().registerEvents(new TownyListener(this), this);
                System.out.println("[TradingCards] Towny successfully hooked!");
            } else {
                System.out.println("[TradingCards] Towny not found, hook unsuccessful!");
            }
        }
        if (getConfig().getBoolean("PluginSupport.Vault.Vault-Enabled")) {
            if (getServer().getPluginManager().getPlugin("Vault") != null) {
                setupEconomy();
                System.out.println("[TradingCards] Vault hook successful!");
                this.hasVault = true;
            } else {
                System.out.println("[TradingCards] Vault not found, hook unsuccessful!");
            }
        }
        if (getConfig().getBoolean("PluginSupport.MobArena.MobArena-Enabled")) {
            if (getServer().getPluginManager().getPlugin("MobArena") != null) {
                PluginManager pm = getServer().getPluginManager();
                MobArena maPlugin = (MobArena) pm.getPlugin("MobArena");
                this.am = maPlugin.getArenaMaster();
                pm.registerEvents(new MobArenaListener(this), this);
                System.out.println("[TradingCards] Mob Arena hook successful!");
                this.hasMobArena = true;
            } else {
                System.out.println("[TradingCards] Mob Arena not found, hook unsuccessful!");
            }
        }
        if (getConfig().getBoolean("PluginSupport.MythicMobs.MythicMobs-Enabled")) {
            if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
                PluginManager pm = getServer().getPluginManager();
                //MythicMobs mmPlugin = (MythicMobs) pm.getPlugin("MythicMobs");
                pm.registerEvents(new MythicMobsListener(this), this);
                System.out.println("[TradingCards] MythicMobs hook successful!");
                this.hasMythicMobs = true;
            } else {
                System.out.println("[TradingCards] MythicMobs not found, hook unsuccessful!");
            }
        }
        if (getConfig().getBoolean("General.Schedule-Cards")) {
            startTimer();
        }
    }

    public void onDisable() {
        this.deckData = null;
        this.deckDataFile = null;
        this.messagesData = null;
        this.messagesDataFile = null;
        this.cardsData = null;
        this.cardsDataFile = null;
        econ = null;
        perms = null;
        chat = null;
        getServer().getPluginManager().removePermission(permRarities);
    }

    public static TradingCards getInstance() {
        return INSTANCE;
    }

    public void initializeDatabase(String databaseName, String createStatement) {
        Database db = new SQLite(databaseName, createStatement, this.getDataFolder());
        db.load();
        databases.put(databaseName, db);
    }

    public Map<String, Database> getDatabases() {
        return databases;
    }

    public Database getDatabase(String databaseName) {
        return getDatabases().get(databaseName);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider < Economy > rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public String formatDouble(double value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(value);
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

    public ItemStack getBlankCard(int quantity) {
        ItemStack tradingCard = new ItemStack(Material.getMaterial(getConfig().getString("General.Card-Material")), quantity);
        return tradingCard;
    }

    public ItemStack getBlankBoosterPack() {
        ItemStack boosterPack = new ItemStack(Material.getMaterial(getConfig().getString("General.BoosterPack-Material")));
        return boosterPack;
    }

    public ItemStack getBlankDeck() {
        ItemStack deck = new ItemStack(Material.getMaterial(getConfig().getString("General.Deck-Material")));
        return deck;
    }

    public ItemStack createDeck(Player p, int num) {
        ItemStack deck = getBlankDeck();
        ItemMeta deckMeta = deck.getItemMeta();
        deckMeta.setDisplayName(cMsg(getConfig().getString("General.Deck-Prefix") + p.getName() + "'s Deck #" + num));
        if (getConfig().getBoolean("General.Hide-Enchants", true)) deckMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        deck.setItemMeta(deckMeta);
        deck.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        return deck;
    }

    public void reloadDeckData() {
        if (this.deckDataFile == null) this.deckDataFile = new File(getDataFolder(), "decks.yml");
        this.deckData = YamlConfiguration.loadConfiguration(this.deckDataFile);
        Reader defConfigStream = null;
        defConfigStream = new InputStreamReader(getResource("decks.yml"), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.deckData.setDefaults(defConfig);
        }
    }

    public FileConfiguration getDeckData() {
        if (this.deckData == null) reloadDeckData();
        return this.deckData;
    }

    public void saveDeckData() {
        if (this.deckData == null || this.deckDataFile == null) return;
        try {
            getDeckData().save(this.deckDataFile);
        } catch(IOException iOException) {}
    }

    public void saveDefaultDeckFile() {
        if (this.deckDataFile == null) this.deckDataFile = new File(getDataFolder(), "decks.yml");
        if (!this.deckDataFile.exists()) saveResource("decks.yml", false);
    }

    public void reloadMessagesData() {
        if (this.messagesDataFile == null) this.messagesDataFile = new File(getDataFolder(), "messages.yml");
        this.messagesData = YamlConfiguration.loadConfiguration(this.messagesDataFile);
        Reader defConfigStream = null;
        defConfigStream = new InputStreamReader(getResource("messages.yml"), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.messagesData.setDefaults(defConfig);
        }
    }

    public FileConfiguration getMessagesData() {
        if (this.messagesData == null) reloadMessagesData();
        return this.messagesData;
    }

    public void saveMessagesData() {
        if (this.messagesData == null || this.messagesDataFile == null) return;
        try {
            getMessagesData().save(this.messagesDataFile);
        } catch(IOException iOException) {}
    }

    public void saveDefaultMessagesFile() {
        if (this.messagesDataFile == null) this.messagesDataFile = new File(getDataFolder(), "messages.yml");
        if (!this.messagesDataFile.exists()) saveResource("messages.yml", false);
    }

    public void reloadCardsData() {
        if (this.cardsDataFile == null) this.cardsDataFile = new File(getDataFolder(), "cards.yml");
        this.cardsData = YamlConfiguration.loadConfiguration(this.cardsDataFile);
        Reader defConfigStream = null;
        defConfigStream = new InputStreamReader(getResource("cards.yml"), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.cardsData.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCardsData() {
        if (this.cardsData == null) reloadCardsData();
        return this.cardsData;
    }

    public void saveCardsData() {
        if (this.cardsData == null || this.cardsDataFile == null) return;
        try {
            getCardsData().save(this.cardsDataFile);
        } catch(IOException iOException) {}
    }

    public void saveDefaultCardsFile() {
        if (this.cardsDataFile == null) this.cardsDataFile = new File(getDataFolder(), "cards.yml");
        if (!this.cardsDataFile.exists()) saveResource("cards.yml", false);
    }

    public boolean hasDeck(Player p, int num) {
        for (ItemStack i: p.getInventory()) {
            if (i != null && i.getType() == Material.valueOf(getConfig().getString("General.Deck-Material")) && i.containsEnchantment(Enchantment.DURABILITY) && i.getEnchantmentLevel(Enchantment.DURABILITY) == 10) {

                String name = i.getItemMeta().getDisplayName();
                String[] splitName = name.split("#");
                if (num == Integer.valueOf(splitName[1]).intValue()) return true;
            }
        }
        return false;
    }

    public boolean deleteCardSqlite(Player p, String card, String rarity) {
        int cardID = getCardID(card,rarity);
        String playerID = p.getUniqueId().toString();
        if(hasCard(p,card,rarity) == 1) {
            getDatabase("trading_cards").executeStatement("DELETE FROM decks WHERE uuid = '"+playerID+"' AND card="+cardID+" LIMIT 1");
            return true;
        } else if(hasCard(p,card,rarity) > 1) {
            int cardCount = hasCard(p,card,rarity) - 1;
            getDatabase("trading_cards").executeStatement("UPDATE decks SET count = "+cardCount+" WHERE uuid = '"+playerID+"' AND card="+cardID+" LIMIT 1");
            return true;
        }
        else return false;
    }

    public boolean deleteCard(Player p, String card, String rarity) {
        if(usingSqlite) return deleteCardSqlite(p,card,rarity);
        if (hasCard(p, card, rarity) > 0) {
            String uuidString = p.getUniqueId().toString();
            int deckNumber = 0;
            ConfigurationSection deckList = getDeckData().getConfigurationSection("Decks.Inventories." + uuidString);
            if (deckList != null) for (String s: deckList.getKeys(false)) {
                deckNumber += Integer.valueOf(s).intValue();
                debugMsg("[Cards] Deck running total: " + deckNumber);
            }
            if (deckNumber == 0) {
                debugMsg("[Cards] No decks?!");
            } else {
                debugMsg("[Cards] Decks:" + deckNumber);
                for (int i = 0; i < deckNumber; i++) {
                    if (getDeckData().contains("Decks.Inventories." + uuidString + "." + (i + 1))) {
                        List < String > contents = getDeckData().getStringList("Decks.Inventories." + uuidString + "." + (i + 1));
                        List < String > contentsNew = new ArrayList < >();
                        for (String s2: contents) {
                            String[] splitContents = s2.split(",");
                            if (getConfig().getBoolean("General.Eat-Shiny-Cards") && splitContents[3].equalsIgnoreCase("yes")) {
                                debugMsg("[Cards] Eat-Shiny-Cards is true and card is shiny!");
                                if (splitContents[0].equalsIgnoreCase(rarity)) {
                                    if (splitContents[1].equalsIgnoreCase(card)) {
                                        if (Integer.valueOf(splitContents[2]).intValue() <= 1) continue;
                                        int number = Integer.valueOf(splitContents[2]).intValue();
                                        splitContents[2] = String.valueOf(number - 1);
                                        StringBuilder strBuilder = new StringBuilder();
                                        for (int a = 0; a < splitContents.length; a++) {
                                            strBuilder.append(splitContents[a]);
                                            strBuilder.append(",");

                                        }
                                        String newString = strBuilder.substring(0, strBuilder.length() - 1);
                                        contentsNew.add(newString);
                                        continue;
                                    }
                                    contentsNew.add(s2);
                                    continue;
                                }
                                contentsNew.add(s2);
                                continue;
                            }
                            if (getConfig().getBoolean("General.Eat-Shiny-Cards") && splitContents[3].equalsIgnoreCase("no")) {
                                debugMsg("[Cards] Eat-Shiny-Cards is true and card is not shiny!");
                                if (splitContents[0].equalsIgnoreCase(rarity)) {
                                    if (splitContents[1].equalsIgnoreCase(card)) {
                                        if (Integer.valueOf(splitContents[2]).intValue() <= 1) continue;
                                        int number = Integer.valueOf(splitContents[2]).intValue();
                                        splitContents[2] = String.valueOf(number - 1);
                                        StringBuilder strBuilder = new StringBuilder();
                                        for (int a = 0; a < splitContents.length; a++) {
                                            strBuilder.append(splitContents[a]);
                                            strBuilder.append(",");

                                        }
                                        String newString = strBuilder.substring(0, strBuilder.length() - 1);
                                        contentsNew.add(newString);
                                        continue;
                                    }
                                    contentsNew.add(s2);
                                    continue;
                                }
                                contentsNew.add(s2);
                                continue;
                            }
                            if (!getConfig().getBoolean("General.Eat-Shiny-Cards") && splitContents[3].equalsIgnoreCase("yes")) {
                                debugMsg("[Cards] Eat-Shiny-Cards is false and card is shiny!");
                                if (!splitContents[0].equalsIgnoreCase(rarity)) continue;
                                debugMsg("[Cards] Adding card..");
                                contentsNew.add(s2);
                                continue;
                            }
                            if (getConfig().getBoolean("General.Eat-Shiny-Cards") || !splitContents[3].equalsIgnoreCase("no")) continue;
                            debugMsg("[Cards] Eat-Shiny-Cards is false and card is not shiny!");
                            if (splitContents[0].equalsIgnoreCase(rarity)) {
                                if (splitContents[1].equalsIgnoreCase(card)) {
                                    if (Integer.valueOf(splitContents[2]).intValue() <= 1) continue;
                                    int number = Integer.valueOf(splitContents[2]).intValue();
                                    splitContents[2] = String.valueOf(number - 1);
                                    StringBuilder strBuilder = new StringBuilder();
                                    for (int a = 0; a < splitContents.length; a++) {
                                        strBuilder.append(splitContents[a]);
                                        strBuilder.append(",");

                                    }
                                    String newString = strBuilder.substring(0, strBuilder.length() - 1);
                                    contentsNew.add(newString);
                                    continue;
                                }
                                contentsNew.add(s2);
                                continue;
                            }
                            contentsNew.add(s2);
                        }
                        getDeckData().set("Decks.Inventories." + uuidString + "." + (i + 1), contentsNew);
                        saveDeckData();
                        reloadCustomConfig();
                        contentsNew.clear();
                    }
                }
            }
        }
        return true;
    }

    public int getCardID(String name, String rarity) {
        Integer cardID = (Integer) getDatabase("trading_cards").queryValue("SELECT id FROM cards WHERE name = '" + name + "' AND rarity = '" + rarity + "'", "ID");
        return cardID;
    }

    public int getCardCount(String uuid, Integer deckNum, Integer cardID) {
        Integer cardCount = (Integer) getDatabase("trading_cards").queryValue("SELECT count FROM decks WHERE uuid = '" + uuid + "' AND deckID = "+ deckNum +" AND card = " + cardID + "", "ID");
        return cardCount;
    }

    public int getPlayerDeckFromCard(String uuid, Integer cardID) {
        Integer deckID = (Integer) getDatabase("trading_cards").queryValue("SELECT deckID FROM decks WHERE uuid = '" + uuid + "' AND card = " + cardID + "", "ID");
        return deckID;
    }

    public int hasCard(Player p, String card, String rarity) {
        int deckNumber = 0;
        debugMsg("[Cards] Started check for card: " + card + ", " + rarity);
        String uuidString = p.getUniqueId().toString();
        if(usingSqlite) {
            int cardID = getCardID(card,rarity);
            if(getDatabase("trading_cards").queryValue("SELECT count FROM decks WHERE uuid = '"+uuidString+"' AND card = "+cardID, "ID") == null) {
                return 0;
            } else {
                int deckID = getPlayerDeckFromCard(uuidString,cardID);
                return getCardCount(uuidString,deckID,cardID);
            }
        } else {
            ConfigurationSection deckList = getDeckData().getConfigurationSection("Decks.Inventories." + uuidString);
            debugMsg("[Cards] Deck UUID: " + uuidString);
            if (getConfig().getBoolean("General.Debug-Mode") && getDeckData().contains("Decks.Inventories." + uuidString))
                System.out.println("[Cards] Deck.yml contains player!");
            if (getConfig().getBoolean("General.Debug-Mode")) {
                for (String s : deckList.getKeys(false))
                    System.out.println("[Cards] Deck rarity content: " + s);
                debugMsg("[Cards] Done!");
            }
            if (deckList == null) return 0;
            for (String s : deckList.getKeys(false)) {
                deckNumber += Integer.valueOf(s).intValue();
                if (getConfig().getBoolean("General.Debug-Mode"))
                    System.out.println("[Cards] Deck running total: " + deckNumber);

            }
            if (deckNumber == 0) {
                debugMsg("[Cards] No decks?!");
                return 0;
            }
            debugMsg("[Cards] Decks:" + deckNumber);
            for (int i = 0; i < deckNumber; i++) {
                debugMsg("[Cards] Starting iteration " + i);
                if (getDeckData().contains("Decks.Inventories." + uuidString + "." + (i + 1))) {
                    List<String> contents = getDeckData().getStringList("Decks.Inventories." + uuidString + "." + (i + 1));
                    for (String s2 : contents) {
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] Deck file content: " + s2);
                        String[] splitContents = s2.split(",");
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] " + card + " - " + splitContents[1]);
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] " + rarity + " - " + splitContents[0]);
                        if (splitContents[0].equalsIgnoreCase(rarity)) {
                            if (getConfig().getBoolean("General.Debug-Mode"))
                                System.out.println("[Cards] Rarity match: " + splitContents[0]);
                            if (splitContents[1].equalsIgnoreCase(card) && splitContents[3].equalsIgnoreCase("no")) {
                                if (getConfig().getBoolean("General.Debug-Mode"))
                                    System.out.println("[Cards] Card match: " + splitContents[1]);
                                return Integer.valueOf(splitContents[2]).intValue();
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public boolean hasShiny(Player p, String card, String rarity) {
        int deckNumber = 0;
        debugMsg("[Cards] Started check for card: " + card + ", " + rarity);
        String uuidString = p.getUniqueId().toString();
        if(usingSqlite) {
            int cardID = getCardID(card,rarity);
            if(getDatabase("trading_cards").queryValue("SELECT count FROM decks WHERE uuid = '"+uuidString+"' AND card = "+cardID, "ID") == null) {
                return false;
            } else {
                Integer isShiny = (Integer) getDatabase("trading_cards").queryValue("SELECT isShiny FROM decks WHERE uuid = '" + uuidString + "' AND card = " + cardID + "", "ID");
                if(isShiny == 1) return true;
                else return false;
            }
        } else {
            ConfigurationSection deckList = getDeckData().getConfigurationSection("Decks.Inventories." + uuidString);
            debugMsg("[Cards] Deck UUID: " + uuidString);
            if ((getConfig().getBoolean("General.Debug-Mode") & getDeckData().contains("Decks.Inventories." + uuidString)))
                System.out.println("[Cards] Deck.yml contains player!");
            if (getConfig().getBoolean("General.Debug-Mode")) {
                for (String s : deckList.getKeys(false))
                    System.out.println("[Cards] Deck rarity content: " + s);
                debugMsg("[Cards] Done!");
            }
            if (deckList == null) return false;
            for (String s : deckList.getKeys(false)) {
                deckNumber += Integer.valueOf(s).intValue();
                if (getConfig().getBoolean("General.Debug-Mode"))
                    System.out.println("[Cards] Deck running total: " + deckNumber);

            }
            if (deckNumber == 0) {
                debugMsg("[Cards] No decks?!");
                return false;
            }
            debugMsg("[Cards] Decks:" + deckNumber);
            for (int i = 0; i < deckNumber; i++) {
                debugMsg("[Cards] Starting iteration " + i);
                if (getDeckData().contains("Decks.Inventories." + uuidString + "." + (i + 1))) {
                    List<String> contents = getDeckData().getStringList("Decks.Inventories." + uuidString + "." + (i + 1));
                    for (String s2 : contents) {
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] Deck file content: " + s2);
                        String[] splitContents = s2.split(",");
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] " + card + " - " + splitContents[1]);
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] " + rarity + " - " + splitContents[0]);
                        if (splitContents[0].equalsIgnoreCase(rarity)) {
                            if (getConfig().getBoolean("General.Debug-Mode"))
                                System.out.println("[Cards] Rarity match: " + splitContents[0]);
                            if (!splitContents[1].equalsIgnoreCase(card)) continue;
                            if (getConfig().getBoolean("General.Debug-Mode"))
                                System.out.println("[Cards] Card match: " + splitContents[1]);
                            if (splitContents[3].equalsIgnoreCase("yes")) return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public void openDeck(Player p, int deckNum) {
        debugMsg("[Cards] Deck opened.");
        String uuidString = p.getUniqueId().toString();
        debugMsg("[Cards] Deck UUID: " + uuidString);
        /*if(usingSqlite) {
            Map rows = getDatabase("trading_cards").queryMultipleRows("SELECT * FROM decks WHERE uuid = '"+uuidString+"' AND deckID = "+deckNum, "card");
            int cardID = (Integer)rows.get("trading_cards").get(0);
        }*/
        List < String > contents = getDeckData().getStringList("Decks.Inventories." + uuidString + "." + deckNum);
        List < ItemStack > cards = new ArrayList < >();
        List < Integer > quantity = new ArrayList < >();
        ItemStack card = null;
        boolean isNull = false;
        for (String s: contents) {
            debugMsg("[Cards] Deck file content: " + s);
            String[] splitContents = s.split(",");
            if (splitContents.length > 1) {
                if (splitContents[1] == null) splitContents[1] = "None";
                if (splitContents[3].equalsIgnoreCase("yes")) {
                    if (!splitContents[0].equalsIgnoreCase("BLANK") && !splitContents[1].equalsIgnoreCase("None") && splitContents[1] != null && !splitContents[1].isEmpty()) {
                        card = createPlayerCard(splitContents[1], splitContents[0], Integer.valueOf(splitContents[2]), true);
                    } else {
                        System.out.println("[Cards] Warning! A null card has been found in a deck. It was truncated for safety.");
                        isNull = true;
                    }
                } else if (!splitContents[1].equalsIgnoreCase("None") && !splitContents[0].equalsIgnoreCase("BLANK") && splitContents[1] != null && !splitContents[1].isEmpty()) {
                    card = getNormalCard(splitContents[1], splitContents[0], Integer.valueOf(splitContents[2]));
                } else {
                    System.out.println("[Cards] Warning! A null card has been found in a deck. It was truncated for safety.");
                    isNull = true;
                }
            }
            if (!isNull) cards.add(card);
            if (splitContents.length > 1) {
                quantity.add(Integer.valueOf(splitContents[2]));
            } else {
                quantity.add(Integer.valueOf(1));
            }
            isNull = false;
            if (splitContents.length > 1 && getConfig().getBoolean("General.Debug-Mode") && !isNull) {
                System.out.println("[Cards] Put " + card + "," + splitContents[2] + " into respective lists.");
                continue;
            }
            if (!getConfig().getBoolean("General.Debug-Mode") || isNull) continue;
            System.out.println("[Cards] Put spacer into list.");
        }
        int invSlots = 27;
        if (getConfig().getBoolean("General.Use-Large-Decks")) invSlots = 54;
        Inventory inv = Bukkit.createInventory(null, invSlots, cMsg("&c" + p.getName() + "'s Deck #" + deckNum));
        debugMsg("[Cards] Created inventory.");
        int iter = 0;
        for (ItemStack i: cards) {
            debugMsg("[Cards] Item " + i.getType().toString() + " added to inventory!");
            i.setAmount(quantity.get(iter).intValue());
            inv.addItem(i);
            iter++;
        }
        iter = 0;
        p.openInventory(inv);
    }

    public String isRarity(String input) {
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        if (getConfig().contains("Rarities." + input.replaceAll("_", " "))) return input.replaceAll("_", " ");
        if (getConfig().contains("Rarities." + input.replaceAll("_", " ").toUpperCase())) return input.replaceAll("_", " ").toUpperCase();
        if (getConfig().contains("Rarities." + input.replaceAll("_", " ").toLowerCase())) return input.replaceAll("_", " ").toLowerCase();
        if (getConfig().contains("Rarities." + output.replaceAll("_", " "))) return output.replaceAll("_", " ");
        if (getConfig().contains("Rarities." + capitaliseUnderscores(input))) return output.replaceAll("_", " ");
        return "None";
    }

    public String capitaliseUnderscores(String input) {
        String[] strArray = input.split("_");
        String[] finalArray = new String[strArray.length];
        StringBuilder finalized = new StringBuilder();
        for (int i = 0; i < strArray.length; i++) {
            finalized.append(finalArray[i] = strArray[i].toLowerCase().substring(0, 1).toUpperCase() + strArray[i].substring(1));

            finalized.append("_");
        }
        String finalString = finalized.substring(0, finalized.length() - 1);
        return finalString;
    }

    public boolean isMob(String input) {
        EntityType type = null;
        try {
            type = EntityType.valueOf(input.toUpperCase());
            return ! (!this.hostileMobs.contains(type) && !this.neutralMobs.contains(type) && !this.passiveMobs.contains(type) && !this.bossMobs.contains(type));
        } catch(IllegalArgumentException ex) {

            return false;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        String viewTitle = e.getView().getTitle();
        debugMsg("[Cards] Title: " + viewTitle);
        if (viewTitle.contains("s Deck #")) {
            debugMsg("[Cards] Deck closed.");
            ItemStack[] contents = e.getInventory().getContents();
            String[] title = viewTitle.split("'");
            String[] titleNum = viewTitle.split("#");
            int deckNum = Integer.valueOf(titleNum[1]).intValue();
            debugMsg("[Cards] Deck num: " + deckNum);
            debugMsg("[Cards] Title: " + title[0]);
            debugMsg("[Cards] Title: " + title[1]);
            UUID id = Bukkit.getOfflinePlayer(ChatColor.stripColor(title[0])).getUniqueId();
            debugMsg("[Cards] New ID: " + id.toString());
            List < String > serialized = new ArrayList < >();
            ItemStack[] arrayOfItemStack1;
            for (int j = (arrayOfItemStack1 = contents).length, i = 0; i < j; i++) {

                ItemStack it = arrayOfItemStack1[i];
                if (it != null && it.getType() == Material.valueOf(getConfig().getString("General.Card-Material")) && it.getItemMeta().hasDisplayName()) {
                    List < String > lore = it.getItemMeta().getLore();
                    String shinyPrefix = getConfig().getString("General.Shiny-Name");
                    String rarity = ChatColor.stripColor(lore.get(lore.size() - 1)).replaceAll(shinyPrefix + " ", "");
                    String card = getCardName(rarity, it.getItemMeta().getDisplayName());
                    String amount = String.valueOf(it.getAmount());
                    String shiny = "no";
                    if (it.containsEnchantment(Enchantment.ARROW_INFINITE)) shiny = "yes";
                    String serializedString = rarity + "," + card + "," + amount + "," + shiny;
                    serialized.add(serializedString);
                    debugMsg("[Cards] Added " + serializedString + " to deck file.");
                } else if (it != null && getConfig().getBoolean("General.Drop-Deck-Items")) {
                    Player p = Bukkit.getPlayer(ChatColor.stripColor(title[0]));
                    World w = p.getWorld();
                    w.dropItemNaturally(p.getLocation(), it);
                }
                getDeckData().set("Decks.Inventories." + id.toString() + "." + deckNum, serialized);
                saveDeckData();
            }
        }
    }
    
    public void debugMsg(String msg) {
        if (getConfig().getBoolean("General.Debug-Mode")) System.out.println(msg);
    }

    public String getCardName(String rarity, String display) {
        boolean hasPrefix = false;
        String prefix = "";
        if (getConfig().contains("General.Card-Prefix") && getConfig().getString("General.Card-Prefix") != "") {
            hasPrefix = true;
            prefix = ChatColor.stripColor(getConfig().getString("General.Card-Prefix"));
        }
        String shinyPrefix = getConfig().getString("General.Shiny-Name");
        String cleaned = ChatColor.stripColor(display);
        if (hasPrefix) cleaned = cleaned.replaceAll(prefix, "");
        cleaned = cleaned.replaceAll(shinyPrefix + " ", "");
        String[] cleanedArray = cleaned.split(" ");
        ConfigurationSection cs = getCardsData().getConfigurationSection("Cards." + rarity);
        Set < String > keys = cs.getKeys(false);
        for (String s: keys) {
            debugMsg("[Cards] getCardName s: " + s);
            debugMsg("[Cards] getCardName display: " + display);
            if (cleanedArray.length > 1) {
                debugMsg("[Cards] cleanedArray > 1");
                if ((cleanedArray[0] + "_" + cleanedArray[1]).matches(s)) return s;
                if ((cleanedArray[0] + " " + cleanedArray[1]).matches(s)) return s;
                if (cleanedArray.length > 2 && (cleanedArray[1] + "_" + cleanedArray[2]).matches(s)) return s;
                if (cleanedArray.length > 2 && (cleanedArray[1] + " " + cleanedArray[2]).matches(s)) return s;
                if (cleanedArray.length > 3 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3]).matches(s)) return s;
                if (cleanedArray.length > 3 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3]).matches(s)) return s;
                if (cleanedArray.length > 4 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3] + "_" + cleanedArray[4]).matches(s)) return s;
                if (cleanedArray.length > 4 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3] + " " + cleanedArray[4]).matches(s)) return s;
                if (cleanedArray.length > 5 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3] + "_" + cleanedArray[4] + "_" + cleanedArray[5]).matches(s)) return s;
                if (cleanedArray.length > 5 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3] + " " + cleanedArray[4] + " " + cleanedArray[5]).matches(s)) return s;
                if (cleanedArray.length > 6 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3] + "_" + cleanedArray[4] + "_" + cleanedArray[5] + "_" + cleanedArray[6]).matches(s)) return s;
                if (cleanedArray.length > 6 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3] + " " + cleanedArray[4] + " " + cleanedArray[5] + " " + cleanedArray[6]).matches(s)) return s;
                if (cleanedArray.length == 1 && cleanedArray[0].matches(s)) return s;
                if (cleanedArray.length == 2 && cleanedArray[1].matches(s)) return s;
            }
        }
        return "None";
    }

    public ItemStack createBoosterPack(String name) {
        ItemStack boosterPack = getBlankBoosterPack();
        String packName = name;
        int numNormalCards = getConfig().getInt("BoosterPacks." + name + ".NumNormalCards");
        int numSpecialCards = getConfig().getInt("BoosterPacks." + name + ".NumSpecialCards");
        String prefix = getConfig().getString("General.BoosterPack-Prefix");
        String normalCardColour = getConfig().getString("Colours.BoosterPackNormalCards");
        String extraCardColour = getConfig().getString("Colours.BoosterPackExtraCards");
        String loreColour = getConfig().getString("Colours.BoosterPackLore");
        String nameColour = getConfig().getString("Colours.BoosterPackName");
        String normalRarity = getConfig().getString("BoosterPacks." + name + ".NormalCardRarity");
        String specialRarity = getConfig().getString("BoosterPacks." + name + ".SpecialCardRarity");
        String extraRarity = "";
        int numExtraCards = 0;
        boolean hasExtraRarity = false;
        if (getConfig().contains("BoosterPacks." + name + ".ExtraCardRarity") && getConfig().contains("BoosterPacks." + name + ".NumExtraCards")) {
            hasExtraRarity = true;
            extraRarity = getConfig().getString("BoosterPacks." + name + ".ExtraCardRarity");
            numExtraCards = getConfig().getInt("BoosterPacks." + name + ".NumExtraCards");
        }
        String specialCardColour = getConfig().getString("Colours.BoosterPackSpecialCards");
        ItemMeta pMeta = boosterPack.getItemMeta();
        pMeta.setDisplayName(cMsg(prefix + nameColour + packName.replaceAll("_", " ")));
        List < String > lore = new ArrayList < >();
        lore.add(cMsg(String.valueOf(normalCardColour) + numNormalCards + loreColour + " " + normalRarity.toUpperCase()));
        if (hasExtraRarity) lore.add(cMsg(String.valueOf(extraCardColour) + numExtraCards + loreColour + " " + extraRarity.toUpperCase()));
        lore.add(cMsg(String.valueOf(specialCardColour) + numSpecialCards + loreColour + " " + specialRarity.toUpperCase()));
        pMeta.setLore(lore);
        if (getConfig().getBoolean("General.Hide-Enchants", true)) pMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        boosterPack.setItemMeta(pMeta);
        boosterPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        return boosterPack;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            EquipmentSlot e = event.getHand();
            if (e.equals(EquipmentSlot.HAND)) {
                Player p = event.getPlayer();
                if (p.getInventory().getItemInMainHand().getType() == Material.valueOf(getConfig().getString("General.BoosterPack-Material")) && event.getPlayer().hasPermission("cards.openboosterpack") && p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.ARROW_INFINITE))

                    if (p.getGameMode() != GameMode.CREATIVE) {
                        ItemStack boosterPack = event.getPlayer().getInventory().getItemInMainHand();
                        ItemMeta packMeta = boosterPack.getItemMeta();
                        List < String > lore = packMeta.getLore();
                        if (p.getInventory().getItemInMainHand().getAmount() > 1) {
                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        } else {
                            p.getInventory().removeItem(p.getInventory().getItemInMainHand());
                        }
                        boolean hasExtra = false;
                        if (lore.size() > 2) hasExtra = true;
                        String[] line1 = lore.get(0).split(" ", 2);
                        String[] line2 = lore.get(1).split(" ", 2);
                        String[] line3 = {
                                ""
                        };
                        if (hasExtra) line3 = lore.get(2).split(" ", 2);
                        int normalCardAmount = Integer.valueOf(ChatColor.stripColor(line1[0])).intValue();
                        int specialCardAmount = Integer.valueOf(ChatColor.stripColor(line2[0])).intValue();
                        int extraCardAmount = 0;
                        if (hasExtra) extraCardAmount = Integer.valueOf(ChatColor.stripColor(line3[0])).intValue();
                        p.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.OpenBoosterPack")));
                        int i;
                        String prefix = ChatColor.stripColor(cMsg(getConfig().getString("General.BoosterPack-Prefix")));
                        String packName = "None";
                        if(packMeta.hasDisplayName()) {
                            packName = ChatColor.stripColor(packMeta.getDisplayName());
                            if (getConfig().getBoolean("General.Debug-Mode")) {
                                System.out.println("[Cards] Pack name first pass: " + packName);
                                System.out.println("[Cards] Prefix is: " + prefix);
                            }
                            packName = packName.replace(prefix, "");
                            if (getConfig().getBoolean("General.Debug-Mode"))
                                System.out.println("[Cards] Pack name second pass: " + packName);
                            packName = packName.replaceAll(" ", "_");
                            if (getConfig().getBoolean("General.Debug-Mode"))
                                System.out.println("[Cards] Pack name third pass: "+packName);
                        }
                        if(packName.equals("None")) {
                            System.out.println("[Cards] Error: Pack name not found...");
                            return;
                        }
                        for (i = 0; i < normalCardAmount; i++) {
                            String normRarity = WordUtils.capitalizeFully(line1[1]);
                            debugMsg("[Cards] Pack name is: "+packName);
                            normRarity = upgradeRarity(packName, normRarity);
                            if (p.getInventory().firstEmpty() != -1) {
                                p.getInventory().addItem(generateCard(normRarity, false));
                            } else {
                                World curWorld = p.getWorld();
                                if (p.getGameMode() == GameMode.SURVIVAL) {
                                    curWorld.dropItem(p.getLocation(), generateCard(normRarity, false));
                                }
                            }
                        }
                        for (i = 0; i < specialCardAmount; i++) {
                            String specRarity = WordUtils.capitalizeFully(line2[1]);
                            debugMsg("[Cards] Pack name is: "+packName);
                            specRarity = upgradeRarity(packName, specRarity);
                            if (p.getInventory().firstEmpty() != -1) {
                                p.getInventory().addItem(generateCard(specRarity, false));
                            } else {
                                World curWorld = p.getWorld();
                                if (p.getGameMode() == GameMode.SURVIVAL) {
                                    curWorld.dropItem(p.getLocation(), generateCard(specRarity, false));
                                }
                            }
                        }
                        if (hasExtra) for (i = 0; i < extraCardAmount; i++) {
                            String extrRarity = WordUtils.capitalizeFully(line3[1]);
                            debugMsg("[Cards] Pack name is: "+packName);
                            extrRarity = upgradeRarity(packName, extrRarity);
                            if (p.getInventory().firstEmpty() != -1) {
                                p.getInventory().addItem(generateCard(extrRarity, false));
                            } else {
                                World curWorld = p.getWorld();
                                if (p.getGameMode() == GameMode.SURVIVAL) {
                                    curWorld.dropItem(p.getLocation(), generateCard(extrRarity, false));
                                }
                            }
                        }
                    } else {
                        event.getPlayer().sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoCreative")));
                    }
                if (p.getInventory().getItemInMainHand().getType() == Material.valueOf(getConfig().getString("General.Deck-Material"))) {
                    debugMsg("[Cards] Deck material...");
                    debugMsg("[Cards] Not creative...");
                    if (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.DURABILITY)) {
                        debugMsg("[Cards] Has enchant...");
                        if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DURABILITY) == 10) {
                            debugMsg("[Cards] Enchant is level 10...");
                            if (p.getGameMode() != GameMode.CREATIVE) {
                                String name = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                                String[] nameSplit = name.split("#");
                                int num = Integer.valueOf(nameSplit[1]).intValue();
                                openDeck(p, num);
                            } else {
                                event.getPlayer().sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.DeckCreativeError")));
                            }
                        }
                    }
                }
            }
        }
    }

    public String upgradeRarity(String packName, String rarity) {
        debugMsg("[Cards] Starting booster pack upgrade check - Current rarity is "+rarity+"!");
        ConfigurationSection rarities = getConfig().getConfigurationSection("Rarities");
        Set < String > rarityKeys = rarities.getKeys(false);
        Map < Integer,String > rarityMap = new HashMap < >();
        int i = 0;
        int curRarity = 0;
        for (String key: rarityKeys) {
            rarityMap.put(Integer.valueOf(i), key);
            if(key.equalsIgnoreCase(rarity)) curRarity = i;
            debugMsg("[Cards] Rarity " + i + " is " + key);
            i++;
        }
        int chance = getConfig().getInt("BoosterPacks." + packName + ".UpgradeChance", 0);
        if(chance <= 0) {
            debugMsg("[Cards] Pack has upgrade chance set to 0! Exiting..");
            return rarityMap.get(curRarity);
        }
        int random = this.r.nextInt(100000) + 1;
        if(random <= chance) {
            if(curRarity < i) curRarity++;
            debugMsg("[Cards] Card upgraded! new rarity is "+rarityMap.get(curRarity)+"!");
            return rarityMap.get(curRarity);
        }
        debugMsg("[Cards] Card not upgraded! Rarity remains at "+rarityMap.get(curRarity)+"!");
        return rarityMap.get(curRarity);
    }

    public String calculateRarity(EntityType e, boolean alwaysDrop) {
        int shouldItDrop = this.r.nextInt(100) + 1;
        int bossRarity = 0;
        String type = "";
        debugMsg("[Cards] shouldItDrop Num: " + shouldItDrop);
        if (isMobHostile(e)) {
            if (!alwaysDrop) {
                if (shouldItDrop > getConfig().getInt("Chances.Hostile-Chance")) return "None";
                type = "Hostile";
            } else {
                type = "Hostile";
            }
        } else if (isMobNeutral(e)) {
            if (!alwaysDrop) {
                if (shouldItDrop > getConfig().getInt("Chances.Neutral-Chance")) return "None";
                type = "Neutral";
            } else {
                type = "Neutral";
            }
        } else if (isMobPassive(e)) {
            if (!alwaysDrop) {
                if (shouldItDrop > getConfig().getInt("Chances.Passive-Chance")) return "None";
                type = "Passive";
            } else {
                type = "Passive";
            }
        } else {
            if (!isMobBoss(e)) return "None";
            if (!alwaysDrop) {
                if (shouldItDrop > getConfig().getInt("Chances.Boss-Chance")) return "None";
                if (getConfig().getBoolean("Chances.Boss-Drop")) bossRarity = getConfig().getInt("Chances.Boss-Drop-Rarity");
                type = "Boss";
            } else {
                type = "Boss";
            }

        }
        ConfigurationSection rarities = getConfig().getConfigurationSection("Rarities");
        Set < String > rarityKeys = rarities.getKeys(false);
        Map < String,
                Integer > rarityChances = new HashMap < >();
        Map < Integer,
                String > rarityIndexes = new HashMap < >();
        int i = 0;
        int mini = 0;
        int random = this.r.nextInt(100000) + 1;
        debugMsg("[Cards] Random Card Num: " + random);
        debugMsg("[Cards] Type: " + type);
        for (String key: rarityKeys) {
            rarityIndexes.put(Integer.valueOf(i), key);
            i++;
            debugMsg("[Cards] " + i + ", " + key);
            if (getConfig().contains("Chances." + key + "." + StringUtils.capitalize(e.getKey().getKey())) && mini == 0) {
                debugMsg("[Cards] Mini: " + i);
                mini = i;
            }
            int chance = getConfig().getInt("Chances." + key + "." + type, -1);
            debugMsg("[Cards] Keys: " + key + ", " + chance + ", i=" + i);
            rarityChances.put(key, Integer.valueOf(chance));
        }
        if (mini != 0) {
            debugMsg("[Cards] Mini: " + mini);
            debugMsg("[Cards] i: " + i);
            while (i >= mini) {
                i--;
                debugMsg("[Cards] i: " + i);
                int chance = getConfig().getInt("Chances." + rarityIndexes.get(Integer.valueOf(i)) + "." + StringUtils.capitalize(e.getKey().getKey()), -1);
                debugMsg("[Cards] Chance: " + chance);
                debugMsg("[Cards] Rarity: " + rarityIndexes.get(Integer.valueOf(i)));
                if (chance > 0) {
                    debugMsg("[Cards] Chance > 0");
                    if (random <= chance) {
                        debugMsg("[Cards] Random <= Chance");
                        return rarityIndexes.get(Integer.valueOf(i));
                    }
                }
            }
        } else {
            while (i > 0) {
                i--;
                debugMsg("[Cards] Final loop iteration " + i);
                debugMsg("[Cards] Iteration " + i + " in HashMap is: " + rarityIndexes.get(Integer.valueOf(i)) + ", " + getConfig().getString("Rarities." + rarityIndexes.get(Integer.valueOf(i)) + ".Name"));
                int chance = getConfig().getInt("Chances." + rarityIndexes.get(Integer.valueOf(i)) + "." + type, -1);
                debugMsg("[Cards] " + getConfig().getString("Rarities." + rarityIndexes.get(Integer.valueOf(i)) + ".Name") + "'s chance of dropping: " + chance + " out of 100,000");
                debugMsg("[Cards] The random number we're comparing that against is: " + random);
                if (chance > 0 && random <= chance) {
                    debugMsg("[Cards] Yup, looks like " + random + " is definitely lower than " + chance + "!");
                    debugMsg("[Cards] Giving a " + getConfig().getString("Rarities." + rarityIndexes.get(Integer.valueOf(i)) + ".Name") + " card.");
                    return rarityIndexes.get(Integer.valueOf(i));
                }
            }
        }
        return "None";
    }

    public boolean isOnList(Player p) {
        List < String > playersOnList = getConfig().getStringList("Blacklist.Players");
        return playersOnList.contains(p.getName());

    }

    public void addToList(Player p) {
        List < String > playersOnList = getConfig().getStringList("Blacklist.Players");
        playersOnList.add(p.getName());
        getConfig().set("Blacklist.Players", null);
        getConfig().set("Blacklist.Players", playersOnList);
        saveConfig();
    }

    public void removeFromList(Player p) {
        List < String > playersOnList = getConfig().getStringList("Blacklist.Players");
        playersOnList.remove(p.getName());
        getConfig().set("Blacklist.Players", null);
        getConfig().set("Blacklist.Players", playersOnList);
        saveConfig();
    }

    public char blacklistMode() {
        if (getConfig().getBoolean("Blacklist.Whitelist-Mode")) return 'w';
        return 'b';
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        boolean drop = false;
        String worldName = "";
        List < String > worlds = new ArrayList < >();
        if (e.getEntity().getKiller() instanceof Player) {
            Player p = e.getEntity().getKiller();
            drop = ((!isOnList(p) || blacklistMode() != 'b') && ((!isOnList(p) && blacklistMode() == 'b') || (isOnList(p) && blacklistMode() == 'w')));

            worldName = p.getWorld().getName();
            worlds = getConfig().getStringList("World-Blacklist");
            if (this.hasMobArena) {
                int i = 0;
                debugMsg("[Cards] Mob Arena checks starting.");
                if (this.am.getArenas() != null && !this.am.getArenas().isEmpty()) {
                    debugMsg("[Cards] There is at least 1 arena!");
                    for (Arena arena: this.am.getArenas()) {
                        i++;
                        debugMsg("[Cards] For arena #" + i + "...");
                        debugMsg("[Cards] In arena?: " + arena.inArena(p));
                        if (arena.inArena(p) || arena.inLobby(p)) {
                            debugMsg("[Cards] Killer is in an arena/lobby, so let's mess with the drops.");
                            if (getConfig().getBoolean("PluginSupport.MobArena.Disable-In-Arena")) drop = false;
                            if (!getConfig().getBoolean("General.Debug-Mode")) continue;
                            System.out.println("[Cards] Drops are now: " + drop);
                            continue;
                        }
                        if (!getConfig().getBoolean("General.Debug-Mode")) continue;
                        System.out.println("[Cards] Killer is not in this arena!");
                    }
                }
            }
        }
        if (drop && !worlds.contains(worldName)) {

            String rare = calculateRarity(e.getEntityType(), false);
            if (getConfig().getBoolean("Chances.Boss-Drop") && isMobBoss(e.getEntityType())) rare = getConfig().getString("Chances.Boss-Drop-Rarity");
            boolean cancelled = false;

            if (rare != "None") {
                if (getConfig().getBoolean("General.Spawner-Block") && e.getEntity().getCustomName() != null && e.getEntity().getCustomName().equals(getConfig().getString("General.Spawner-Mob-Name"))) {

                    debugMsg("[Cards] Mob came from spawner, not dropping card.");
                    cancelled = true;
                }
                if (!cancelled) {
                    debugMsg("[Cards] Successfully generated card.");

                    if (generateCard(rare, false) != null) e.getDrops().add(generateCard(rare, false));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (getConfig().getBoolean("General.Player-Drops-Card") && getConfig().getBoolean("General.Auto-Add-Players")) {
            Player player = e.getEntity().getKiller();
            if (player != null && player instanceof Player) {
                ConfigurationSection rarities = getConfig().getConfigurationSection("Rarities");
                Set < String > rarityKeys = rarities.getKeys(false);
                String k = null;
                for (String key: rarityKeys) {
                    if (getCardsData().contains("Cards." + key + "." + e.getEntity().getName())) {
                        debugMsg("[Cards] " + key);
                        k = key;
                    }
                }
                if (k != null) {
                    int rndm = this.r.nextInt(100) + 1;
                    if (rndm <= getConfig().getInt("General.Player-Drops-Card-Rarity")) {
                        e.getDrops().add(createPlayerCard(e.getEntity().getName(), k, Integer.valueOf(1), false));
                        debugMsg("[Cards] " + e.getDrops().toString());
                    }
                } else {
                    System.out.println("k is null");
                }
            }
        }
    }

    public ItemStack generateCard(String rare, boolean forcedShiny) {
        if (!rare.equals("None")) {
            String cost;
            debugMsg("[Cards] generateCard.rare: " + rare);
            ItemStack card = getBlankCard(1);
            reloadCustomConfig();
            ConfigurationSection cardSection = getCardsData().getConfigurationSection("Cards." + rare);
            debugMsg("[Cards] generateCard.cardSection: " + getCardsData().contains("Cards." + rare));
            debugMsg("[Cards] generateCard.rarity: " + rare);
            Set < String > cards = cardSection.getKeys(false);
            List < String > cardNames = new ArrayList < >();
            cardNames.addAll(cards);
            int cIndex = this.r.nextInt(cardNames.size());
            String cardName = cardNames.get(cIndex);
            boolean hasShinyVersion = getCardsData().getBoolean("Cards." + rare + "." + cardName + ".Has-Shiny-Version");
            boolean isShiny = false;
            if (hasShinyVersion) {
                int shinyRandom = this.r.nextInt(100) + 1;
                if (shinyRandom <= getConfig().getInt("Chances.Shiny-Version-Chance")) isShiny = true;
            }
            if (forcedShiny) isShiny = true;
            String rarityName = rare;
            String rarityColour = getConfig().getString("Rarities." + rare + ".Colour");
            String prefix = getConfig().getString("General.Card-Prefix");
            String series = getCardsData().getString("Cards." + rare + "." + cardName + ".Series");
            String seriesColour = getConfig().getString("Colours.Series");
            String seriesDisplay = getConfig().getString("DisplayNames.Cards.Series", "Series");
            String about = getCardsData().getString("Cards." + rare + "." + cardName + ".About", "None");
            String aboutColour = getConfig().getString("Colours.About");
            String aboutDisplay = getConfig().getString("DisplayNames.Cards.About", "About");
            String type = getCardsData().getString("Cards." + rare + "." + cardName + ".Type");
            String typeColour = getConfig().getString("Colours.Type");
            String typeDisplay = getConfig().getString("DisplayNames.Cards.Type", "Type");
            String info = getCardsData().getString("Cards." + rare + "." + cardName + ".Info", "None");
            String infoColour = getConfig().getString("Colours.Info");
            String infoDisplay = getConfig().getString("DisplayNames.Cards.Info", "Info");
            String shinyPrefix = getConfig().getString("General.Shiny-Name");
            if (getCardsData().contains("Cards." + rare + "." + cardName + ".Buy-Price")) {
                cost = String.valueOf(getCardsData().getDouble("Cards." + rare + "." + cardName + ".Buy-Price"));
            } else {
                cost = "None";
            }
            ItemMeta cmeta = card.getItemMeta();
            boolean isPlayerCard = false;
            if (isPlayerCard(cardName)) isPlayerCard = true;
            if (isShiny) {
                if (!isPlayerCard) {
                    cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.ShinyTitle").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("%SHINYPREFIX%", shinyPrefix).replaceAll("_", " ")));
                } else {
                    cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.ShinyTitle").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("%SHINYPREFIX%", shinyPrefix)));
                }
            } else if (!isPlayerCard) {
                cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("_", " ")));
            } else {
                cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost)));
            }
            List < String > lore = new ArrayList < >();
            lore.add(cMsg(typeColour + typeDisplay + ": &f" + type));
            if (info.equals("None") || info.equals("")) {
                lore.add(cMsg(infoColour + infoDisplay + ": &f" + info));
            } else {
                lore.add(cMsg(infoColour + infoDisplay + ":"));
                lore.addAll(wrapString(info));
            }
            lore.add(cMsg(seriesColour + seriesDisplay + ": &f" + series));
            if (getCardsData().contains("Cards." + rare + "." + cardName + ".About")) lore.add(cMsg(aboutColour + aboutDisplay + ": &f" + about));
            if (isShiny) {
                lore.add(cMsg(rarityColour + ChatColor.BOLD + getConfig().getString("General.Shiny-Name") + " " + rarityName));
            } else {
                lore.add(cMsg(rarityColour + ChatColor.BOLD + rarityName));
            }
            cmeta.setLore(lore);
            if (getConfig().getBoolean("General.Hide-Enchants", true)) cmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            card.setItemMeta(cmeta);
            if (isShiny) card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
            return card;
        }
        return null;
    }

    public List < String > wrapString(String s) {
        String parsedString = ChatColor.stripColor(s);
        String addedString = WordUtils.wrap(parsedString, getConfig().getInt("General.Info-Line-Length", 25), "\n", true);
        String[] splitString = addedString.split("\n");
        List < String > finalArray = new ArrayList < >();
        String[] arrayOfString1;
        for (int j = (arrayOfString1 = splitString).length, i = 0; i < j; i++) {

            String ss = arrayOfString1[i];
            System.out.println(ChatColor.getLastColors(ss));
            finalArray.add(cMsg("&f &7- &f" + ss));
        }
        return finalArray;
    }

    public String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil((s.length() / interval));
        String[] result = new String[arrayLength];
        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        }
        result[lastIndex] = s.substring(j);
        return result;
    }

    public ItemStack createPlayerCard(String cardName, String rarity, Integer num, boolean forcedShiny) {
        String cost;
        ItemStack card = getBlankCard(num.intValue());
        boolean hasShinyVersion = getCardsData().getBoolean("Cards." + rarity + "." + cardName + ".Has-Shiny-Version");
        boolean isShiny = false;
        if (hasShinyVersion) {
            int shinyRandom = this.r.nextInt(100) + 1;
            if (shinyRandom <= getConfig().getInt("Chances.Shiny-Version-Chance")) isShiny = true;
        }
        if (forcedShiny) isShiny = true;
        String rarityName = rarity;
        String rarityColour = getConfig().getString("Rarities." + rarity + ".Colour");
        String prefix = getConfig().getString("General.Card-Prefix");
        String series = getCardsData().getString("Cards." + rarity + "." + cardName + ".Series");
        String seriesColour = getConfig().getString("Colours.Series");
        String seriesDisplay = getConfig().getString("DisplayNames.Cards.Series", "Series");
        String about = getCardsData().getString("Cards." + rarity + "." + cardName + ".About", "None");
        String aboutColour = getConfig().getString("Colours.About");
        String aboutDisplay = getConfig().getString("DisplayNames.Cards.About", "About");
        String type = getCardsData().getString("Cards." + rarity + "." + cardName + ".Type");
        String typeColour = getConfig().getString("Colours.Type");
        String typeDisplay = getConfig().getString("DisplayNames.Cards.Type", "Type");
        String info = getCardsData().getString("Cards." + rarity + "." + cardName + ".Info");
        String infoColour = getConfig().getString("Colours.Info");
        String infoDisplay = getConfig().getString("DisplayNames.Cards.Info", "Info");
        String shinyPrefix = getConfig().getString("General.Shiny-Name");
        if (getCardsData().contains("Cards." + rarity + "." + cardName + ".Buy-Price")) {
            cost = String.valueOf(getCardsData().getDouble("Cards." + rarity + "." + cardName + ".Buy-Price"));
        } else {
            cost = "None";
        }
        ItemMeta cmeta = card.getItemMeta();
        boolean isPlayerCard = false;
        if (isPlayerCard(cardName.replaceAll(" ", "_"))) {
            debugMsg("[Cards] is player card = true");
            isPlayerCard = true;
        } else if (getConfig().getBoolean("General.Debug-Mode")) {
            System.out.println("[Cards] is player card = false");
        }
        if (isShiny) {
            if (isPlayerCard) {
                cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.ShinyTitle").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("%SHINYPREFIX%", shinyPrefix)));
            } else {
                cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.ShinyTitle").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("%SHINYPREFIX%", shinyPrefix).replaceAll("_", " ")));
            }
        } else if (isPlayerCard) {
            cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost)));
        } else {
            cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("_", " ")));
        }
        List < String > lore = new ArrayList < >();
        lore.add(cMsg(typeColour + typeDisplay + ": &f" + type));
        if (info.equals("None") || info.equals("")) {
            lore.add(cMsg(infoColour + infoDisplay + ": &f" + info));
        } else {
            lore.add(cMsg(infoColour + infoDisplay + ":"));
            lore.addAll(wrapString(info));
        }
        lore.add(cMsg(seriesColour + seriesDisplay + ": &f" + series));
        if (getCardsData().contains("Cards." + rarity + "." + cardName + ".About")) lore.add(cMsg(aboutColour + aboutDisplay + ": &f" + about));
        if (isShiny) {
            lore.add(cMsg(rarityColour + ChatColor.BOLD + getConfig().getString("General.Shiny-Name") + " " + rarityName));
        } else {
            lore.add(cMsg(rarityColour + ChatColor.BOLD + rarityName));
        }
        cmeta.setLore(lore);
        if (getConfig().getBoolean("General.Hide-Enchants", true)) cmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        card.setItemMeta(cmeta);
        if (isShiny) card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        return card;
    }

    public ItemStack getNormalCard(String cardName, String rarity, Integer num) {
        String cost;
        ItemStack card = getBlankCard(num.intValue());
        String rarityName = rarity;
        String rarityColour = getConfig().getString("Rarities." + rarity + ".Colour");
        String prefix = getConfig().getString("General.Card-Prefix");
        String series = getCardsData().getString("Cards." + rarity + "." + cardName + ".Series");
        String seriesColour = getConfig().getString("Colours.Series");
        String seriesDisplay = getConfig().getString("DisplayNames.Cards.Series", "Series");
        String about = getCardsData().getString("Cards." + rarity + "." + cardName + ".About", "None");
        String aboutColour = getConfig().getString("Colours.About");
        String aboutDisplay = getConfig().getString("DisplayNames.Cards.About", "About");
        String type = getCardsData().getString("Cards." + rarity + "." + cardName + ".Type");
        String typeColour = getConfig().getString("Colours.Type");
        String typeDisplay = getConfig().getString("DisplayNames.Cards.Type", "Type");
        String info = getCardsData().getString("Cards." + rarity + "." + cardName + ".Info");
        String infoColour = getConfig().getString("Colours.Info");
        String infoDisplay = getConfig().getString("DisplayNames.Cards.Info", "Info");
        if (getCardsData().contains("Cards." + rarity + "." + cardName + ".Buy-Price")) {
            cost = String.valueOf(getCardsData().getDouble("Cards." + rarity + "." + cardName + ".Buy-Price"));
        } else {
            cost = "None";
        }
        ItemMeta cmeta = card.getItemMeta();
        boolean isPlayerCard = false;
        if (isPlayerCard(cardName.replaceAll(" ", "_"))) {
            debugMsg("[Cards] is player card = true");
            isPlayerCard = true;
        } else if (getConfig().getBoolean("General.Debug-Mode")) {
            System.out.println("[Cards] is player card = false");
        }
        if (!isPlayerCard) {
            cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("_", " ")));
        } else {
            cmeta.setDisplayName(cMsg(getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost)));
        }
        List < String > lore = new ArrayList < >();
        lore.add(cMsg(typeColour + typeDisplay + ": &f" + type));
        if (info.equals("None") || info.equals("")) {
            lore.add(cMsg(infoColour + infoDisplay + ": &f" + info));
        } else {
            lore.add(cMsg(infoColour + infoDisplay + ":"));
            lore.addAll(wrapString(info));
        }
        lore.add(cMsg(seriesColour + seriesDisplay + ": &f" + series));
        if (getCardsData().contains("Cards." + rarity + "." + cardName + ".About")) lore.add(cMsg(aboutColour + aboutDisplay + ": &f" + about));
        lore.add(cMsg(rarityColour + ChatColor.BOLD + rarityName));
        cmeta.setLore(lore);
        if (getConfig().getBoolean("General.Hide-Enchants", true)) cmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        card.setItemMeta(cmeta);
        return card;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (! (e.getEntity() instanceof Player) && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER && getConfig().getBoolean("General.Spawner-Block")) {

            e.getEntity().setCustomName(getConfig().getString("General.Spawner-Mob-Name"));
            debugMsg("[Cards] Spawner mob renamed.");
            e.getEntity().setRemoveWhenFarAway(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (getConfig().getBoolean("General.Auto-Add-Players")) {
            int date,
                    month,
                    year;
            Player p = e.getPlayer();
            GregorianCalendar gc = new GregorianCalendar();
            if (p.hasPlayedBefore()) {
                gc.setTimeInMillis(p.getFirstPlayed());
                date = gc.get(5);
                month = gc.get(2) + 1;
                year = gc.get(1);
            } else {
                gc.setTimeInMillis(System.currentTimeMillis());
                date = gc.get(5);
                month = gc.get(2) + 1;
                year = gc.get(1);
            }
            ConfigurationSection rarities = getConfig().getConfigurationSection("Rarities");

            Set < String > rarityKeys = rarities.getKeys(false);
            Map < String,
                    Boolean > children = permRarities.getChildren();
            String rarity = getConfig().getString("General.Auto-Add-Player-Rarity");
            for (String key: rarityKeys) {

                children.put("cards.rarity." + key, Boolean.valueOf(false));
                permRarities.recalculatePermissibles();
                if (p.hasPermission("cards.rarity." + key)) {
                    rarity = key;
                    break;
                }
            }
            if (p.isOp()) rarity = getConfig().getString("General.Player-Op-Rarity");
            if (!getCardsData().contains("Cards." + rarity + "." + p.getName())) {
                String series = getConfig().getString("General.Player-Series");
                String type = getConfig().getString("General.Player-Type");
                boolean hasShiny = getConfig().getBoolean("General.Player-Has-Shiny-Version");
                getCardsData().set("Cards." + rarity + "." + p.getName() + ".Series", series);
                getCardsData().set("Cards." + rarity + "." + p.getName() + ".Type", type);
                getCardsData().set("Cards." + rarity + "." + p.getName() + ".Has-Shiny-Version", Boolean.valueOf(hasShiny));
                if (getConfig().getBoolean("General.American-Mode")) {
                    getCardsData().set("Cards." + rarity + "." + p.getName() + ".Info", "Joined " + month + "/" + date + "/" + year);
                } else {
                    getCardsData().set("Cards." + rarity + "." + p.getName() + ".Info", "Joined " + date + "/" + month + "/" + year);
                }
                saveCardsData();
                reloadCardsData();
            }
        }
    }

    public void giveawayNatural(EntityType mob, Player sender) {
        if (isMobBoss(mob)) {
            if (sender == null) {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalBossNoPlayer")));
            } else {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalBoss").replaceAll("%player%", sender.getName())));
            }
        } else if (isMobHostile(mob)) {
            if (sender == null) {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalHostileNoPlayer")));
            } else {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalHostile").replaceAll("%player%", sender.getName())));
            }
        } else if (isMobNeutral(mob)) {
            if (sender == null) {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalNeutralNoPlayer")));
            } else {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalNeutral").replaceAll("%player%", sender.getName())));
            }
        } else if (isMobPassive(mob)) {
            if (sender == null) {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalPassiveNoPlayer")));
            } else {
                Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalPassive").replaceAll("%player%", sender.getName())));
            }
        } else if (sender == null) {
            Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNaturalNoPlayer")));
        } else {
            Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayNatural").replaceAll("%player%", sender.getName())));
        }
        for (Player p: Bukkit.getOnlinePlayers()) {
            String rare = calculateRarity(mob, true);
            debugMsg("[Cards] onCommand.rare: " + rare);
            if (p.getInventory().firstEmpty() != -1) {
                if (generateCard(rare, false) == null) continue;
                p.getInventory().addItem(generateCard(rare, false));
                continue;
            }
            World curWorld = p.getWorld();
            if (p.getGameMode() != GameMode.SURVIVAL || generateCard(rare, false) == null) continue;
            curWorld.dropItem(p.getLocation(), generateCard(rare, false));
        }
    }

    public void createCard(Player creator, String rarity, String name, String series, String type, boolean hasShiny, String info, String about) {
        if (!getCardsData().contains("Cards." + rarity + "." + name)) {
            if (name.matches("^[a-zA-Z0-9-_]+$")) {
                if (isPlayerCard(name)) name = name.replaceAll(" ", "_");
                ConfigurationSection rarities = getCardsData().getConfigurationSection("Cards");
                Set < String > rarityKeys = rarities.getKeys(false);
                String keyToUse = "";
                for (String key: rarityKeys) {
                    if (key.equalsIgnoreCase(rarity)) keyToUse = key;
                }
                if (!keyToUse.equals("")) {
                    String series2 = "";
                    String type2 = "";
                    String info2 = "";
                    if (series.matches("^[a-zA-Z0-9-_]+$")) {
                        series2 = series;
                    } else {
                        series2 = "None";
                    }
                    if (type.matches("^[a-zA-Z0-9-_]+$")) {
                        type2 = type;
                    } else {
                        type2 = "None";
                    }
                    if (info.matches("^[a-zA-Z0-9-_/ ]+$")) {
                        info2 = info;

                    } else {
                        info2 = "None";
                    }
                    boolean hasShiny2 = ((hasShiny || !hasShiny) && hasShiny);
                    getCardsData().set("Cards." + rarity + "." + name + ".Series", series2);
                    getCardsData().set("Cards." + rarity + "." + name + ".Type", type2);
                    getCardsData().set("Cards." + rarity + "." + name + ".Has-Shiny-Version", Boolean.valueOf(hasShiny2));
                    getCardsData().set("Cards." + rarity + "." + name + ".Info", info2);
                    saveCardsData();
                    reloadCardsData();
                    creator.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CreateSuccess").replaceAll("%name%", name).replaceAll("%rarity%", rarity)));
                } else {
                    creator.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoRarity")));
                }
            } else {
                creator.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CreateNoName")));
            }
        } else {
            creator.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CreateExists")));
        }
    }

    public void reloadCustomConfig() {
        File file = new File(getDataFolder() + File.separator + "config.yml");
        if (!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        reloadConfig();
        reloadDeckData();
        reloadMessagesData();
        reloadCardsData();
        reloadDeckData();
        reloadMessagesData();
        reloadCardsData();
    }

    public Boolean exists(String statement) {
        if(getDatabase("trading_cards").queryValue(statement, "ID") == null) return false;
        else return true;
    }

    public void sqliteTransfer() {
        ConfigurationSection cards = getCardsData().getConfigurationSection("Cards");
        Set<String> cardKeys = cards.getKeys(false);
        // Key is rarity, probably
        for (String key : cardKeys) {
            ConfigurationSection cardsWithKey = getCardsData().getConfigurationSection("Cards." + key);
            Set<String> keyKeys = cardsWithKey.getKeys(false);
            for (String key2 : keyKeys) {
                String cost = "None";
                String series = getCardsData().getString("Cards." + key + "." + key2 + ".Series");
                String about = getCardsData().getString("Cards." + key + "." + key2 + ".About", "None");
                String type = getCardsData().getString("Cards." + key + "." + key2 + ".Type");
                String info = getCardsData().getString("Cards." + key + "." + key2 + ".Info");
                if (getCardsData().contains("Cards." + key + "." + key2 + ".Buy-Price"))
                    cost = String.valueOf(getCardsData().getDouble("Cards." + key + "." + key2 + ".Buy-Price"));
                if(!exists("SELECT * FROM cards WHERE rarity = '" + key + "' AND name = '" + key2 + "' AND about = '" + about + "' AND series = '" + series + "' AND type = '" + type + "' AND info = '" + info + "' AND price = '" + cost + "'")) {
                    if (getDatabase("trading_cards").executeStatement("INSERT INTO cards (rarity, name, about, series, type, info, price) VALUES ('" + key + "', '" + key2 + "', '" + about + "', '" + series + "', '" + type + "', '" + info + "', '" + cost + "')")) {
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] " + key + ", " + key2 + " - Added to SQLite!");
                    } else {
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] " + key + ", " + key2 + " - Unable to be added!");
                    }
                }
            }
        }
        // Deck time
        ConfigurationSection decks = getDeckData().getConfigurationSection("Decks.Inventories");
        Set<String> deckKeys = decks.getKeys(false);
        int deckNum = 0;
        // Key is UUID, probably
        for (String key : deckKeys) {
            debugMsg("[Cards] Deck key is: " + key);
            ConfigurationSection deckList = getDeckData().getConfigurationSection("Decks.Inventories." + key);
            if (deckList != null) for (String s : deckList.getKeys(false)) {
                deckNum += Integer.valueOf(s).intValue();
                if (getConfig().getBoolean("General.Debug-Mode"))
                    System.out.println("[Cards] Deck running total: " + deckNum);
            }
            if (deckNum == 0) {
                debugMsg("[Cards] No decks?!");
            } else {
                debugMsg("[Cards] Decks:" + deckNum);
                for (int i = 0; i < deckNum; i++) {
                    List<String> contents = getDeckData().getStringList("Decks.Inventories." + key + "." + deckNum);
                    for (String s : contents) {
                        if (getConfig().getBoolean("General.Debug-Mode"))
                            System.out.println("[Cards] Deck content: " + s);
                        String[] splitContents = s.split(",");
                        if (splitContents.length > 1) {
                            if (splitContents[1] == null) splitContents[1] = "None";
                            Integer cardID = (Integer) getDatabase("trading_cards").queryValue("SELECT id FROM cards WHERE name = '" + splitContents[1] + "' AND rarity = '" + splitContents[0] + "'", "ID");
                            // If card in deck is a shiny
                            if (splitContents[3].equalsIgnoreCase("yes")) {
                                if (!splitContents[0].equalsIgnoreCase("BLANK") && !splitContents[1].equalsIgnoreCase("None") && splitContents[1] != null && !splitContents[1].isEmpty()) {
                                    if(getDatabase("trading_cards").queryValue("SELECT * FROM decks WHERE uuid = '" + key + "' AND deckID = '" + deckNum + "' AND card = '" + cardID + "' AND isShiny = 1", "ID") == null) {
                                        if (getDatabase("trading_cards").executeStatement("INSERT INTO decks (uuid, deckID, card, isShiny, count) VALUES ('" + key + "', '" + deckNum + "', '" + cardID + "', 1, "+Integer.valueOf(splitContents[2])+")")) {
                                        } else {
                                            if (getConfig().getBoolean("General.Debug-Mode"))
                                                System.out.println("[Cards] Error adding shiny card to deck SQLite, check stack!");
                                        }
                                    }
                                }
                            } else if (!splitContents[1].equalsIgnoreCase("None") && !splitContents[0].equalsIgnoreCase("BLANK") && splitContents[1] != null && !splitContents[1].isEmpty()) {
                                if(getDatabase("trading_cards").queryValue("SELECT * FROM decks WHERE uuid = '" + key + "' AND deckID = '" + deckNum + "' AND card = '" + cardID + "' AND isShiny = 0", "ID") == null) {
                                    if (getDatabase("trading_cards").executeStatement("INSERT INTO decks (uuid, deckID, card, isShiny, count) VALUES ('" + key + "', '" + deckNum + "', '" + cardID + "', 0, "+Integer.valueOf(splitContents[2])+")")) {
                                    } else {
                                        if (getConfig().getBoolean("General.Debug-Mode"))
                                            System.out.println("[Cards] Error adding card to deck SQLite, check stack!");
                                    }
                                }
                            } else {
                                System.out.println("[Cards] Warning! A null card has been found in a deck. It was truncated for safety.");
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("cards")) {
            if (args.length <= 0) {
                boolean showUsage = getConfig().getBoolean("General.Show-Command-Usage", true);
                sender.sendMessage(cMsg(formatTitle(getConfig().getString("General.Server-Name") + " Trading Cards")));
                if (sender.hasPermission("cards.reload")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.ReloadUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.ReloadHelp")));
                }
                if (sender.hasPermission("cards.givecard")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.GiveCardUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.GiveCardHelp")));
                }
                if (sender.hasPermission("cards.giveshinycard")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.GiveShinyCardUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.GiveShinyCardHelp")));
                }
                if (sender.hasPermission("cards.giverandomcard")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.GiveRandomCardUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.GiveRandomCardHelp")));
                }
                if (sender.hasPermission("cards.giveboosterpack")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.GiveBoosterPackUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.GiveBoosterPackHelp")));
                }
                if (sender.hasPermission("cards.giveaway")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.GiveawayUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.GiveawayHelp")));
                }
                if (sender.hasPermission("cards.getdeck")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.GetDeckUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.GetDeckHelp")));
                }
                if (sender.hasPermission("cards.list")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.ListUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.ListHelp")));
                }
                if (sender.hasPermission("cards.listpacks")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.ListPacksUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.ListPacksHelp")));
                }
                if (sender.hasPermission("cards.toggle")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.ToggleUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.ToggleHelp")));
                }
                if (sender.hasPermission("cards.create")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.CreateUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.CreateHelp")));
                }
                if (sender.hasPermission("cards.buy") && this.hasVault) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.BuyUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.BuyHelp")));
                }
                if (sender.hasPermission("cards.worth") && this.hasVault) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.WorthUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.WorthHelp")));
                }
                if (sender.hasPermission("cards.resolve")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.ResolveUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.ResolveHelp")));
                }
                if (sender.hasPermission("cards.reward")) {
                    sender.sendMessage(cMsg("&7> &3" + getMessagesData().getString("Messages.RewardUsage")));
                    if (showUsage) sender.sendMessage(cMsg("   &7- &f&o" + getMessagesData().getString("Messages.RewardHelp")));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("cards.reload")) {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.Reload")));
                    reloadCustomConfig();
                    if (getConfig().getBoolean("General.Schedule-Cards")) startTimer();
                    return true;
                }
                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
            } else if (args[0].equalsIgnoreCase("resolve")) {
                if (sender.hasPermission("cards.resolve")) {
                    if (args.length < 2) {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ResolveUsage")));
                    } else {
                        Boolean online;
                        if (Bukkit.getPlayer(args[1]) != null) {
                            online = Boolean.valueOf(true);
                        } else {
                            online = Boolean.valueOf(false);

                        }
                        if (online.booleanValue()) {
                            Player p = Bukkit.getPlayer(args[1]);
                            String uuid = p.getUniqueId().toString();
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ResolveMsg").replaceAll("%name%", p.getName()).replaceAll("%uuid%", uuid)));

                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ResolveError").replaceAll("%name%", args[1])));

                        }
                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                }
            } else if (args[0].equalsIgnoreCase("toggle")) {
                Player p2 = (Player) sender;
                if (isOnList(p2) && blacklistMode() == 'b') {
                    removeFromList(p2);
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ToggleEnabled")));
                } else if (isOnList(p2) && blacklistMode() == 'w') {
                    removeFromList(p2);
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ToggleDisabled")));
                } else if (!isOnList(p2) && blacklistMode() == 'b') {
                    addToList(p2);
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ToggleDisabled")));
                } else if (!isOnList(p2) && blacklistMode() == 'w') {
                    addToList(p2);
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ToggleEnabled")));
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (sender.hasPermission("cards.create")) {
                    Player p2 = (Player) sender;
                    if (args.length < 8) {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CreateUsage")));
                    } else {
                        boolean isShiny = false;
                        isShiny = !(!args[5].equalsIgnoreCase("true") && !args[5].equalsIgnoreCase("yes") && !args[5].equalsIgnoreCase("y"));
                        createCard(p2, args[1].replaceAll("_", " "), args[2], args[3].replaceAll("_", " "), args[4].replaceAll("_", " "), isShiny, args[6].replaceAll("_", " "), args[7].replaceAll("_", " "));
                    }
                }
            } else if (args[0].equalsIgnoreCase("givecard")) {
                if (sender.hasPermission("cards.givecard")) {
                    if (args.length > 2) {
                        Player p2 = (Player) sender;
                        if (getCardsData().contains("Cards." + args[1].replaceAll("_", " ") + "." + args[2])) {
                            p2.getInventory().addItem(getNormalCard(args[2], args[1].replaceAll("_", " "), Integer.valueOf(1)));
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoCard")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveCardUsage")));
                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                }
            } else if (args[0].equalsIgnoreCase("giveshinycard")) {
                if (sender.hasPermission("cards.giveshinycard")) {
                    if (args.length > 2) {
                        Player p2 = (Player) sender;
                        if (getCardsData().contains("Cards." + args[1].replaceAll("_", " ") + "." + args[2])) {
                            p2.getInventory().addItem(createPlayerCard(args[2], args[1].replaceAll("_", " "), Integer.valueOf(1), true));
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoCard")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveShinyCardUsage")));
                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                }
            } else if (args[0].equalsIgnoreCase("giveboosterpack")) {
                if (sender.hasPermission("cards.giveboosterpack")) {
                    if (args.length > 2) {
                        if (getConfig().contains("BoosterPacks." + args[2].replaceAll(" ", "_"))) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                Player p2 = Bukkit.getPlayer(args[1]);
                                if (p2.getInventory().firstEmpty() != -1) {
                                    p2.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.BoosterPackMsg")));
                                    p2.getInventory().addItem(createBoosterPack(args[2]));
                                } else {
                                    World curWorld = p2.getWorld();
                                    if (p2.getGameMode() == GameMode.SURVIVAL) {
                                        p2.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.BoosterPackMsg")));
                                        curWorld.dropItem(p2.getLocation(), createBoosterPack(args[2]));
                                    }

                                }
                            } else {
                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPlayer")));
                            }
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoBoosterPack")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveBoosterPackUsage")));
                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                }
            } else if (args[0].equalsIgnoreCase("getdeck")) {
                if (sender.hasPermission("cards.getdeck")) {
                    if (args.length > 1) {
                        if (StringUtils.isNumeric(args[1])) {
                            if (sender.hasPermission("cards.decks." + args[1])) {
                                Player p2 = (Player) sender;
                                if (getConfig().getBoolean("General.Use-Deck-Item")) {
                                    if (!hasDeck(p2, Integer.valueOf(args[1]).intValue())) {
                                        if (p2.getInventory().firstEmpty() != -1) {
                                            p2.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveDeck")));
                                            p2.getInventory().addItem(createDeck(p2, Integer.valueOf(args[1]).intValue()));

                                        } else {
                                            World curWorld = p2.getWorld();
                                            if (p2.getGameMode() == GameMode.SURVIVAL) {
                                                p2.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveDeck")));
                                                curWorld.dropItem(p2.getLocation(), createDeck(p2, Integer.valueOf(args[1]).intValue()));
                                            }
                                        }
                                    } else {
                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.AlreadyHaveDeck")));
                                    }
                                } else if (p2.getGameMode() == GameMode.CREATIVE) {
                                    if (getConfig().getBoolean("General.Decks-In-Creative")) {
                                        openDeck(p2, Integer.valueOf(args[1]).intValue());
                                    } else {
                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.DeckCreativeError")));
                                    }
                                } else {
                                    openDeck(p2, Integer.valueOf(args[1]).intValue());
                                }
                            } else {
                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.MaxDecks")));
                            }
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GetDeckUsage")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GetDeckUsage")));
                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                }
            } else if (args[0].equalsIgnoreCase("giverandomcard")) {
                if (sender.hasPermission("cards.randomcard")) {
                    if (args.length > 2) {
                        if (Bukkit.getPlayer(args[2]) != null) {
                            Player p2 = Bukkit.getPlayer(args[2]);
                            try {
                                if (EntityType.valueOf(args[1].toUpperCase()) != null) {
                                    String rare = calculateRarity(EntityType.valueOf(args[1].toUpperCase()), true);
                                    debugMsg("[Cards] onCommand.rare: " + rare);
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveRandomCardMsg").replaceAll("%player%", p2.getName())));
                                    if (p2.getInventory().firstEmpty() != -1) {
                                        p2.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveRandomCard")));
                                        if (generateCard(rare, false) != null) p2.getInventory().addItem(generateCard(rare, false));
                                    } else {
                                        World curWorld2 = p2.getWorld();
                                        if (p2.getGameMode() == GameMode.SURVIVAL) {
                                            p2.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveRandomCard")));
                                            if (generateCard(rare, false) != null) curWorld2.dropItem(p2.getLocation(), generateCard(rare, false));

                                        }

                                    }
                                } else {
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoEntity")));
                                }
                            } catch(IllegalArgumentException e) {
                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoEntity")));
                            }
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPlayer")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveRandomCardUsage")));
                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("cards.list")) {
                    if (args.length >= 2) {
                        if (isRarity(args[1]).equalsIgnoreCase("None")) {
                            if (sender.hasPermission("cards.list.others")) {
                                boolean online2 = false;
                                online2 = (Bukkit.getPlayer(args[1]) != null);
                                if (online2) {
                                    Player p3 = Bukkit.getPlayer(args[1]);
                                    ConfigurationSection cards = getCardsData().getConfigurationSection("Cards");
                                    Set < String > cardKeys = cards.getKeys(false);
                                    String msg = "";
                                    int i = 0;
                                    int numCardsCounter = 0;
                                    String finalMsg = "";
                                    sender.sendMessage(cMsg("&e&l------- &7(&6&l" + p3.getName() + "'s Collection&7)&e&l -------"));
                                    for (String key: cardKeys) {
                                        ConfigurationSection cardsWithKey = getCardsData().getConfigurationSection("Cards." + key);
                                        Set < String > keyKeys = cardsWithKey.getKeys(false);
                                        for (String key2: keyKeys) {
                                            if (i > 32) {
                                                if (hasCard(p3, key2, key) > 0) numCardsCounter++;
                                                finalMsg = msg + "&7and more!";

                                                i++;
                                                continue;
                                            }
                                            debugMsg("[Cards] " + key + ", " + key2);
                                            String colour = getConfig().getString("Colours.ListHaveCard");
                                            if (hasShiny(p3, key2, key)) {
                                                numCardsCounter++;
                                                colour = getConfig().getString("Colours.ListHaveShinyCard");
                                                msg = msg + colour + key2.replaceAll("_", " ") + "&f, ";
                                            } else if (hasCard(p3, key2, key) > 0 && !hasShiny(p3, key2, key)) {
                                                numCardsCounter++;
                                                msg = msg + colour + key2.replaceAll("_", " ") + "&f, ";
                                            } else {
                                                msg = msg + "&7" + key2.replaceAll("_", " ") + "&f, ";
                                            }
                                            i++;
                                        }
                                        if (numCardsCounter >= i) {
                                            sender.sendMessage(cMsg("&6--- " + key + " &7(" + getConfig().getString("Colours.ListRarityComplete") + "Complete&7)&6 ---"));
                                        } else {
                                            sender.sendMessage(cMsg("&6--- " + key + " &7(&c" + numCardsCounter + "&f/&a" + i + "&7)&6 ---"));
                                        }
                                        msg = StringUtils.removeEnd(msg, ", ");
                                        if (finalMsg.equals("")) {
                                            sender.sendMessage(cMsg(msg));
                                        } else {
                                            sender.sendMessage(cMsg(finalMsg));
                                        }
                                        msg = "";
                                        finalMsg = "";
                                        i = 0;
                                        numCardsCounter = 0;
                                    }
                                } else {
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ListError").replaceAll("%name%", args[1])));
                                }
                            } else {
                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                            }
                        } else {
                            ConfigurationSection cards2 = getCardsData().getConfigurationSection("Cards." + isRarity(args[1]));
                            Set < String > cardKeys2 = cards2.getKeys(false);
                            String msg2 = "";
                            int j = 0;
                            int numCardsCounter2 = 0;
                            Player p4 = (Player) sender;
                            String finalMsg2 = "";
                            for (String thisKey: cardKeys2) {
                                if (j > 100) {
                                    if (hasCard(p4, thisKey, isRarity(args[1])) > 0) numCardsCounter2++;
                                    finalMsg2 = msg2 + "&7and more!";
                                    j++;
                                    continue;

                                }
                                debugMsg("[Cards] " + thisKey + ", " + isRarity(args[1]));
                                String colour2 = getConfig().getString("Colours.ListHaveCard");
                                if (hasShiny((Player) sender, thisKey, isRarity(args[1]))) {
                                    numCardsCounter2++;
                                    colour2 = getConfig().getString("Colours.ListHaveShinyCard");
                                    msg2 = msg2 + colour2 + thisKey.replaceAll("_", " ") + "&f, ";
                                } else if (hasCard((Player) sender, thisKey, isRarity(args[1])) > 0 && !hasShiny((Player) sender, thisKey, isRarity(args[1]))) {
                                    numCardsCounter2++;
                                    msg2 = msg2 + colour2 + thisKey.replaceAll("_", " ") + "&f, ";
                                }
                                j++;

                            }
                            if (numCardsCounter2 >= j) {
                                sender.sendMessage(cMsg("&6--- " + isRarity(args[1]) + " &7(" + getConfig().getString("Colours.ListRarityComplete") + "Complete&7)&6 ---"));
                            } else {
                                sender.sendMessage(cMsg("&6--- " + isRarity(args[1]) + " &7(&c" + numCardsCounter2 + "&f/&a" + j + "&7)&6 ---"));
                            }
                            msg2 = StringUtils.removeEnd(msg2, ", ");
                            if (finalMsg2.equals("")) {
                                sender.sendMessage(cMsg(msg2));
                            } else {
                                sender.sendMessage(cMsg(finalMsg2));
                            }
                            msg2 = "";
                            finalMsg2 = "";
                            j = 0;
                            numCardsCounter2 = 0;
                        }
                    } else {
                        ConfigurationSection cards2 = getCardsData().getConfigurationSection("Cards");
                        Set < String > cardKeys2 = cards2.getKeys(false);
                        String msg2 = "";
                        int j = 0;
                        int numCardsCounter2 = 0;
                        Player p4 = (Player) sender;
                        String finalMsg2 = "";
                        for (String key: cardKeys2) {
                            String thisKey = key;
                            ConfigurationSection cardsWithKey = getCardsData().getConfigurationSection("Cards." + thisKey);
                            Set < String > keyKeys = cardsWithKey.getKeys(false);
                            for (String key2: keyKeys) {
                                if (j > 32) {
                                    if (hasCard(p4, key2, key) > 0) numCardsCounter2++;
                                    finalMsg2 = msg2 + "&7and more!";
                                    j++;
                                    continue;
                                }
                                debugMsg("[Cards] " + key + ", " + key2);
                                String colour3 = getConfig().getString("Colours.ListHaveCard");
                                if (hasShiny((Player) sender, key2, key)) {
                                    numCardsCounter2++;
                                    colour3 = getConfig().getString("Colours.ListHaveShinyCard");
                                    msg2 = msg2 + colour3 + key2.replaceAll("_", " ") + "&f, ";
                                } else if (hasCard((Player) sender, key2, key) > 0 && !hasShiny((Player) sender, key2, key)) {
                                    numCardsCounter2++;
                                    msg2 = msg2 + colour3 + key2.replaceAll("_", " ") + "&f, ";
                                } else {
                                    msg2 = msg2 + "&7" + key2.replaceAll("_", " ") + "&f, ";
                                }
                                j++;
                            }
                            if (numCardsCounter2 >= j) {
                                sender.sendMessage(cMsg("&6--- " + key + " &7(" + getConfig().getString("Colours.ListRarityComplete") + "Complete&7)&6 ---"));
                            } else {
                                sender.sendMessage(cMsg("&6--- " + key + " &7(&c" + numCardsCounter2 + "&f/&a" + j + "&7)&6 ---"));
                            }
                            msg2 = StringUtils.removeEnd(msg2, ", ");
                            if (finalMsg2.equals("")) {
                                sender.sendMessage(cMsg(msg2));
                            } else {
                                sender.sendMessage(cMsg(finalMsg2));
                            }
                            msg2 = "";
                            finalMsg2 = "";
                            j = 0;
                            numCardsCounter2 = 0;
                        }

                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                }
            } else if (args[0].equalsIgnoreCase("listpacks")) {
                    if (sender.hasPermission("cards.listpacks")) {
                        ConfigurationSection cards3 = getConfig().getConfigurationSection("BoosterPacks");
                        Set < String > cardKeys3 = cards3.getKeys(false);
                        int k = 0;
                        sender.sendMessage(cMsg("&6--- Booster Packs ---"));
                        boolean hasPrice = false;
                        boolean hasExtra = false;
                        for (String key: cardKeys3) {
                            if (getConfig().getBoolean("PluginSupport.Vault.Vault-Enabled") && getConfig().contains("BoosterPacks." + key + ".Price") && getConfig().getDouble("BoosterPacks." + key + ".Price") > 0.0D) hasPrice = true;
                            if (getConfig().contains("BoosterPacks." + key + ".ExtraCardRarity") && getConfig().contains("BoosterPacks." + key + ".NumExtraCards")) hasExtra = true;
                            k++;
                            if (hasPrice) {
                                sender.sendMessage(cMsg("&6" + k + ") &e" + key + " &7(&aPrice: " + getConfig().getDouble("BoosterPacks." + key + ".Price") + "&7)"));
                            } else {
                                sender.sendMessage(cMsg("&6" + k + ") &e" + key));

                            }
                            if (hasExtra) {
                                sender.sendMessage(cMsg("  &7- &f&o" + getConfig().getInt("BoosterPacks." + key + ".NumNormalCards") + " " + getConfig().getString("BoosterPacks." + key + ".NormalCardRarity") + ", " + getConfig().getInt("BoosterPacks." + key + ".NumExtraCards") + " " + getConfig().getString("BoosterPacks." + key + ".ExtraCardRarity") + ", " + getConfig().getInt("BoosterPacks." + key + ".NumSpecialCards") + " " + getConfig().getString("BoosterPacks." + key + ".SpecialCardRarity")));
                            } else {
                                sender.sendMessage(cMsg("  &7- &f&o" + getConfig().getInt("BoosterPacks." + key + ".NumNormalCards") + " " + getConfig().getString("BoosterPacks." + key + ".NormalCardRarity") + ", " + getConfig().getInt("BoosterPacks." + key + ".NumSpecialCards") + " " + getConfig().getString("BoosterPacks." + key + ".SpecialCardRarity")));
                            }
                            hasPrice = false;
                            hasExtra = false;
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                    }
                } else if (args[0].equalsIgnoreCase("reward")) {
                    if (sender.hasPermission("cards.reward")) {
                        if (getConfig().getBoolean("General.Allow-Rewards")) {
                            if (args.length > 1) {
                                if (!isRarity(args[1]).equalsIgnoreCase("None")) {
                                    if (completedRarity((Player) sender, isRarity(args[1]))) {
                                        if (getConfig().contains("Rarities." + isRarity(args[1]) + ".RewardCmd1") && !getConfig().getString("Rarities." + isRarity(args[1]) + ".RewardCmd1").equalsIgnoreCase("None")) getServer().dispatchCommand(getServer().getConsoleSender(), getConfig().getString("Rarities." + isRarity(args[1]) + ".RewardCmd1").replaceAll("%player%", sender.getName()));
                                        if (getConfig().contains("Rarities." + isRarity(args[1]) + ".RewardCmd2") && !getConfig().getString("Rarities." + isRarity(args[1]) + ".RewardCmd2").equalsIgnoreCase("None")) getServer().dispatchCommand(getServer().getConsoleSender(), getConfig().getString("Rarities." + isRarity(args[1]) + ".RewardCmd2").replaceAll("%player%", sender.getName()));
                                        if (getConfig().contains("Rarities." + isRarity(args[1]) + ".RewardCmd3") && !getConfig().getString("Rarities." + isRarity(args[1]) + ".RewardCmd3").equalsIgnoreCase("None")) getServer().dispatchCommand(getServer().getConsoleSender(), getConfig().getString("Rarities." + isRarity(args[1]) + ".RewardCmd3").replaceAll("%player%", sender.getName()));
                                        if (getConfig().getBoolean("General.Reward-Broadcast")) Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.RewardBroadcast").replaceAll("%player%", sender.getName()).replaceAll("%rarity%", isRarity(args[1]))));
                                        if (!deleteRarity((Player) sender, isRarity(args[1])) && getConfig().getBoolean("General.Debug-Mode")) System.out.println("Cannot delete rarity: " + isRarity(args[1]));
                                    } else if (getConfig().getBoolean("General.Eat-Shiny-Cards")) {
                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.RewardError2")));
                                    } else {
                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.RewardError3").replaceAll("%shinyName%", getConfig().getString("General.Shiny-Name"))));
                                    }
                                } else {
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.RewardError")));
                                }
                            } else {
                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.RewardUsage")));
                            }
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.RewardDisabled")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                    }
                } else if (args[0].equalsIgnoreCase("giveaway")) {
                    if (sender.hasPermission("cards.giveaway")) {
                        if (args.length > 1) {
                            ConfigurationSection rarities = getCardsData().getConfigurationSection("Cards");
                            Set < String > rarityKeys = rarities.getKeys(false);
                            String keyToUse = "";
                            if (isMob(args[1])) {
                                if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
                                    giveawayNatural(EntityType.valueOf(args[1].toUpperCase()), null);
                                } else {
                                    giveawayNatural(EntityType.valueOf(args[1].toUpperCase()), (Player) sender);
                                }
                            } else {
                                for (String thisKey4: rarityKeys) {
                                    if (thisKey4.equalsIgnoreCase(args[1].replaceAll("_", " "))) keyToUse = thisKey4;
                                }
                                if (!keyToUse.equals("")) {
                                    Bukkit.broadcastMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.Giveaway").replaceAll("%player%", sender.getName()).replaceAll("%rarity%", keyToUse)));
                                    for (Player p5: Bukkit.getOnlinePlayers()) {
                                        ConfigurationSection cards4 = getCardsData().getConfigurationSection("Cards." + keyToUse);
                                        Set < String > cardKeys4 = cards4.getKeys(false);
                                        int rIndex = this.r.nextInt(cardKeys4.size());
                                        int l = 0;
                                        String cardName = "";
                                        for (String theCardName: cardKeys4) {
                                            if (l == rIndex) {
                                                cardName = theCardName;
                                                break;

                                            }
                                            l++;

                                        }
                                        if (p5.getInventory().firstEmpty() != -1) {
                                            p5.getInventory().addItem(createPlayerCard(cardName, keyToUse, Integer.valueOf(1), false));
                                            continue;
                                        }
                                        World curWorld3 = p5.getWorld();
                                        if (p5.getGameMode() != GameMode.SURVIVAL) continue;
                                        curWorld3.dropItem(p5.getLocation(), createPlayerCard(cardName, keyToUse, Integer.valueOf(1), false));
                                    }
                                } else {
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoRarity")));
                                }

                            }
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.GiveawayUsage")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                    }
                } else if (args[0].equalsIgnoreCase("worth")) {
                    if (sender.hasPermission("cards.worth")) {
                        if (this.hasVault) {
                            Player p = (Player) sender;
                            if (p.getInventory().getItemInMainHand().getType() == Material.valueOf(getConfig().getString("General.Card-Material"))) {
                                ItemStack itemInHand = p.getInventory().getItemInMainHand();
                                String itemName = itemInHand.getItemMeta().getDisplayName();
                                debugMsg(itemName);
                                debugMsg(ChatColor.stripColor(itemName));
                                String[] splitName = ChatColor.stripColor(itemName).split(" ");
                                String cardName2 = "";
                                if (splitName.length > 1) {
                                    cardName2 = splitName[1];

                                } else {
                                    cardName2 = splitName[0];
                                }
                                debugMsg(cardName2);
                                List < String > lore = itemInHand.getItemMeta().getLore();
                                String rarity = ChatColor.stripColor(lore.get(3));
                                debugMsg(rarity);
                                boolean canBuy = false;
                                double buyPrice = 0.0D;
                                if (getCardsData().contains("Cards." + rarity + "." + cardName2 + ".Buy-Price")) {
                                    buyPrice = getCardsData().getDouble("Cards." + rarity + "." + cardName2 + ".Buy-Price");
                                    if (buyPrice > 0.0D) canBuy = true;
                                }
                                if (canBuy) {
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CanBuy").replaceAll("%buyAmount%", String.valueOf(buyPrice))));
                                } else if (!canBuy) {
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CanNotBuy")));
                                }
                            } else {
                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NotACard")));
                            }
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoVault")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                    }
                } else if (args[0].equalsIgnoreCase("buy")) {
                    if (sender.hasPermission("cards.buy")) {
                        if (this.hasVault) {
                            Player p = (Player) sender;
                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("pack")) {
                                    if (args.length > 2) {
                                        if (getConfig().contains("BoosterPacks." + args[2])) {
                                            double buyPrice2 = 0.0D;
                                            boolean canBuy2 = false;
                                            if (getConfig().contains("BoosterPacks." + args[2] + ".Price")) {
                                                buyPrice2 = getConfig().getDouble("BoosterPacks." + args[2] + ".Price");
                                                if (buyPrice2 > 0.0D) canBuy2 = true;
                                            }
                                            if (canBuy2) {
                                                if (econ.getBalance(p) >= buyPrice2) {
                                                    if (getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {
                                                        econ.withdrawPlayer(p, buyPrice2);
                                                        econ.depositPlayer(p, getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
                                                    } else {
                                                        econ.withdrawPlayer(p, buyPrice2);
                                                    }
                                                    if (p.getInventory().firstEmpty() != -1) {
                                                        p.getInventory().addItem(createBoosterPack(args[2]));

                                                    } else {
                                                        World curWorld4 = p.getWorld();
                                                        if (p.getGameMode() == GameMode.SURVIVAL) curWorld4.dropItem(p.getLocation(), createBoosterPack(args[2]));
                                                    }
                                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.BoughtCard").replaceAll("%amount%", String.valueOf(buyPrice2))));
                                                } else {
                                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NotEnoughMoney")));
                                                }
                                            } else {
                                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CannotBeBought")));
                                            }
                                        } else {
                                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.PackDoesntExist")));
                                        }
                                    } else {
                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ChoosePack")));
                                    }
                                } else if (args[1].equalsIgnoreCase("card")) {
                                    if (args.length > 2) {
                                        if (args.length > 3) {
                                            if (getCardsData().contains("Cards." + args[2] + "." + args[3])) {
                                                double buyPrice2 = 0.0D;
                                                boolean canBuy2 = false;
                                                if (getCardsData().contains("Cards." + args[2] + "." + args[3] + ".Buy-Price")) {
                                                    buyPrice2 = getCardsData().getDouble("Cards." + args[2] + "." + args[3] + ".Buy-Price");
                                                    if (buyPrice2 > 0.0D) canBuy2 = true;
                                                }
                                                if (canBuy2) {
                                                    if (econ.getBalance(p) >= buyPrice2) {
                                                        if (getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {
                                                            econ.withdrawPlayer(p, buyPrice2);
                                                            econ.depositPlayer(p, getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
                                                        } else {
                                                            econ.withdrawPlayer(p, buyPrice2);
                                                        }
                                                        if (p.getInventory().firstEmpty() != -1) {
                                                            p.getInventory().addItem(createPlayerCard(args[3], args[2], Integer.valueOf(1), false));

                                                        } else {
                                                            World curWorld4 = p.getWorld();
                                                            if (p.getGameMode() == GameMode.SURVIVAL) curWorld4.dropItem(p.getLocation(), createPlayerCard(args[3], args[2], Integer.valueOf(1), false));
                                                        }
                                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.BoughtCard").replaceAll("%amount%", String.valueOf(buyPrice2))));
                                                    } else {
                                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NotEnoughMoney")));
                                                    }
                                                } else {
                                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CannotBeBought")));
                                                }
                                            } else {
                                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.CardDoesntExist")));
                                            }
                                        } else {
                                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ChooseCard")));
                                        }
                                    } else {
                                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.ChooseRarity")));
                                    }
                                } else {
                                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.BuyUsage")));
                                }
                            } else {
                                sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.BuyUsage")));
                            }
                        } else {
                            sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoVault")));
                        }
                    } else {
                        sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoPerms")));
                    }
                } else {
                    sender.sendMessage(cMsg(getMessagesData().getString("Messages.Prefix") + " " + getMessagesData().getString("Messages.NoCmd")));
                }
            }
        return true;
    }

    public boolean completedRarity(Player p, String rarity) {
        if (!isRarity(rarity).equals("None")) {
            ConfigurationSection cards = getCardsData().getConfigurationSection("Cards." + isRarity(rarity));
            Set < String > cardKeys = cards.getKeys(false);
            int i = 0;

            int numCardsCounter = 0;
            for (String key: cardKeys) {
                debugMsg("[Cards] Iteration:: " + i);
                debugMsg("[Cards] Key: " + key);
                debugMsg("[Cards] Counter: " + numCardsCounter);
                boolean shinyver = false;
                boolean regularver = false;
                if (hasShiny(p, key, isRarity(rarity))) shinyver = true;
                if (hasCard(p, key, isRarity(rarity)) > 0) regularver = true;
                if (shinyver && regularver) {
                    numCardsCounter++;
                } else if (!shinyver && regularver) {
                    numCardsCounter++;
                }
                i++;
            }
            if (numCardsCounter >= i) {
                debugMsg("[Cards] True!");
                return true;
            }
            if (!getConfig().getBoolean("General.Debug-Mode")) return false;
            System.out.println("[Cards] False!");

        }
        return false;
    }

    public boolean deleteRarity(Player p, String rarity) {
        if (!isRarity(rarity).equals("None")) {
            ConfigurationSection cards = getCardsData().getConfigurationSection("Cards." + isRarity(rarity));
            Set < String > cardKeys = cards.getKeys(false);

            int numCardsCounter = 0;
            for (String key: cardKeys) {
                debugMsg("deleteRarity iteration: " + numCardsCounter);
                if (hasShiny(p, key, rarity) && hasCard(p, key, rarity) == 0) {
                    debugMsg("Deleted: Cards." + key + ".key2");
                    deleteCard(p, key, rarity);
                    numCardsCounter++;
                }
                if (hasCard(p, key, rarity) > 0) {
                    debugMsg("Deleted: Cards." + key + ".key2");
                    deleteCard(p, key, rarity);
                    numCardsCounter++;
                }
            }
        }
        return true;
    }

    public String cMsg(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void startTimer() {
        int hours = 1;
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        if (scheduler.isQueued(this.taskid) || scheduler.isCurrentlyRunning(this.taskid)) {
            scheduler.cancelTask(this.taskid);
            debugMsg("[Cards] Successfully cancelled task " + this.taskid);
        }
        if (getConfig().getInt("General.Schedule-Card-Time-In-Hours") < 1) {
            hours = 1;
        } else {
            hours = getConfig().getInt("General.Schedule-Card-Time-In-Hours");
        }
        String tmessage = getMessagesData().getString("Messages.TimerMessage").replaceAll("%hour%", String.valueOf(hours));
        Bukkit.broadcastMessage(cMsg(String.valueOf(getMessagesData().getString("Messages.Prefix")) + " " + tmessage));
        this.taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable() {
                    public void run() {
                        debugMsg("[Cards] Task running..");
                        if (getConfig().getBoolean("General.Schedule-Cards")) if (getConfig().getBoolean("General.Schedule-Cards-Natural")) {
                            String mob = getConfig().getString("General.Schedule-Card-Mob");
                            if (isMob(mob.toUpperCase())) {
                                giveawayNatural(EntityType.valueOf(mob.toUpperCase()), (Player) null);
                            } else {
                                System.out.println("[Cards] Error! schedule-card-mob is an invalid mob?");
                            }
                        } else {
                            debugMsg("[Cards] Schedule cards is true.");
                            ConfigurationSection rarities = getCardsData().getConfigurationSection("Cards");
                            Set < String > rarityKeys = rarities.getKeys(false);
                            String keyToUse = "";
                            for (String key: rarityKeys) {
                                debugMsg("[Cards] Rarity key: " + key);
                                if (key.equalsIgnoreCase(getConfig().getString("General.Schedule-Card-Rarity"))) keyToUse = key;
                            }
                            debugMsg("[Cards] keyToUse: " + keyToUse);
                            if (!keyToUse.equals("")) {
                                Bukkit.broadcastMessage(cMsg(String.valueOf(getMessagesData().getString("Messages.Prefix")) + " " + getMessagesData().getString("Messages.ScheduledGiveaway")));
                                for (Player p: Bukkit.getOnlinePlayers()) {
                                    ConfigurationSection cards = getCardsData().getConfigurationSection("Cards." + keyToUse);
                                    Set < String > cardKeys = cards.getKeys(false);
                                    int rIndex = r.nextInt(cardKeys.size());
                                    int i = 0;
                                    String cardName = "";
                                    for (String theCardName: cardKeys) {
                                        if (i == rIndex) {
                                            cardName = theCardName;
                                            break;
                                        }
                                        i++;
                                    }
                                    if (p.getInventory().firstEmpty() != -1) {
                                        p.getInventory().addItem(new ItemStack[] {
                                                createPlayerCard(cardName, keyToUse, Integer.valueOf(1), false)
                                        });
                                        continue;
                                    }
                                    World curWorld = p.getWorld();
                                    if (p.getGameMode() != GameMode.SURVIVAL) continue;
                                    curWorld.dropItem(p.getLocation(), createPlayerCard(cardName, keyToUse, Integer.valueOf(1), false));
                                }
                            }
                        }
                    }
                },
                (hours * 20 * 60 * 60), (hours * 20 * 60 * 60));
    }

    public boolean isPlayerCard(String name) {
        String rarity = getConfig().getString("General.Auto-Add-Player-Rarity");
        String type = getConfig().getString("General.Player-Type");
        return getCardsData().contains("Cards." + rarity + "." + name) && getCardsData().getString("Cards." + rarity + "." + name + ".Type").equalsIgnoreCase(type);
    }

    public String formatTitle(String title) {
        String line = "&7[&foOo&7]&f____________________________________________________&7[&foOo&7]&f";
        int pivot = "&7[&foOo&7]&f____________________________________________________&7[&foOo&7]&f".length() / 2;
        String center = "&7.< &3" + title + "&7 >.&f";
        String out = "&7[&foOo&7]&f____________________________________________________&7[&foOo&7]&f".substring(0, Math.max(0, pivot - center.length() / 2));
        out = out + center + "&7[&foOo&7]&f____________________________________________________&7[&foOo&7]&f".substring(pivot + center.length() / 2);
        return out;
    }
}