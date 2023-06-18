package net.tinetwork.tradingcards.tradingcardsplugin.utils;


import com.github.sarhatabaot.tradingcards.commands.BuyCommand;
import com.github.sarhatabaot.tradingcards.extensions.ItemExtensionsKt;
import com.github.sarhatabaot.tradingcards.utils.PlaceholderUtil;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.impl.DropTypeManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.impl.TradingRarityManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author sarhatabaot
 */
public class CardUtil {
    private static TradingCards plugin;
    private static AllCardManager cardManager;
    public static final int RANDOM_MAX = 100000;
    public static ItemStack BLANK_CARD;
    
    private static final DropType EMPTY_TYPE = new DropType("tc-internal-empty", "", "empty");
    
    private CardUtil() {
        throw new UnsupportedOperationException();
    }
    
    public static void init(final @NotNull TradingCards plugin) {
        CardUtil.plugin = plugin;
        CardUtil.cardManager = plugin.getCardManager();
        CardUtil.BLANK_CARD = plugin.getGeneralConfig().blankCard();
    }
    
    /**
     * Drops an item at the player's location.
     *
     * @param player Player
     * @param item   Item
     */
    public static void dropItem(final @NotNull Player player, final ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        final String debugItem = nbtItem.toString();
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            plugin.debug(CardUtil.class, "Added item " + debugItem + " to " + player.getName());
        } else {
            World playerWorld = player.getWorld();
            if (player.getGameMode() == GameMode.SURVIVAL) {
                playerWorld.dropItem(player.getLocation(), item);
                plugin.debug(CardUtil.class, "Dropped item " + debugItem + " @ " + player.getLocation());
            }
        }
    }
    
    @NotNull
    public static DropType getMobType(EntityType e) {
        if (MobGroupUtil.isMobHostile(e)) {
            return DropTypeManager.HOSTILE;
        }
        if (MobGroupUtil.isMobNeutral(e)) {
            return DropTypeManager.NEUTRAL;
        }
        if (MobGroupUtil.isMobPassive(e)) {
            return DropTypeManager.PASSIVE;
        }
        if (MobGroupUtil.isMobBoss(e))
            return DropTypeManager.BOSS;
        
        return EMPTY_TYPE;
    }

    @Deprecated
    public static boolean isCard(final @NotNull NBTItem nbtItem) {
        return NbtUtils.Card.isCard(nbtItem);
    }
    
    private static void broadcastPrefixedMessage(final String message) {
        Bukkit.broadcastMessage(plugin.getPrefixedMessage(message));
    }
    
    public static void giveawayNatural(EntityType mob, Player sender) {
        broadcastPrefixedMessage(getGiveawayMessage(mob, sender));
        for (final Player p : Bukkit.getOnlinePlayers()) {
            String rare = cardManager.getRandomRarityId(CardUtil.getMobType(mob));
            if (rare.equalsIgnoreCase(TradingRarityManager.EMPTY_RARITY.getId()))
                continue;
            CardUtil.dropItem(p, cardManager.getRandomCardByRarity(rare).build(false));
        }
    }
    
    //todo actual value of alwaysDrop is always "false"
    public static boolean shouldDrop(DropType dropType, boolean alwaysDrop) {
        int randomDropChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        int mobDropChance = getGeneralMobChance(dropType);
        plugin.debug(CardUtil.class, InternalDebug.CardsManager.DROP_CHANCE.formatted(randomDropChance, alwaysDrop, dropType, mobDropChance));
        if (!alwaysDrop && randomDropChance > mobDropChance) {
            plugin.debug(CardUtil.class, "Not dropping, because generated chance is larger than required: (%d > %d)".formatted(randomDropChance, mobDropChance));
            return false;
        }
        return true;
    }
    
    private static int getGeneralMobChance(@NotNull DropType dropType) {
        return switch (dropType.getType()) {
            case "boss" -> plugin.getChancesConfig().bossChance();
            case "hostile" -> plugin.getChancesConfig().hostileChance();
            case "neutral" -> plugin.getChancesConfig().neutralChance();
            case "passive" -> plugin.getChancesConfig().passiveChance();
            case "all" -> plugin.getChancesConfig().allChance();
            default -> 0;
        };
    }
    
    private static String getGiveawayMessage(EntityType mob, Player sender) {
        if (MobGroupUtil.isMobBoss(mob)) {
            if (sender == null) {
                return plugin.getMessagesConfig().giveawayNaturalBossNoPlayer();
            }
            return plugin.getMessagesConfig().giveawayNaturalBoss().replaceAll(PlaceholderUtil.PLAYER.asRegex(), sender.getName());
        }
        if (MobGroupUtil.isMobHostile(mob)) {
            if (sender == null) {
                return plugin.getMessagesConfig().giveawayNaturalHostileNoPlayer();
            }
            return plugin.getMessagesConfig().giveawayNaturalHostile().replaceAll(PlaceholderUtil.PLAYER.asRegex(), sender.getName());
        }
        if (MobGroupUtil.isMobNeutral(mob)) {
            if (sender == null) {
                return plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutralNoPlayer());
            }
            return plugin.getMessagesConfig().giveawayNaturalNeutral().replaceAll(PlaceholderUtil.PLAYER.asRegex(), sender.getName());
        }
        
        if (MobGroupUtil.isMobPassive(mob)) {
            if (sender == null) {
                return plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassiveNoPlayer());
            }
            return plugin.getMessagesConfig().giveawayNaturalPassive().replaceAll(PlaceholderUtil.PLAYER.asRegex(), sender.getName());
        }
        if (sender == null) {
            return plugin.getMessagesConfig().giveawayNaturalNoPlayer();
        }
        return plugin.getMessagesConfig().giveawayNatural().replaceAll(PlaceholderUtil.PLAYER.asRegex(), sender.getName());
    }

    
    public static @NotNull List<String> formatLore(final String info, final String about, final String rarity, final boolean isShiny, final String type, final @NotNull Series series) {
        List<String> lore = new ArrayList<>();
        final ColorSeries colorSeries = series.getColorSeries();
        final String typeFormat = ChatUtil.color(colorSeries.getType() + plugin.getGeneralConfig().displayType() + type);
        final String seriesFormat = ChatUtil.color(colorSeries.getSeries() + plugin.getGeneralConfig().displaySeries() + series.getDisplayName());
        final String rarityFormat = ChatUtil.color(colorSeries.getRarity());
        
        lore.add(typeFormat);
        if (info != null) {
            final String infoFormat = ChatUtil.color(colorSeries.getInfo() + plugin.getGeneralConfig().displayInfo());
            if (!"none".equalsIgnoreCase(info) && !info.isEmpty()) {
                lore.add(ChatUtil.color(infoFormat));
                lore.addAll(ChatUtil.wrapString(info));
            } else {
                lore.add(ChatUtil.color(infoFormat + info));
            }
        }
        
        lore.add(seriesFormat);
        if (about != null) {
            final String aboutFormat = ChatUtil.color(colorSeries.getAbout() + plugin.getGeneralConfig().displayAbout());
            if (!"none".equalsIgnoreCase(about) && !about.isEmpty()) {
                lore.add(ChatUtil.color(aboutFormat));
                lore.addAll(ChatUtil.wrapString(about));
            } else {
                lore.add(ChatUtil.color(aboutFormat + about));
            }
        }
        
        final String rarityName = ChatUtil.color(rarity.replace('_', ' '));
        lore.add(ChatUtil.color(getShinyFormat(isShiny, rarityFormat, rarityName)));
        if (plugin.placeholderapi()) {
            lore.forEach(s -> PlaceholderAPI.setPlaceholders(null, s));
        }
        return lore;
    }
    
    //Returns the format if it's shiny. If it isn't returns the normal format.
    private static @NotNull String getShinyFormat(boolean isShiny, String format, String name) {
        if (isShiny) {
            return format + plugin.getGeneralConfig().shinyName() + " " + name;
        }
        return format + name;
    }
    
    public static boolean calculateIfShiny(boolean forcedShiny) {
        if (forcedShiny)
            return true;
        int shinyRandom = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        boolean isShiny = shinyRandom <= plugin.getChancesConfig().shinyVersionChance();
        plugin.debug(AllCardManager.class, "Shiny=" + isShiny + ", Value=" + shinyRandom + ", ShinyChance=" + plugin.getChancesConfig().shinyVersionChance());
        return isShiny;
    }
    
    public static boolean noEconomy(final CommandSender player) {
        if (plugin.getEconomyWrapper() == null) {
            ChatUtil.sendMessage(player, plugin.getMessagesConfig().noEconomy());
            return true;
        }
        return false;
    }
    
    public static boolean hasCardsInInventory(final Player player, final @NotNull PackEntry packEntry) {
        return hasCardsInInventory(player, packEntry, 1);
    }
    
    public static boolean hasCardsInInventory(final Player player, final @NotNull PackEntry packEntry, int amount) {
        return packEntry.amount() * amount <= countCardsInInventory(player, packEntry);
    }

    @Deprecated
    public static int countCardsInInventory(final @NotNull Player player, final PackEntry packEntry) {
        int count = 0;
        PlayerInventory inventory = player.getInventory();
        
        for (ItemStack itemStack : inventory.getContents()) {
            if (ItemExtensionsKt.matchesEntry(itemStack, packEntry)) {
                count += itemStack.getAmount();
            }
        }
        return count;
    }


    /**
     * @param player    Player
     * @param packEntry PackEntry
     * @return A map of the ItemStacks removed and the amounts removed
     */
    public static @NotNull Map<ItemStack, Integer> removeCardsMatchingEntry(final @NotNull Player player, final @NotNull PackEntry packEntry) {
        Map<ItemStack, Integer> removedItemStacks = new HashMap<>();
        PlayerInventory inventory = player.getInventory();
        int countLeftToRemove = packEntry.amount();
        for (ItemStack itemStack : Arrays.stream(inventory.getContents())
            .filter(Objects::nonNull)
            .filter(itemStack -> itemStack.getType() != Material.AIR)
            .toList()) {
            
            boolean matchesEntry = ItemExtensionsKt.matchesEntry(itemStack, packEntry);
            if (matchesEntry) {
                //accounts for zombie:common:10:default when the entry is common:5:default
                if (itemStack.getAmount() > countLeftToRemove) {
                    itemStack.setAmount(itemStack.getAmount() - countLeftToRemove);
                    removedItemStacks.put(itemStack, countLeftToRemove);
                    plugin.debug(BuyCommand.class, "Removed %d from ItemStack %s, new amount: %s".formatted(countLeftToRemove, itemStack.toString(), itemStack.getAmount()));
                    break;
                }
                
                countLeftToRemove -= itemStack.getAmount();
                removedItemStacks.put(itemStack, itemStack.getAmount());
                player.getInventory().removeItem(itemStack);
                plugin.debug(BuyCommand.class, "Removed ItemStack %s, amount left to remove %d".formatted(itemStack.toString(), countLeftToRemove));
            }
        }
        
        return removedItemStacks;
    }
}
