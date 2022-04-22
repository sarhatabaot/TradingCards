package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.events.DeckCloseEvent;
import net.tinetwork.tradingcards.api.events.DeckOpenEvent;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.api.events.DeckLoadEvent;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.InternalLog;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TradingDeckManager implements DeckManager {
    private final TradingCards plugin;
    private final TradingCardManager cardManager;
    private final Storage<TradingCard> storage;
    private final Map<UUID, Integer> playerDeckViewingMap;

    public TradingDeckManager(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
        this.cardManager = plugin.getCardManager();
        this.storage = plugin.getStorage();
        this.playerDeckViewingMap = new HashMap<>();
        this.plugin.getLogger().info(() -> InternalLog.Init.LOAD_DECK_MANAGER);
    }


    public void openDeck(@NotNull Player player, int deckNum) {
        //Checks, if in SQL mode a migration has been done.
        if (plugin.getStorage().getType() != StorageType.YAML
                && (plugin.getStorage().getCardsCount() == 0 || plugin.getMigrateCommand().isRanDataMigration())) {
            ChatUtil.sendPrefixedMessage(player, "Stopped opening of deck due to un-migrated data.");
            ChatUtil.sendPrefixedMessage(player, "Make sure to migrate your data with /cards migrate data.");
            ChatUtil.sendPrefixedMessage(player,"If you already ran /cards migrate data, make sure to restart your server.");
            return;
        }
        plugin.debug(TradingDeckManager.class, InternalDebug.DecksManager.HAS_MIGRATION.formatted(plugin.getMigrateCommand().isRanDataMigration()));
        plugin.debug(TradingDeckManager.class, InternalDebug.DecksManager.PLAYER_UUID.formatted(player.getUniqueId()));

        addDeckViewer(player.getUniqueId(), deckNum);

        Inventory deckInventory = generateDeckInventory(player, deckNum);
        DeckLoadEvent deckLoadEvent = new DeckLoadEvent(deckInventory, deckNum);
        Bukkit.getPluginManager().callEvent(deckLoadEvent);

        final InventoryView deckView = player.openInventory(deckLoadEvent.getInventory());
        Bukkit.getPluginManager().callEvent(new DeckOpenEvent(deckView, deckNum));
    }

    public void closeAllOpenViews() {
        for (Map.Entry<UUID, Integer> entry : this.playerDeckViewingMap.entrySet()) {
            Bukkit.getPluginManager().callEvent(new DeckCloseEvent(Bukkit.getPlayer(entry.getKey()).getOpenInventory(), entry.getValue()));
        }
    }

    public void addDeckViewer(UUID uuid, int num) {
        plugin.debug(getClass(), InternalDebug.DecksManager.ADDED_DECK_UUID_NUMBER.formatted(uuid,num));
        this.playerDeckViewingMap.put(uuid, num);
    }


    public void removeDeckViewer(UUID uuid) {
        plugin.debug(getClass(), InternalDebug.DecksManager.REMOVED_DECK_UUID.formatted(uuid));
        this.playerDeckViewingMap.remove(uuid);
    }

    public int getViewerDeckNum(UUID uuid) {
        return this.playerDeckViewingMap.get(uuid);
    }

    public boolean containsViewer(UUID uuid) {
        return this.playerDeckViewingMap.containsKey(uuid);
    }

    private @NotNull Inventory generateDeckInventory(final @NotNull Player player, final int deckNum) {
        List<ItemStack> cards = loadCardsFromStorage(player.getUniqueId(), deckNum);
        Inventory inv = Bukkit.createInventory(player.getPlayer(), getDeckSize(), ChatUtil.color(plugin.getMessagesConfig().deckInventoryTitle().replace("%player%", player.getName()).replace("%deck_num%", String.valueOf(deckNum))));
        for (ItemStack cardItem : cards) {
            NBTItem nbtItem = new NBTItem(cardItem);
            inv.addItem(cardItem);
            plugin.debug(TradingDeckManager.class, "Item=" + cardItem.getType() + ",amount=" + cardItem.getAmount() + ", added to inventory. NBT=" + nbtItem);
        }
        return inv;
    }

    private @NotNull List<ItemStack> loadCardsFromStorage(final UUID uuid, final int deckNum) {
        final List<ItemStack> cards = new ArrayList<>();
        plugin.debug(TradingDeckManager.class, "uuid=" + uuid + ",deckNum=" + deckNum);
        final Deck deck = storage.getDeck(uuid, deckNum);

        List<StorageEntry> deckEntries = deck.getDeckEntries();
        if (deckEntries == null)
            return cards;

        for (StorageEntry deckEntry : deckEntries) {
            plugin.debug(getClass(), deckEntry.toString());

            final String cardId = deckEntry.getCardId();
            final String rarityId = deckEntry.getRarityId();
            final String seriesId = deckEntry.getSeriesId();

            TradingCard card = cardManager.getCard(cardId, rarityId, seriesId,false);
            if (card instanceof EmptyCard) {
                plugin.debug(getClass(), "Card is not in a cards storage, skipping.");
                continue;
            }

            ItemStack cardItem = card.build(deckEntry.isShiny());
            cardItem.setAmount(deckEntry.getAmount());
            cards.add(cardItem);
        }
        return cards;
    }

    private int getDeckSize() {
        int deckRows = plugin.getGeneralConfig().deckRows();
        if (deckRows > 6 || deckRows < 1) {
            deckRows = 3;
        }
        return deckRows * 9;
    }

    @NotNull
    @Override
    public ItemStack createDeckItem(@NotNull final Player player, final int deckNumber) {
        ItemStack deck = plugin.getGeneralConfig().blankDeck();
        ItemMeta deckMeta = deck.getItemMeta();
        //probably best to have this set somewhere
        deckMeta.setDisplayName(ChatUtil.color(plugin.getGeneralConfig().deckPrefix() + player.getName() + "'s Deck #" + deckNumber));
        deckMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        deck.setItemMeta(deckMeta);
        deck.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        return deck;
    }

    @NotNull
    @Override
    public ItemStack getNbtItem(@NotNull final Player player, final int deckNumber) {
        NBTItem nbtItem = new NBTItem(createDeckItem(player, deckNumber));
        nbtItem.setBoolean(NbtUtils.NBT_IS_DECK, true);
        nbtItem.setInteger(NbtUtils.NBT_DECK_NUMBER, deckNumber);
        return nbtItem.getItem();
    }

    public boolean isDeckMaterial(final Material material) {
        return material == plugin.getGeneralConfig().deckMaterial();
    }

    @Override
    public boolean isDeck(final @NotNull ItemStack item) {
        if (item.getType() == Material.AIR)
            return false;
        return new NBTItem(item).getBoolean(NbtUtils.NBT_IS_DECK);
    }

    public int getDeckNumber(final ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey(NbtUtils.NBT_DECK_NUMBER))
            return nbtItem.getInteger(NbtUtils.NBT_DECK_NUMBER);

        String[] nameSplit = item.getItemMeta().getDisplayName().split("#");
        return Integer.parseInt(nameSplit[1]);
    }

    @Override
    public boolean hasDeckItem(@NotNull final Player player, final int num) {
        final List<ItemStack> inventoryContents = Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .toList();

        for (final ItemStack itemStack : inventoryContents) {
            if (isDeck(itemStack) && num == getDeckNumber(itemStack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasCard(@NotNull Player player, String cardId, String rarityId) {
        return storage.hasCard(player.getUniqueId(), cardId, rarityId);
    }

    @Override
    public boolean hasShinyCard(@NotNull Player player, String cardId, String rarityId) {
        return storage.hasShinyCard(player.getUniqueId(), cardId, rarityId);
    }

    public void createNewDeckInFile(final UUID uuid, final int deckNumber) {
        storage.saveDeck(uuid, deckNumber, new Deck(uuid, deckNumber, new ArrayList<>()));
    }
}
