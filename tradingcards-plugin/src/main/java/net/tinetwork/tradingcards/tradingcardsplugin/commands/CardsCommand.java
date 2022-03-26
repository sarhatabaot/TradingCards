package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.MessagesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.PlayerBlacklist;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("cards")
public class CardsCommand extends BaseCommand {
    private final TradingCards plugin;
    private final TradingCardManager cardManager;
    private final PlayerBlacklist playerBlacklist;

    private final MessagesConfig messagesConfig;

    protected static final String PLAYER_NOT_ONLINE = "This player is not online. Or doesn't exist.";
    protected static final String CANNOT_SELL_CARD = "Cannot sell this card.";

    public CardsCommand(final @NotNull TradingCards plugin, final PlayerBlacklist playerBlacklist) {
        this.plugin = plugin;
        this.playerBlacklist = playerBlacklist;
        this.cardManager = plugin.getCardManager();
        this.messagesConfig = plugin.getMessagesConfig();
    }

    private void debug(final String message) {
        plugin.debug(getClass(), message);
    }

    @CatchUnknown
    @HelpCommand
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("version|ver")
    @CommandPermission(Permissions.VERSION)
    @Description("Show the plugin version.")
    public void onVersion(final CommandSender sender) {
        final String format = "%s %s API-%s";
        ChatUtil.sendMessage(sender, String.format(format, plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAPIVersion()));
    }

    @Subcommand("reload")
    @CommandPermission(Permissions.RELOAD)
    @Description("Reloads all the configs.")
    public void onReload(final CommandSender sender) {
        ChatUtil.sendPrefixedMessage(sender, messagesConfig.reload());
        plugin.reloadPlugin();
    }


    @Subcommand("resolve")
    @CommandPermission(Permissions.RESOLVE)
    @Description("Shows a player's uuid")
    public void onResolve(final CommandSender sender, final @NotNull Player player) {
        ChatUtil.sendMessage(sender, plugin.getMessagesConfig().resolveMsg().replace(PlaceholderUtil.NAME, player.getName()).replace(PlaceholderUtil.UUID, player.getUniqueId().toString()));
    }

    @Subcommand("toggle")
    @CommandPermission(Permissions.TOGGLE)
    @Description("Toggles card drops from mobs.")
    public void onToggle(final Player player) {
        if (playerBlacklist.isAllowed(player)) {
            playerBlacklist.add(player);
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleDisabled()));
        } else {
            playerBlacklist.remove(player);
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleEnabled()));
        }
    }


    private String getFormattedRarity(final String rarity) {
        for (final Rarity rarityKey : plugin.getRarityManager().getRarities()) {
            if (rarityKey.getId().equalsIgnoreCase(rarity.replace("_", " "))) {
                return rarityKey.getId();
            }
        }
        return "";
    }

    @Subcommand("giveaway rarity")
    @CommandPermission(Permissions.GIVEAWAY_RARITY)
    @Description("Give away a random card by rarity to the server.")
    @CommandCompletion("@rarities")
    public void onGiveawayRarity(final CommandSender sender, final String rarity) {
        if (plugin.getRarityManager().getRarity(rarity) == null) {
            ChatUtil.sendPrefixedMessage(sender, messagesConfig.noRarity());
            return;
        }

        Bukkit.broadcastMessage(plugin.getPrefixedMessage(messagesConfig.giveaway().replace(PlaceholderUtil.PLAYER, sender.getName()).replace(PlaceholderUtil.RARITY, getFormattedRarity(rarity))));
        for (final Player p5 : Bukkit.getOnlinePlayers()) {
            CardUtil.dropItem(p5, cardManager.getRandomCard(rarity).build(false));
        }
    }

    @Subcommand("giveaway entity")
    @CommandPermission(Permissions.GIVEAWAY_ENTITY)
    @Description("Give away a random card by entity to the server.")
    public void onGiveawayMob(final CommandSender sender, final String entity) {
        if (plugin.isMob(entity)) {
            if (sender instanceof ConsoleCommandSender) {
                CardUtil.giveawayNatural(EntityType.valueOf(entity.toUpperCase()), null);
            } else {
                CardUtil.giveawayNatural(EntityType.valueOf(entity.toUpperCase()), (Player) sender);
            }
        }
    }

    @Subcommand("worth")
    @CommandPermission(Permissions.WORTH)
    @Description("Shows a card's worth.")
    public void onWorth(final Player player) {
        if (!CardUtil.hasVault(player)) {
            return;
        }
        final NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
        if (!CardUtil.isCard(nbtItem)) {
            ChatUtil.sendPrefixedMessage(player, messagesConfig.notACard());
            return;
        }

        final String cardId = nbtItem.getString(NbtUtils.NBT_CARD_NAME);
        final String rarityId = nbtItem.getString(NbtUtils.NBT_RARITY);
        debug("Card name=" + cardId + ", Card rarity=" + rarityId);

        final TradingCard tradingCard = cardManager.getCard(cardId, rarityId, false);
        final double buyPrice = tradingCard.getBuyPrice();
        final double sellPrice = tradingCard.getSellPrice();

        final String buyMessage = (buyPrice > 0.0D) ? messagesConfig.canBuy().replace(PlaceholderUtil.BUY_AMOUNT, String.valueOf(buyPrice)) : messagesConfig.canNotBuy();
        final String sellMessage = (sellPrice > 0.0D) ? messagesConfig.canSell().replace(PlaceholderUtil.SELL_AMOUNT, String.valueOf(sellPrice)) : messagesConfig.canNotSell();
        debug("buy=" + buyPrice + "|sell=" + sellPrice);
        ChatUtil.sendPrefixedMessage(player, buyMessage);
        ChatUtil.sendPrefixedMessage(player, sellMessage);
    }

}





