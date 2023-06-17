package com.github.sarhatabaot.tradingcards.card

import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author sarhatabaot
 */
class EmptyCard: TradingCard(
    "tc-internal-null-card",
    Material.AIR
) {
    override fun buildItem(shiny: Boolean): ItemStack = ItemStack(material)
}