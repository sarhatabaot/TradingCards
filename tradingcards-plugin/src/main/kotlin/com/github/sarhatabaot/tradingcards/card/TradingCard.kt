package com.github.sarhatabaot.tradingcards.card

import net.tinetwork.tradingcards.api.card.Card
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalExceptions
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


class TradingCard : Card<TradingCard> {
    constructor(cardName: String, defaultMaterial: Material) : super(cardName) {
        material(defaultMaterial)
    }

    constructor(card: TradingCard) : super(card)

    override fun get(): TradingCard {
        return this
    }

    override fun buildItem(shiny: Boolean): ItemStack {
        val cardItemStack = ItemStack(material)
        val cardMeta = cardItemStack.itemMeta ?: throw NullPointerException(InternalExceptions.NO_ITEM_META)
        cardMeta.setDisplayName(CardUtil.formatDisplayName(this))
        cardMeta.lore = CardUtil.formatLore(
            info,
            about,
            rarity.displayName,
            shiny,
            type.displayName,
            series
        )
        if (shiny) {
            cardMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false)
        }
        cardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        cardItemStack.setItemMeta(cardMeta)
        return cardItemStack
    }
}
