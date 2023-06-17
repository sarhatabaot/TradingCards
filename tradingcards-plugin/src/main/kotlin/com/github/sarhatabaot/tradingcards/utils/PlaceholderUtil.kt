package com.github.sarhatabaot.tradingcards.utils

import org.jetbrains.annotations.Contract

/**
 * @author sarhatabaot
 */

object PlaceholderUtil {

    @JvmRecord
    data class InternalPlaceholder(val placeholder: String) {
        @Contract(pure = true)
        fun asRegex(): String {
            return "(?i)$placeholder"
        }
    }


    var PLAYER = InternalPlaceholder("%player%")
    var PACK = InternalPlaceholder("%pack%")
    var CARD = InternalPlaceholder("%card%")
    var DISPLAY_NAME = InternalPlaceholder("%name%")
    var UUID = InternalPlaceholder("%uuid%")
    var RARITY = InternalPlaceholder("%rarity%")
    var SERIES = InternalPlaceholder("%series%")
    var BUY_AMOUNT = InternalPlaceholder("%buyamount%")
    var SELL_AMOUNT = InternalPlaceholder("%sellamount%")
    var PREFIX = InternalPlaceholder("%prefix%")
    var COLOR = InternalPlaceholder("%color%")
    var BUY_PRICE = InternalPlaceholder("%buy_price%")
    var SELL_PRICE = InternalPlaceholder("%sell_price%")
    var SHINY_PREFIX = InternalPlaceholder("%shiny_prefix%")
    var SHINY_PREFIX_ALT = InternalPlaceholder("%shinyprefix%")
    var AMOUNT = InternalPlaceholder("%amount%")
    var CARDS_OWNED = InternalPlaceholder("%cards_owned%")
    var SHINY_CARDS_OWNED = InternalPlaceholder("%shiny_cards_owned%")
    var CARDS_TOTAL = InternalPlaceholder("%cards_total%")

}
