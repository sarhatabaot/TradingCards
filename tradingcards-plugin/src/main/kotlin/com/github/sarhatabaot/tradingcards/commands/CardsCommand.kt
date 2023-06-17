package com.github.sarhatabaot.tradingcards.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.github.sarhatabaot.tradingcards.card.TradingCard
import com.github.sarhatabaot.tradingcards.extensions.isCard
import com.github.sarhatabaot.tradingcards.extensions.sendColoredMessage
import com.github.sarhatabaot.tradingcards.utils.PlaceholderUtil
import de.tr7zw.changeme.nbtapi.NBTItem
import net.tinetwork.tradingcards.api.utils.NbtUtils
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.MessagesConfig
import net.tinetwork.tradingcards.tradingcardsplugin.denylist.PlayerDenylist
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
class CardsCommand(var plugin: TradingCards, var playerDenyList: PlayerDenylist) : BaseCommand() {
    var cardManager: AllCardManager = plugin.cardManager
    private var messagesConfig: MessagesConfig = plugin.messagesConfig

    @CatchUnknown
    @HelpCommand
    fun onHelp(help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("version|ver")
    @CommandPermission(Permissions.Admin.VERSION)
    @Description("Show the plugin version.")
    fun onVersion(sender: CommandSender) {
        sender.sendColoredMessage(
            InternalMessages.CardsCommand.VERSION.format(
                plugin.name,
                plugin.description.version,
                plugin.description.apiVersion
            )
        )
    }

    @Subcommand("reload")
    @CommandPermission(Permissions.Admin.RELOAD)
    @Description("Reloads all the configs.")
    fun onReload(sender: CommandSender) {
        sender.sendColoredMessage(messagesConfig.reload())
        plugin.reloadPlugin()
    }

    @Subcommand("resolve")
    @CommandPermission(Permissions.Admin.RESOLVE)
    @Description("Shows a player's uuid")
    fun onResolve(sender: CommandSender, player: Player) {
        sender.sendColoredMessage(
            plugin.messagesConfig.resolveMsg().replace(
                PlaceholderUtil.DISPLAY_NAME.asRegex().toRegex(),
                player.name
            ).replace(PlaceholderUtil.UUID.asRegex().toRegex(), player.uniqueId.toString())
        )
    }

    @Subcommand("toggle")
    @CommandPermission(Permissions.TOGGLE)
    @Description("Toggles card drops from mobs.")
    fun onToggle(player: Player) {
        if (playerDenyList.isAllowed(player)) {
            playerDenyList.add(player)
            player.sendColoredMessage(plugin.messagesConfig.toggleDisabled())
        } else {
            playerDenyList.remove(player)
            player.sendColoredMessage(plugin.messagesConfig.toggleEnabled())
        }
    }

    @Subcommand("worth")
    @CommandPermission(Permissions.User.Economy.WORTH)
    @Description("Shows a card's worth.")
    fun onWorth(player: Player) {
        if (CardUtil.noEconomy(player)) {
            return
        }
        val item = player.inventory.itemInMainHand
        val nbtItem = NBTItem(player.inventory.itemInMainHand)
        if (!item.isCard()) {
            player.sendMessage(messagesConfig.notACard())
            return
        }
        val cardId = NbtUtils.Card.getCardId(nbtItem)
        val rarityId = NbtUtils.Card.getRarityId(nbtItem)
        val seriesId = NbtUtils.Card.getSeriesId(nbtItem)

        plugin.debug(CardsCommand::class.java,InternalDebug.CardsCommand.CARD_RARITY_ID.format(cardId, rarityId))

        val tradingCard: TradingCard = cardManager.getCard(cardId, rarityId, seriesId)
        val buyPrice = tradingCard.buyPrice
        val sellPrice = tradingCard.sellPrice

        val buyMessage = if (buyPrice > 0.0) messagesConfig.canBuy().replace(
            PlaceholderUtil.BUY_AMOUNT.asRegex().toRegex(),
            buyPrice.toString()
        ) else messagesConfig.canNotBuy()

        val sellMessage = if (sellPrice > 0.0) messagesConfig.canSell().replace(
            PlaceholderUtil.SELL_AMOUNT.asRegex().toRegex(),
            sellPrice.toString()
        ) else messagesConfig.canNotSell()

        plugin.debug(CardsCommand::class.java,InternalDebug.CardsCommand.BUY_SELL_PRICE.format(buyPrice, sellPrice))
        player.sendColoredMessage(buyMessage)
        player.sendColoredMessage(sellMessage)
    }

}