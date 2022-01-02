package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.events.DeckOpenEvent;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.api.events.DeckLoadEvent;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TradingDeckManager implements DeckManager {
    private final TradingCards plugin;
    private final TradingCardManager cardManager;
    private final Storage deckStorage;
    //private final DeckConfig deckConfig;
    private final Map<UUID,Integer> playerDeckViewingMap;

    public TradingDeckManager(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
        this.cardManager = plugin.getCardManager();
        this.deckStorage =  plugin.getDeckStorage();
        this.playerDeckViewingMap = new HashMap<>();
    }


    public void openDeck(@NotNull Player player, int deckNum) {
        plugin.debug(TradingDeckManager.class,"Deck UUID: " + player.getUniqueId());

        addDeckViewer(player.getUniqueId(),deckNum);

        Inventory deckInventory = generateDeckInventory(player,deckNum);
        DeckLoadEvent deckLoadEvent = new DeckLoadEvent(deckInventory,deckNum);
        Bukkit.getPluginManager().callEvent(deckLoadEvent);

        final InventoryView deckView = player.openInventory(deckLoadEvent.getInventory());
        Bukkit.getPluginManager().callEvent(new DeckOpenEvent(deckView,deckNum));
    }

    public void addDeckViewer(UUID uuid, int num) {
        plugin.debug(getClass(),"Added uuid "+uuid+" deck #"+num+" to deck viewer map.");
        this.playerDeckViewingMap.put(uuid,num);
    }


    public void removeDeckViewer(UUID uuid) {
        plugin.debug(getClass(),"Removed uuid "+uuid+" from deck viewer map.");
        this.playerDeckViewingMap.remove(uuid);
    }

    public int getViewerDeckNum(UUID uuid) {
        return this.playerDeckViewingMap.get(uuid);
    }

    public boolean containsViewer(UUID uuid) {
        return this.playerDeckViewingMap.containsKey(uuid);
    }

    private @NotNull Inventory generateDeckInventory(final @NotNull Player player, final int deckNum) {
        List<ItemStack> cards = loadCardsFromFile(player.getUniqueId(), deckNum);
        Inventory inv = Bukkit.createInventory(player.getPlayer(), getDeckSize(), ChatUtil.color(plugin.getMessagesConfig().deckInventoryTitle().replace("%player%", player.getName()).replace("%deck_num%", String.valueOf(deckNum))));
        for (ItemStack cardItem : cards) {
            inv.addItem(cardItem);
            plugin.debug(TradingDeckManager.class,"Item=" + cardItem.getType() + ",amount=" + cardItem.getAmount() + ", added to inventory");
        }
        return inv;
    }

    private @NotNull List<ItemStack> loadCardsFromFile(final UUID uuid, final int deckNum) {
        final List<ItemStack> cards = new ArrayList<>();

        //List<StorageEntry> deckEntries = DeckConfig.convertToDeckEntries(deckConfig.getDeckEntries(uuid, String.valueOf(deckNum)));
        List<StorageEntry> deckEntries = deckStorage.getDeck(uuid,deckNum).getDeckEntries();
        for (StorageEntry deckEntry : deckEntries) {
            plugin.debug(getClass(),deckEntry.toString());
            TradingCard card = cardManager.getCard(deckEntry.getCardId(),
                    deckEntry.getRarityId(),
                    deckEntry.isShiny());
            if(card instanceof EmptyCard) {
                plugin.debug(getClass(),"Card is not in a cards file, skipping.");
                continue;
            }
            ItemStack cardItem = card.build();
            cardItem.setAmount(deckEntry.getAmount());
            cards.add(cardItem);
        }
        return cards;
    }

    private int getDeckSize() {
        if (plugin.getGeneralConfig().useLargeDecks())
            return 54;
        return 27;
    }

    @NotNull
    @Override
    public ItemStack createDeckItem(@NotNull final Player p, final int num) {
        ItemStack deck = plugin.getGeneralConfig().blankDeck();
        ItemMeta deckMeta = deck.getItemMeta();
        //probably best to have this set somewhere
        deckMeta.setDisplayName(ChatUtil.color(plugin.getGeneralConfig().deckPrefix() + p.getName() + "'s Deck #" + num));
        deckMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        deck.setItemMeta(deckMeta);
        deck.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        return deck;
    }

    @NotNull
    @Override
    public ItemStack createDeck(@NotNull final Player player, final int num) {
        NBTItem nbtItem = new NBTItem(createDeckItem(player, num));
        nbtItem.setBoolean(NbtUtils.NBT_IS_DECK, true);
        nbtItem.setInteger(NbtUtils.NBT_DECK_NUMBER, num);
        return nbtItem.getItem();
    }

    public boolean isDeckMaterial(final Material material) {
        return material == plugin.getGeneralConfig().deckMaterial();
    }

    @Override
    public boolean isDeck(final @NotNull ItemStack item) {
        return isDeckMaterial(item.getType()) && hasEnchantments(item) && new NBTItem(item).getBoolean(NbtUtils.NBT_IS_DECK);
    }

    public int getDeckNumber(final ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey(NbtUtils.NBT_DECK_NUMBER))
            return nbtItem.getInteger(NbtUtils.NBT_DECK_NUMBER);

        String[] nameSplit = item.getItemMeta().getDisplayName().split("#");
        return Integer.parseInt(nameSplit[1]);
    }

    private static boolean hasEnchantments(final @NotNull ItemStack item) {
        return item.containsEnchantment(Enchantment.DURABILITY) && item.getEnchantmentLevel(Enchantment.DURABILITY) == 10;
    }

    @Override
    public boolean hasDeck(@NotNull final Player player, final int num) {
        for (final ItemStack itemStack : player.getInventory()) {
            if (isDeck(itemStack) && num == getDeckNumber(itemStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasCard(@NotNull Player player, String card, String rarity) {
        return plugin.getDeckStorage().hasCard(player.getUniqueId(), card, rarity);
    }

    @Override
    public boolean hasShiny(@NotNull Player player, String card, String rarity) {
        return plugin.getDeckStorage().hasShinyCard(player.getUniqueId(), card, rarity);
    }
}
