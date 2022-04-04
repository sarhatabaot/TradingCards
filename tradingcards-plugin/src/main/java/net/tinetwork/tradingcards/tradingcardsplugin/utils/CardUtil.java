package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.DropTypeManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author sarhatabaot
 */
public class CardUtil {
    private static TradingCards plugin;
    private static TradingCardManager cardManager;
    public static final int RANDOM_MAX = 100000;
    public static ItemStack BLANK_CARD;


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
        final String debugItem = "name:" + nbtItem.getString(NbtUtils.NBT_CARD_NAME) + " rarity:" + nbtItem.getString(NbtUtils.NBT_RARITY);
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
        if (plugin.isMobHostile(e)) {
            return DropTypeManager.HOSTILE;
        }
        if (plugin.isMobNeutral(e)) {
            return DropTypeManager.NEUTRAL;
        }
        if (plugin.isMobPassive(e)) {
            return DropTypeManager.PASSIVE;
        }
        return DropTypeManager.BOSS;
    }

    public static boolean isCard(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir())
            return false;
        final NBTItem nbtItem = new NBTItem(itemStack);
        return isCard(nbtItem);
    }

    public static boolean isCard(final @NotNull NBTItem nbtItem) {
        return nbtItem.getBoolean(NbtUtils.NBT_IS_CARD);
    }

    private static void broadcastPrefixedMessage(final String message) {
        Bukkit.broadcastMessage(plugin.getPrefixedMessage(message));
    }

    public static void giveawayNatural(EntityType mob, Player sender) {
        if (plugin.isMobBoss(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBossNoPlayer());
            } else {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBoss().replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), sender.getName()));
            }
        } else if (plugin.isMobHostile(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostileNoPlayer());
            } else {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostile().replace(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), sender.getName()));
            }
        } else if (plugin.isMobNeutral(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutralNoPlayer()));
            } else {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutral().replace(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), sender.getName())));
            }
        } else if (plugin.isMobPassive(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassiveNoPlayer()));
            } else {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassive().replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), sender.getName())));
            }
        } else if (sender == null) {
            broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNoPlayer());
        } else {
            broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNatural().replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), sender.getName()));
        }

        for (final Player p : Bukkit.getOnlinePlayers()) {
            String rare = cardManager.getRandomRarity(CardUtil.getMobType(mob), true);
            plugin.debug(CardUtil.class, "onCommand.rare: " + rare);
            CardUtil.dropItem(p, cardManager.getRandomCard(rare).build(false));
        }

    }

    public static @NotNull String formatDisplayName(final @NotNull TradingCard card, boolean shiny) {
        final String[] shinyPlayerCardFormat = new String[]{PlaceholderUtil.PREFIX, PlaceholderUtil.COLOR, PlaceholderUtil.NAME, PlaceholderUtil.BUY_PRICE, PlaceholderUtil.SELL_PRICE, PlaceholderUtil.SHINY_PREFIX};
        final String[] shinyCardFormat = new String[]{PlaceholderUtil.PREFIX, PlaceholderUtil.COLOR, PlaceholderUtil.NAME, PlaceholderUtil.BUY_PRICE, PlaceholderUtil.SELL_PRICE, PlaceholderUtil.SHINY_PREFIX, "_"};

        final String[] cardFormat = new String[]{PlaceholderUtil.PREFIX, PlaceholderUtil.COLOR, PlaceholderUtil.NAME, PlaceholderUtil.BUY_PRICE, PlaceholderUtil.SELL_PRICE, "_"};
        final String[] playerCardFormat = new String[]{PlaceholderUtil.PREFIX, PlaceholderUtil.COLOR, PlaceholderUtil.NAME, PlaceholderUtil.BUY_PRICE, PlaceholderUtil.SELL_PRICE};

        Rarity rarity = card.getRarity();
        final String shinyTitle = plugin.getGeneralConfig().displayShinyTitle();
        final String title = plugin.getGeneralConfig().displayTitle();
        final String shinyPrefix = plugin.getGeneralConfig().shinyName();
        final String prefix = plugin.getGeneralConfig().cardPrefix();
        final String rarityColour = rarity.getDefaultColor();
        final String buyPrice = String.valueOf(card.getBuyPrice());
        final String sellPrice = String.valueOf(card.getSellPrice());

        String finalTitle;
        if (shiny && shinyPrefix != null) {
            if (card.isPlayerCard()) {
                finalTitle = ChatUtil.color(StringUtils.replaceEach(shinyTitle.replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.SHINY_PREFIX_ALT), PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.SHINY_PREFIX)), shinyPlayerCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice, shinyPrefix}));
            } else {
                finalTitle = ChatUtil.color(StringUtils.replaceEach(shinyTitle.replace(PlaceholderUtil.SHINY_PREFIX_ALT, PlaceholderUtil.SHINY_PREFIX), shinyCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice, shinyPrefix, " "}));
            }
        } else {
            if (card.isPlayerCard()) {
                finalTitle = ChatUtil.color(StringUtils.replaceEach(title, playerCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice}));
            } else {
                finalTitle = ChatUtil.color(StringUtils.replaceEach(title, cardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice, " "}));
            }
        }
        if (plugin.placeholderapi()) {
            return PlaceholderAPI.setPlaceholders(null, finalTitle);
        }
        return finalTitle;
    }


    public static @NotNull List<String> formatLore(final String info, final String about, final String rarity, final boolean isShiny, final String type, final @NotNull Series series) {
        List<String> lore = new ArrayList<>();
        final ColorSeries colorSeries = series.getColorSeries();
        final String typeFormat = colorSeries.getType() + plugin.getGeneralConfig().displayType() + type;
        final String infoFormat = colorSeries.getInfo() + plugin.getGeneralConfig().displayInfo();
        final String seriesFormat = colorSeries.getSeries() + plugin.getGeneralConfig().displaySeries() + series.getDisplayName();
        final String aboutFormat = colorSeries.getAbout() + plugin.getGeneralConfig().displayAbout();
        final String rarityFormat = colorSeries.getRarity();

        lore.add(typeFormat);
        if (!"none".equalsIgnoreCase(info) && !info.isEmpty()) {
            lore.add(infoFormat);
            lore.addAll(ChatUtil.wrapString(info));
        } else {
            lore.add(infoFormat + info);
        }

        lore.add(seriesFormat);
        if (!"none".equalsIgnoreCase(about) && !about.isEmpty()) {
            lore.add(aboutFormat);
            lore.addAll(ChatUtil.wrapString(about));
        } else {
            lore.add(aboutFormat + about);
        }

        final String rarityName = ChatUtil.color(rarity.replace('_', ' '));
        lore.add(getShinyFormat(isShiny, rarityFormat, rarityName));

        lore.forEach(ChatUtil::color);
        if(plugin.placeholderapi()) {
            lore.forEach(s -> PlaceholderAPI.setPlaceholders(null, s));
        }
        return lore;
    }

    //Returns the format if it's shiny. If it isn't returns the normal format.
    private static String getShinyFormat(boolean isShiny, String format, String name) {
        if (isShiny) {
            return format + plugin.getGeneralConfig().shinyName() + " " + name;
        }
        return format + name;
    }


    @Contract(pure = true)
    public static @NotNull String cardKey(@NotNull final String rarityId, @NotNull final String cardId) {
        return rarityId + "." + cardId;
    }

    public static boolean calculateIfShiny(boolean forcedShiny) {
        if (forcedShiny)
            return true;
        int shinyRandom = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        boolean isShiny = shinyRandom <= plugin.getChancesConfig().shinyVersionChance();
        plugin.debug(TradingCardManager.class, "Shiny=" + isShiny + ", Value=" + shinyRandom + ", ShinyChance=" + plugin.getChancesConfig().shinyVersionChance());
        return isShiny;
    }

    public static boolean hasVault(final CommandSender player) {
        if (!plugin.isHasVault()) {
            ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().noVault());
            return false;
        }
        return true;
    }
}
