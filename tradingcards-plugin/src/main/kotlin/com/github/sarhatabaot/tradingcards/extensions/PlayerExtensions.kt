package com.github.sarhatabaot.tradingcards.extensions

import com.github.sarhatabaot.kraken.core.chat.ChatUtil
import de.tr7zw.changeme.nbtapi.NBTItem
import net.tinetwork.tradingcards.api.model.pack.PackEntry
import net.tinetwork.tradingcards.api.utils.NbtUtils
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 *
 * @author sarhatabaot
 */
class PlayerExtensions {
}


fun Player.sendTradedCardsMessage(removedCardsMap: Map<ItemStack, Int>) {
    for ((key, value) in removedCardsMap) {
        val nbtItem = NBTItem(key)
        val rarityId = NbtUtils.Card.getRarityId(nbtItem)
        val cardId = NbtUtils.Card.getCardId(nbtItem)
        val seriesId = NbtUtils.Card.getSeriesId(nbtItem)
        sendColoredMessage("- $value $rarityId $cardId $seriesId")
    }
}

fun Player.sendColoredMessage(message: String) {
    this.sendMessage(ChatUtil.color(message))
}

fun Player.sendFormattedMessage(message: String) {
    this.sendMessage(ChatUtil.format(this, message))
}

fun Player.hasCardsInInventory(tradeCards: List<PackEntry>): Boolean {
    if (tradeCards.isEmpty())
        return true

    for (packEntry in tradeCards) {
        if (packEntry.amount > this.countCardsInInventory(packEntry))
            return false
    }
    return true
}

fun Player.countCardsInInventory(packEntry: PackEntry): Int {
    var count = 0

    for (itemStack in this.inventory.contents) {
        if(itemStack.matchesEntry(packEntry)) {
            count += itemStack.amount
        }
    }
    return count
}