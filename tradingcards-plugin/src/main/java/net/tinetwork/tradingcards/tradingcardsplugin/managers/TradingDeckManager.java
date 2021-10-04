package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.model.deck.DeckEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.DeckConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
    private final DeckConfig deckConfig;
    private final Map<UUID,Integer> playerDeckViewingMap;

    public TradingDeckManager(final TradingCards plugin) {
        this.plugin = plugin;
        this.cardManager = plugin.getCardManager();
        this.deckConfig = plugin.getDeckConfig();
        this.playerDeckViewingMap = new HashMap<>();
    }

    public void openDeck(Player player, int deckNum) {
        String uuidString = player.getUniqueId().toString();
        openDeckViewer(player.getUniqueId(),deckNum);
        plugin.debug(TradingDeckManager.class,"Deck UUID: " + uuidString);
        player.openInventory(generateDeckInventory(player, deckNum));
    }

    public void openDeckViewer(UUID uuid, int num) {
        plugin.debug(getClass(),"Added uuid "+uuid+" deck #"+num+" to deck viewer map.");
        this.playerDeckViewingMap.put(uuid,num);
    }


    public void closeDeckViewer(UUID uuid) {
        plugin.debug(getClass(),"Removed uuid "+uuid+" from deck viewer map.");
        this.playerDeckViewingMap.remove(uuid);
    }

    public int getViewerDeckNum(UUID uuid) {
        return this.playerDeckViewingMap.get(uuid);
    }

    public boolean containsViewer(UUID uuid) {
        return this.playerDeckViewingMap.containsKey(uuid);
    }

    private Inventory generateDeckInventory(final Player player, final int deckNum) {
        List<ItemStack> cards = loadCardsFromFile(player.getUniqueId(), deckNum);
        Inventory inv = Bukkit.createInventory(null, getDeckSize(), ChatUtil.color(plugin.getMessagesConfig().deckInventoryTitle().replace("%player%", player.getName()).replace("%deck_num%", String.valueOf(deckNum))));
        for (ItemStack cardItem : cards) {
            inv.addItem(cardItem);
            plugin.debug(TradingDeckManager.class,"Item=" + cardItem.getType() + ",amount=" + cardItem.getAmount() + ", added to inventory");
        }
        return inv;
    }

    private List<ItemStack> loadCardsFromFile(final UUID uuid, final int deckNum) {
        final List<ItemStack> cards = new ArrayList<>();

        List<DeckEntry> deckEntries = DeckConfig.convertToDeckEntries(deckConfig.getDeckEntries(uuid, String.valueOf(deckNum)));
        for (DeckEntry deckEntry : deckEntries) {
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
        nbtItem.setBoolean("isDeck", true);
        nbtItem.setInteger("deckNumber", num);
        return nbtItem.getItem();
    }

    public boolean isDeckMaterial(final Material material) {
        return material == plugin.getGeneralConfig().deckMaterial();
    }

    @Override
    public boolean isDeck(final ItemStack item) {
        return isDeckMaterial(item.getType()) && hasEnchantments(item) && new NBTItem(item).getBoolean("isDeck");
    }

    public int getDeckNumber(final ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("deckNumber"))
            return nbtItem.getInteger("deckNumber");

        String[] nameSplit = item.getItemMeta().getDisplayName().split("#");
        return Integer.parseInt(nameSplit[1]);
    }

    private static boolean hasEnchantments(final ItemStack item) {
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
    public boolean hasCard(Player player, String card, String rarity) {
        return plugin.getDeckConfig().containsCard(player.getUniqueId(), card, rarity);
    }

    @Override
    public boolean hasShiny(Player player, String card, String rarity) {
        return plugin.getDeckConfig().containsShinyCard(player.getUniqueId(), card, rarity);
    }
}
