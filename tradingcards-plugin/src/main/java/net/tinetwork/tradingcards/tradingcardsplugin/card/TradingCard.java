package net.tinetwork.tradingcards.tradingcardsplugin.card;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.api.card.Card;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TradingCard extends Card<TradingCard> {
    private final TradingCards plugin;

    public TradingCard(TradingCards plugin, String cardName) {
        super(cardName);
        this.plugin = plugin;
    }

    @Override
    public TradingCard get() {
        return this;
    }

    @Override
    public ItemStack buildItem() {
        ItemStack card = plugin.getGeneralConfig().blankCard().clone();
        ItemMeta cardMeta = card.getItemMeta();
        cardMeta.setDisplayName(formatDisplayName(isPlayerCard(), isShiny(), getPrefix(), getRarityColour(), getCardName().replace('_', ' '), getCost(), getShinyPrefix()));
        cardMeta.setLore(formatLore());
        if (isShiny()) {
            cardMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        }

        cardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);


        card.setItemMeta(cardMeta);

        return card;
    }

    private List<String> formatLore() {
        List<String> lore = new ArrayList<>();
        lore.add(plugin.cMsg(getType().getColour() + getType().getDisplay() + ": &f" + getType().getName()));
        if (!"None".equals(getInfo().getName()) && !"".equals(getInfo().getName())) {
            lore.add(plugin.cMsg(getInfo().getColour() + getInfo().getDisplay() + ":"));
            lore.addAll(plugin.wrapString(getInfo().getName()));
        } else {
            lore.add(plugin.cMsg(getInfo().getColour() + getInfo().getDisplay() + ": &f" + getInfo().getName()));
        }

        lore.add(plugin.cMsg(getSeries().getColour() + getSeries().getDisplay() + ": &f" + getSeries().getName()));
        if (getAbout()!=null) {
            lore.add(plugin.cMsg(getAbout().getColour() + getAbout().getDisplay() + ": &f" + getAbout().getName()));
        }

        if (isShiny()) {
            lore.add(plugin.cMsg(getRarityColour() + ChatColor.BOLD + plugin.getConfig().getString("General.Shiny-Name") + " " + getRarity().replace('_', ' ')));
        } else {
            lore.add(plugin.cMsg(getRarityColour() + ChatColor.BOLD + getRarity().replace('_', ' ')));
        }

        return lore;
    }

    @NotNull
    private String formatDisplayName(boolean isPlayerCard, boolean isShiny, String prefix, String rarityColour, String cardName, String cost, String shinyPrefix) {
        final String[] shinyPlayerCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "%SHINYPREFIX%"};
        final String[] shinyCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "%SHINYPREFIX%", "_"};

        final String[] cardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "_"};
        final String[] playerCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%"};


        final String shinyTitle = plugin.getConfig().getString("DisplayNames.Cards.ShinyTitle");
        final String title = plugin.getConfig().getString("DisplayNames.Cards.Title");
        if (isShiny && shinyPrefix != null) {
            if (isPlayerCard) {
                return plugin.cMsg(StringUtils.replaceEach(shinyTitle, shinyPlayerCardFormat, new String[]{prefix, rarityColour, cardName, cost, shinyPrefix}));
            }
            return plugin.cMsg(StringUtils.replaceEach(shinyTitle, shinyCardFormat, new String[]{prefix, rarityColour, cardName, cost, shinyPrefix, " "}));
        }
        if (isPlayerCard) {
            return plugin.cMsg(StringUtils.replaceEach(title, playerCardFormat, new String[]{prefix, rarityColour, cardName, cost}));
        }
        return plugin.cMsg(StringUtils.replaceEach(title, cardFormat, new String[]{prefix, rarityColour, cardName, cost, " "}));
    }

}
