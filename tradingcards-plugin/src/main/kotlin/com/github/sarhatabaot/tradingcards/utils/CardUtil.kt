package com.github.sarhatabaot.tradingcards.utils

import com.github.sarhatabaot.tradingcards.card.TradingCard
import me.clip.placeholderapi.PlaceholderAPI
import net.tinetwork.tradingcards.api.model.Rarity
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil

/**
 * @author sarhatabaot
 */
private lateinit var plugin: TradingCards //todo, inject.

fun TradingCard.formatTitle(): String {
    val rarity: Rarity = this.rarity
    var shinyTitle = plugin.generalConfig.displayShinyTitle()
        .replace(PlaceholderUtil.SHINY_PREFIX_ALT.asRegex().toRegex(), PlaceholderUtil.SHINY_PREFIX.placeholder)
    var title = plugin.generalConfig.displayTitle()
    val shinyPrefix = plugin.generalConfig.shinyName()
    val prefix = plugin.generalConfig.cardPrefix()
    val rarityColor = rarity.defaultColor
    val buyPrice: String = this.buyPrice.toString()
    val sellPrice: String = this.sellPrice.toString()

    if (this.isShiny && shinyPrefix != null) {
        shinyTitle = ChatUtil.color(
            shinyTitle.replace(PlaceholderUtil.PREFIX.asRegex().toRegex(), prefix)
                .replace(PlaceholderUtil.SHINY_PREFIX.asRegex().toRegex(), shinyPrefix)
                .replace(PlaceholderUtil.COLOR.asRegex().toRegex(), rarityColor)
                .replace(PlaceholderUtil.DISPLAY_NAME.asRegex().toRegex(), this.displayName)
                .replace(PlaceholderUtil.BUY_PRICE.asRegex().toRegex(), buyPrice)
                .replace(PlaceholderUtil.SELL_PRICE.asRegex().toRegex(), sellPrice)
        )
        return if (this.isPlayerCard) {
            shinyTitle
        } else shinyTitle.replace("_", " ")
    }

    title = ChatUtil.color(
        title.replace(PlaceholderUtil.PREFIX.asRegex().toRegex(), prefix)
            .replace(PlaceholderUtil.COLOR.asRegex().toRegex(), rarityColor)
            .replace(PlaceholderUtil.DISPLAY_NAME.asRegex().toRegex(), this.displayName)
            .replace(PlaceholderUtil.BUY_PRICE.asRegex().toRegex(), buyPrice)
            .replace(PlaceholderUtil.SELL_PRICE.asRegex().toRegex(), sellPrice)
    )
    return if (this.isPlayerCard) {
        title
    } else title.replace("_", " ")
}

fun TradingCard.formatDisplayName(): String {
    val finalTitle = formatTitle()

    return if (plugin.placeholderapi()) {
        PlaceholderAPI.setPlaceholders(null, finalTitle)
    } else finalTitle
}