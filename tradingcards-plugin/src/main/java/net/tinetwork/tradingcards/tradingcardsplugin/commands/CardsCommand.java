package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.MessagesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.denylist.PlayerDenylist;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("cards")
public class CardsCommand extends BaseCommand {
    private final TradingCards plugin;
    private final AllCardManager cardManager;
    private final PlayerDenylist playerBlacklist;

    private final MessagesConfig messagesConfig;

    public CardsCommand(final @NotNull TradingCards plugin, final PlayerDenylist playerBlacklist) {
        this.plugin = plugin;
        this.playerBlacklist = playerBlacklist;
        this.cardManager = plugin.getCardManager();
        this.messagesConfig = plugin.getMessagesConfig();
    }

    //Convenience
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
        ChatUtil.sendMessage(sender, InternalMessages.CardsCommand.VERSION.formatted(plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAPIVersion()));
    }

    @Subcommand("reload")
    @CommandPermission(Permissions.RELOAD)
    @Description("Reloads all the configs.")
    public void onReload(final CommandSender sender) {
        ChatUtil.sendMessage(sender, messagesConfig.reload());
        plugin.reloadPlugin();
    }


    @Subcommand("resolve")
    @CommandPermission(Permissions.RESOLVE)
    @Description("Shows a player's uuid")
    public void onResolve(final CommandSender sender, final @NotNull Player player) {
        ChatUtil.sendMessage(sender,
                plugin.getMessagesConfig().resolveMsg().replaceAll(PlaceholderUtil.DISPLAY_NAME.asRegex(),
                        player.getName()).replaceAll(PlaceholderUtil.UUID.asRegex(), player.getUniqueId().toString()));
    }

    @Subcommand("toggle")
    @CommandPermission(Permissions.TOGGLE)
    @Description("Toggles card drops from mobs.")
    public void onToggle(final Player player) {
        if (playerBlacklist.isAllowed(player)) {
            playerBlacklist.add(player);
            ChatUtil.sendMessage(player, plugin.getMessagesConfig().toggleDisabled());
        } else {
            playerBlacklist.remove(player);
            ChatUtil.sendMessage(player, plugin.getMessagesConfig().toggleEnabled());
        }
    }

    @Subcommand("worth")
    @CommandPermission(Permissions.WORTH)
    @Description("Shows a card's worth.")
    public void onWorth(final Player player) {
        if (CardUtil.noEconomy(player)) {
            return;
        }
        final NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
        if (!CardUtil.isCard(nbtItem)) {
            player.sendMessage(messagesConfig.notACard());
            return;
        }

        final String cardId = NbtUtils.Card.getCardId(nbtItem);
        final String rarityId = NbtUtils.Card.getRarityId(nbtItem);
        final String seriesId = NbtUtils.Card.getSeriesId(nbtItem);
        debug(InternalDebug.CardsCommand.CARD_RARITY_ID.formatted(cardId,rarityId));

        final TradingCard tradingCard = cardManager.getCard(cardId, rarityId, seriesId);
        final double buyPrice = tradingCard.getBuyPrice();
        final double sellPrice = tradingCard.getSellPrice();

        final String buyMessage = (buyPrice > 0.0D) ?
                messagesConfig.canBuy().replaceAll(PlaceholderUtil.BUY_AMOUNT.asRegex(), String.valueOf(buyPrice)) : messagesConfig.canNotBuy();
        final String sellMessage = (sellPrice > 0.0D) ? messagesConfig.canSell().replaceAll(PlaceholderUtil.SELL_AMOUNT.asRegex(), String.valueOf(sellPrice)) : messagesConfig.canNotSell();
        debug(InternalDebug.CardsCommand.BUY_SELL_PRICE.formatted(buyPrice,sellPrice));
        ChatUtil.sendMessage(player, buyMessage);
        ChatUtil.sendMessage(player, sellMessage);
    }

}





