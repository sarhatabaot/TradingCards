package com.github.sarhatabaot.tradingcards.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.sarhatabaot.tradingcards.card.EmptyCard
import com.github.sarhatabaot.tradingcards.extensions.hasCardsInInventory
import com.github.sarhatabaot.tradingcards.extensions.sendColoredMessage
import com.github.sarhatabaot.tradingcards.extensions.sendTradedCardsMessage
import com.github.sarhatabaot.tradingcards.utils.PlaceholderUtil
import net.tinetwork.tradingcards.api.manager.PackManager
import net.tinetwork.tradingcards.api.model.Rarity
import net.tinetwork.tradingcards.api.model.Series
import net.tinetwork.tradingcards.api.model.pack.Pack
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream


/**
 * @author sarhatabaot
 */
@CommandAlias("cards buy")
@CommandPermission(Permissions.User.Economy.BUY)
class BuyCommand(var plugin: TradingCards): BaseCommand() {
    private var packManager: PackManager = plugin.packManager
    private var cardManager: AllCardManager = plugin.cardManager

    @Subcommand("pack")
    @CommandPermission(Permissions.User.Economy.BUY_PACK)
    @CommandCompletion("@packs")
    @Description("Buy a pack.")
    fun onBuyPack(player: Player, packId: String, @Optional amount: Int) {
        if (CardUtil.noEconomy(player))
            return

        if (!packManager.containsPack(packId)) {
            player.sendMessage(plugin.messagesConfig.packDoesntExist())
            return
        }

        val pack = packManager.getPack(packId)
        if (pack.buyPrice <= 0.0 && pack.tradeCards.isEmpty()) {
            player.sendMessage(plugin.messagesConfig.cannotBeBought())
            return
        }
        val amountToBuy = getAmount(amount)
        if (pack.tradeCards.isNotEmpty() && !player.hasCardsInInventory(pack.tradeCards)) {
            player.sendMessage("Not enough cards to perform a trade in.")
            return
        }
        val totalPrice = pack.buyPrice * amountToBuy
        val economyResponse = plugin.economyWrapper.withdraw(player, pack.currencyId, totalPrice)
        if (economyResponse.success()) {
            if (plugin.generalConfig.closedEconomy()) {
                plugin.economyWrapper.depositAccount(plugin.generalConfig.serverAccount(), pack.currencyId, totalPrice)
            }
            if (pack.tradeCards.isNotEmpty()) {
                for (i in 0..amount) {
                    tradeCards(pack, player)
                }
            }
            player.sendMessage(
                plugin.messagesConfig.boughtCard()
                    .replace(PlaceholderUtil.AMOUNT.asRegex().toRegex(), totalPrice.toString())
            )
            val packItem = plugin.packManager.getPackItem(packId)
            packItem.amount = amount
            CardUtil.dropItem(player, packItem)
            return
        }
        player.sendMessage(plugin.messagesConfig.notEnoughMoney())
    }

    private fun tradeCards(pack: Pack, player: Player) {
        val packId = pack.id
        var removedCardsMap: Map<ItemStack, Int> = HashMap()
        for (packEntry in pack.tradeCards) {
            val removedEntryItems = CardUtil.removeCardsMatchingEntry(player, packEntry)
            removedCardsMap = Stream.concat<Map.Entry<ItemStack, Int>>(
                removedCardsMap.entries.stream(),
                removedEntryItems.entries.stream()
            ).collect(
                Collectors.toMap<Map.Entry<ItemStack, Int>, ItemStack, Int>(
                    Function<Map.Entry<ItemStack, Int>, ItemStack> { it: Map.Entry<ItemStack?, Int>? -> java.util.Map.Entry.key },
                    Function<Map.Entry<ItemStack, Int>, Int> { java.util.Map.Entry.value })
            )
        }
        val totalCards = removedCardsMap.values.stream().mapToInt { obj: Int -> obj }.sum()
        player.sendMessage("Traded $totalCards cards for pack $packId:")
        player.sendTradedCardsMessage(removedCardsMap)
    }

    private fun getAmount(amount: Int): Int {
        if (amount == 0) {
            return 1
        }
        return if (amount < 0) {
            amount * -1
        } else amount
    }


    @Subcommand("card")
    @CommandPermission(Permissions.User.Economy.BUY_CARD)
    @Description("Buy a card.")
    @CommandCompletion("@rarities @series @cards")
    fun onBuyCard(player: Player, rarity: Rarity, series: Series, cardId: String) {
        if (CardUtil.noEconomy(player)) return
        if (cardManager.getCard(cardId, rarity.id, series.id) is EmptyCard) {
            player.sendMessage(plugin.messagesConfig.cardDoesntExist())
            return
        }
        val tradingCard = cardManager.getCard(cardId, rarity.id, series.id)
        val buyPrice = tradingCard.buyPrice
        val currencyId = tradingCard.currencyId
        val economyResponse = plugin.economyWrapper.withdraw(player, currencyId, buyPrice)
        if (economyResponse.success()) {
            if (plugin.generalConfig.closedEconomy()) {
                plugin.economyWrapper.depositAccount(plugin.generalConfig.serverAccount(), currencyId, buyPrice)
            }
            CardUtil.dropItem(player, tradingCard.build(false))
            player.sendColoredMessage(
                plugin.messagesConfig.boughtCard()
                    .replace(PlaceholderUtil.AMOUNT.asRegex().toRegex(), buyPrice.toString())
            )
            return
        }
        player.sendColoredMessage(plugin.messagesConfig.notEnoughMoney())
    }
}