package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import de.tr7zw.nbtapi.NBTItem;
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
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author sarhatabaot
 */
public class CardUtil {
    private static TradingCards plugin;
    private static TradingCardManager cardManager;
    private static final char ALT_COLOR_CHAR = '&';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + ALT_COLOR_CHAR + "[0-9A-FK-ORX]");
    public static final int RANDOM_MAX = 100000;
    public static ItemStack BLANK_CARD;

    private static final String PLACEHOLDER_PREFIX = "%PREFIX%";
    private static final String PLACEHOLDER_COLOR = "%COLOR%";
    private static final String PLACEHOLDER_NAME = "%NAME%";
    private static final String PLACEHOLDER_BUY_PRICE = "%BUY_PRICE%";
    private static final String PLACEHOLDER_SELL_PRICE = "%SELL_PRICE%";
    private static final String PLACEHOLDER_SHINY_PREFIX = "%SHINY_PREFIX%";
    private static final String PLACEHOLDER_SHINY_PREFIX_ALT = "%SHINYPREFIX%";

    private CardUtil() {
        throw new UnsupportedOperationException();
    }

    public static void init(final @NotNull TradingCards plugin) {
        CardUtil.plugin = plugin;
        CardUtil.cardManager = plugin.getCardManager();
        CardUtil.BLANK_CARD = plugin.getGeneralConfig().blankCard();
    }

    public static @NotNull String getRarityName(@NotNull final String rarity) {
        return rarity.replace(stripAllColor(plugin.getGeneralConfig().shinyName()), "").trim();
    }

    /**
     * Drops an item at the player's location.
     *
     * @param player Player
     * @param item   Item
     */
    public static void dropItem(final @NotNull Player player, final ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        final String debugItem = "name:" +nbtItem.getString(NbtUtils.NBT_CARD_NAME) + " rarity:"+nbtItem.getString(NbtUtils.NBT_RARITY);
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            plugin.debug(CardUtil.class,"Added item "+debugItem+" to "+ player.getName());
        } else {
            World playerWorld = player.getWorld();
            if (player.getGameMode() == GameMode.SURVIVAL) {
                playerWorld.dropItem(player.getLocation(), item);
                plugin.debug(CardUtil.class,"Dropped item "+debugItem+" @ "+ player.getLocation());
            }
        }
    }

    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    @Contract("!null -> !null; null -> null")
    @Nullable
    public static String stripAllColor(@Nullable final String input) {
        if (input == null) {
            return null;
        }

        return ChatColor.stripColor(STRIP_COLOR_PATTERN.matcher(input).replaceAll(""));
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


    /**
     * Returns the rarity that should drop.
     *
     * @param e
     * @param alwaysDrop
     * @return String Rarity that should drop
     */
    @NotNull
    public static String calculateRarity(EntityType e, boolean alwaysDrop) {
        int randomChance = plugin.getRandom().nextInt(RANDOM_MAX) + 1;
        for (Rarity rarity : plugin.getRaritiesConfig().rarities()) {
            var chance = plugin.getChancesConfig().getChance(rarity.getName()).getFromMobType(CardUtil.getMobType(e));
            if (alwaysDrop || randomChance < chance)
                return rarity.getName();
        }

        return "None";
    }


    public static boolean isCard(final ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(NbtUtils.NBT_IS_CARD);
    }

    private static boolean isCardMaterial(final Material material) {
        return material == plugin.getGeneralConfig().cardMaterial();
    }

    private static void broadcastPrefixedMessage(final String message) {
        Bukkit.broadcastMessage(plugin.getPrefixedMessage(message));
    }

    public static void giveawayNatural(EntityType mob, Player sender) {
        if (plugin.isMobBoss(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBossNoPlayer());
            } else {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBoss().replace("%player%", sender.getName()));
            }
        } else if (plugin.isMobHostile(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostileNoPlayer());
            } else {
                broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostile().replace("%player%", sender.getName()));
            }
        } else if (plugin.isMobNeutral(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutralNoPlayer()));
            } else {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutral().replace("%player%", sender.getName())));
            }
        } else if (plugin.isMobPassive(mob)) {
            if (sender == null) {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassiveNoPlayer()));
            } else {
                broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassive().replace("%player%", sender.getName())));
            }
        } else if (sender == null) {
            broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNoPlayer());
        } else {
            broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNatural().replace("%player%", sender.getName()));
        }

        for (final Player p : Bukkit.getOnlinePlayers()) {
            String rare = cardManager.getRandomRarity(CardUtil.getMobType(mob), true);
            plugin.debug(CardUtil.class,"onCommand.rare: " + rare);
            CardUtil.dropItem(p, cardManager.getRandomCard(rare, false).build());
        }

    }

    public static @NotNull String formatDisplayName(final @NotNull TradingCard card) {
        final String[] shinyPlayerCardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE, PLACEHOLDER_SELL_PRICE, PLACEHOLDER_SHINY_PREFIX};
        final String[] shinyCardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE, PLACEHOLDER_SELL_PRICE, PLACEHOLDER_SHINY_PREFIX, "_"};

        final String[] cardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE, PLACEHOLDER_SELL_PRICE, "_"};
        final String[] playerCardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE, PLACEHOLDER_SELL_PRICE};

        Rarity rarity = card.getRarity();
        final String shinyTitle = plugin.getGeneralConfig().displayShinyTitle();
        final String title = plugin.getGeneralConfig().displayTitle();
        final String shinyPrefix = plugin.getGeneralConfig().shinyName();
        final String prefix = plugin.getGeneralConfig().cardPrefix();
        final String rarityColour = rarity.getDefaultColor();
        final String buyPrice = String.valueOf(card.getBuyPrice());
        final String sellPrice = String.valueOf(card.getSellPrice());



        if (card.isShiny() && shinyPrefix != null) {
            if (card.isPlayerCard()) {
                return ChatUtil.color(StringUtils.replaceEach(shinyTitle.replace(PLACEHOLDER_SHINY_PREFIX_ALT,PLACEHOLDER_SHINY_PREFIX), shinyPlayerCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice, shinyPrefix}));
            }
            return ChatUtil.color(StringUtils.replaceEach(shinyTitle.replace(PLACEHOLDER_SHINY_PREFIX_ALT,PLACEHOLDER_SHINY_PREFIX), shinyCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice, shinyPrefix, " "}));
        }
        if (card.isPlayerCard()) {
            return ChatUtil.color(StringUtils.replaceEach(title, playerCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice}));
        }
        return ChatUtil.color(StringUtils.replaceEach(title, cardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice, sellPrice, " "}));
    }


    public static @NotNull List<String> formatLore(final String info, final String about, final String rarity, final boolean isShiny, final String type, final Series series) {
        List<String> lore = new ArrayList<>();
        final String typeFormat = ChatUtil.color(plugin.getSeriesConfig().getColorSeries(series.getName()).getType() + plugin.getGeneralConfig().displayType() + type);
        final String infoFormat = ChatUtil.color(plugin.getSeriesConfig().getColorSeries(series.getName()).getInfo() + plugin.getGeneralConfig().displayInfo());
        final String seriesFormat = ChatUtil.color(plugin.getSeriesConfig().getColorSeries(series.getName()).getSeries() + plugin.getGeneralConfig().displaySeries() + series.getDisplayName());
        final String aboutFormat = ChatUtil.color(plugin.getSeriesConfig().getColorSeries(series.getName()).getAbout() + plugin.getGeneralConfig().displayAbout());
        final String rarityFormat = ChatUtil.color(plugin.getSeriesConfig().getColorSeries(series.getName()).getRarity());

        lore.add(typeFormat);
        if (!"None".equalsIgnoreCase(info) && !info.isEmpty()) {
            lore.add(infoFormat);
            lore.addAll(ChatUtil.wrapString(info));
        } else {
            lore.add(infoFormat + info);
        }

        lore.add(seriesFormat);
        if (about != null) {
            lore.add(aboutFormat + about);
        }

        final String rarityName = ChatUtil.color(rarity.replace('_', ' '));
        if (isShiny) {
            lore.add(ChatUtil.color(rarityFormat + plugin.getGeneralConfig().shinyName() + " " + rarityName));
        } else {
            lore.add(ChatUtil.color(rarityFormat + rarityName));
        }

        return lore;
    }



    @Contract(pure = true)
    public static @NotNull String cardKey(String rarity, String cardName) {
        return rarity + "." + cardName;
    }

    public static boolean calculateIfShiny(boolean forcedShiny) {
        if (forcedShiny)
            return true;
        int shinyRandom = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        boolean isShiny = shinyRandom <= plugin.getChancesConfig().shinyVersionChance();
        plugin.debug(TradingCardManager.class,"Shiny="+isShiny+", Value="+shinyRandom+", ShinyChance="+plugin.getChancesConfig().shinyVersionChance());
        return isShiny;
    }
}
