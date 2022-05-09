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
        broadcastPrefixedMessage(getGiveawayMessage(mob, sender));
        for (final Player p : Bukkit.getOnlinePlayers()) {
            String rare = cardManager.getRandomRarity(CardUtil.getMobType(mob), true);
            CardUtil.dropItem(p, cardManager.getRandomCard(rare).build(false));
        }
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

    public static @NotNull String formatDisplayName(final @NotNull TradingCard card) {
        final String finalTitle = getFormattedTitle(card);

        if (plugin.placeholderapi()) {
            return PlaceholderAPI.setPlaceholders(null, finalTitle);
        }
        return finalTitle;
    }

    private static @NotNull String getFormattedTitle(@NotNull TradingCard card) {
        Rarity rarity = card.getRarity();
        String shinyTitle = plugin.getGeneralConfig().displayShinyTitle().replaceAll(PlaceholderUtil.SHINY_PREFIX_ALT.asRegex(), PlaceholderUtil.SHINY_PREFIX.placeholder());
        String title = plugin.getGeneralConfig().displayTitle();
        final String shinyPrefix = plugin.getGeneralConfig().shinyName();
        final String prefix = plugin.getGeneralConfig().cardPrefix();
        final String rarityColor = rarity.getDefaultColor();
        final String buyPrice = String.valueOf(card.getBuyPrice());
        final String sellPrice = String.valueOf(card.getSellPrice());

        if (card.isShiny() && shinyPrefix != null) {
            shinyTitle = ChatUtil.color(shinyTitle.replaceAll(PlaceholderUtil.PREFIX.asRegex(),prefix)
                    .replaceAll(PlaceholderUtil.SHINY_PREFIX.asRegex(), shinyPrefix)
                    .replaceAll(PlaceholderUtil.COLOR.asRegex(),rarityColor)
                    .replaceAll(PlaceholderUtil.DISPLAY_NAME.asRegex(), card.getDisplayName())
                    .replaceAll(PlaceholderUtil.BUY_PRICE.asRegex(), buyPrice)
                    .replaceAll(PlaceholderUtil.SELL_PRICE.asRegex(), sellPrice));

            if (card.isPlayerCard()) {
                return shinyTitle;
            }
            return shinyTitle.replace("_", " ");
        }

        title = ChatUtil.color(title.replaceAll(PlaceholderUtil.PREFIX.asRegex(),prefix)
                .replaceAll(PlaceholderUtil.COLOR.asRegex(),rarityColor)
                .replaceAll(PlaceholderUtil.DISPLAY_NAME.asRegex(), card.getDisplayName())
                .replaceAll(PlaceholderUtil.BUY_PRICE.asRegex(), buyPrice)
                .replaceAll(PlaceholderUtil.SELL_PRICE.asRegex(), sellPrice));
        if (card.isPlayerCard()) {
            return title;
        }
        return title.replace("_", " ");
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


    @Contract(pure = true)
    @Deprecated
    /**
     * @since 5.7.0
     * @forRemoval
     */
    public static @NotNull String cardKey(@NotNull final String rarityId, @NotNull final String cardId) {
        return rarityId + "." + cardId;
    }

    @Contract(pure = true)
    public static @NotNull String cardKey(@NotNull final String rarityId, @NotNull final String cardId, @NotNull final String seriesId) {
        return rarityId + "." + cardId+"."+seriesId;
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
            ChatUtil.sendMessage(player, plugin.getMessagesConfig().noVault());
            return false;
        }
        return true;
    }
}
