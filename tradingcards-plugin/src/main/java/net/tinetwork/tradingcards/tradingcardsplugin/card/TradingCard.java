package net.tinetwork.tradingcards.tradingcardsplugin.card;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

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
        Rarity rarity = null;
        try {
            rarity = plugin.getRaritiesConfig().getRarity(getRarity());
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
            return NullCard.AIR;
        }
        if(rarity == null) {
            return NullCard.AIR;
        }
        cardMeta.setDisplayName(formatDisplayName(isPlayerCard(), isShiny(), plugin.getGeneralConfig().cardPrefix(), rarity.getDefaultColor(), getCardName().replace('_', ' '), String.valueOf(getBuyPrice()), plugin.getGeneralConfig().shinyName()));
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
        final String typeFormat = ChatUtil.color(plugin.getGeneralConfig().colorType() + plugin.getGeneralConfig().displayType() + ": &f" + getType());
        final String infoFormat = ChatUtil.color(plugin.getGeneralConfig().colorInfo() + plugin.getGeneralConfig().displayInfo() + ": &f");
        final String seriesFormat = ChatUtil.color(plugin.getGeneralConfig().colorSeries()+ plugin.getGeneralConfig().displaySeries() + ": &f" + getSeries());
        final String aboutFormat = ChatUtil.color(plugin.getGeneralConfig().colorAbout()+ plugin.getGeneralConfig().displayAbout() + ": &f");
        final String rarityFormat = ChatUtil.color(plugin.getGeneralConfig().colorRarity() + ChatColor.BOLD);

        lore.add(typeFormat);
        if (!"None".equalsIgnoreCase(getInfo()) && !getInfo().isEmpty()) {
            lore.add(infoFormat);
            lore.addAll(plugin.wrapString(getInfo()));
        } else {
            lore.add(infoFormat + getInfo());
        }

        lore.add(seriesFormat);
        if (getAbout()!=null) {
            lore.add(aboutFormat + getAbout());
        }

        final String rarityName = getRarity().replace('_', ' ');
        if (isShiny()) {
            lore.add(rarityFormat + plugin.getGeneralConfig().shinyName() + " " + rarityName);
        } else {
            lore.add(rarityFormat + rarityName);
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
