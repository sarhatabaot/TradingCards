package com.github.sarhatabaot.tradingcards.extensions

import de.tr7zw.changeme.nbtapi.NBTItem
import net.tinetwork.tradingcards.api.model.pack.PackEntry
import net.tinetwork.tradingcards.api.utils.NbtUtils
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 *
 * @author sarhatabaot
 */

fun ItemStack.matchesEntry(packEntry: PackEntry): Boolean {
    if (this.type == Material.AIR) return false

    val nbtItem = NBTItem(this)
    if (!isCard()) {
        return false
    }

    //don't count if it's shiny.
    if (NbtUtils.Card.isShiny(nbtItem)) {
        return false
    }

    val nbtRarity = NbtUtils.Card.getRarityId(nbtItem)
    val nbtSeries = NbtUtils.Card.getSeriesId(nbtItem)

    return packEntry.seriesId == nbtSeries && packEntry.rarityId == nbtRarity
}

fun ItemStack.isCard(): Boolean {
    if (this.type.isAir)
        return false
    val nbtItem = NBTItem(this)
    return NbtUtils.Card.isCard(nbtItem)
}