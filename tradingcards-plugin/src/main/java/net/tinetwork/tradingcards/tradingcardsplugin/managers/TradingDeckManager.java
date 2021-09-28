package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.model.deck.DeckEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
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
import java.util.List;
import java.util.UUID;

public class TradingDeckManager implements DeckManager {
    private final TradingCards plugin;
    private final TradingCardManager cardManager;
    private final DeckConfig deckConfig;

    public TradingDeckManager(final TradingCards plugin) {
        this.plugin = plugin;
        this.cardManager = plugin.getCardManager();
        this.deckConfig = plugin.getDeckConfig();
    }

    public void openDeck(Player p, int deckNum) {
        String uuidString = p.getUniqueId().toString();
        plugin.debug("Deck UUID: " + uuidString);
        p.openInventory(generateDeckInventory(p, deckNum));
    }

    private Inventory generateDeckInventory(final Player player, final int deckNum) {
        List<ItemStack> cards = loadCardsFromFile(player.getUniqueId(), deckNum);
        Inventory inv = Bukkit.createInventory(null, getDeckSize(), ChatUtil.color(plugin.getMessagesConfig().deckInventoryTitle().replace("%player%", player.getName()).replace("%deck_num%", String.valueOf(deckNum))));
        for (ItemStack cardItem : cards) {
            inv.addItem(cardItem);
            plugin.debug("Item=" + cardItem.getType() + ",amount=" + cardItem.getAmount() + ", added to inventory");
        }
        return inv;
    }

    private List<ItemStack> loadCardsFromFile(final UUID uuid, final int deckNum) {
        final List<ItemStack> cards = new ArrayList<>();

        List<DeckEntry> deckEntries = DeckConfig.convertToDeckEntries(deckConfig.getDeckEntries(uuid, String.valueOf(deckNum)));
        for (DeckEntry deckEntry : deckEntries) {
            plugin.debug(deckEntry.toString());
            ItemStack cardItem = cardManager.getCard(deckEntry.getCardId(),
                    deckEntry.getRarityId(),
                    deckEntry.isShiny()).build();
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
    public boolean hasDeck(@NotNull final Player p, final int num) {
        for (final ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && isDeck(itemStack)) {
                String name = itemStack.getItemMeta().getDisplayName();
                String[] splitName = name.split("#");
                if (num == Integer.parseInt(splitName[1])) {
                    return true;
                }
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
