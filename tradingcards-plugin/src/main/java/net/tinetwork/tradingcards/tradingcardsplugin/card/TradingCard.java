package net.tinetwork.tradingcards.tradingcardsplugin.card;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradingCard extends Card<TradingCard> {

    public TradingCard(String cardName) {
        super(cardName);
    }

    public TradingCard(final TradingCard card) {
        super(card);
    }

    @Override
    public TradingCard get() {
        return this;
    }

    @Override
    public ItemStack buildItem() {
        ItemStack card = new ItemStack(getMaterial());
        ItemMeta cardMeta = card.getItemMeta();
        cardMeta.setDisplayName(CardUtil.formatDisplayName(this));
        cardMeta.setLore(CardUtil.formatLore(getInfo(),getAbout(),getRarity().getDisplayName(),isShiny(),getType().getDisplayName(),getSeries()));
        if (isShiny()) {
            cardMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        }
        cardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        card.setItemMeta(cardMeta);

        return card;
    }

}
