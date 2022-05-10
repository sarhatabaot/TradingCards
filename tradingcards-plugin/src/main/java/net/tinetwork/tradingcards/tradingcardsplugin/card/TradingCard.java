package net.tinetwork.tradingcards.tradingcardsplugin.card;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalExceptions;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradingCard extends Card<TradingCard> {

    public TradingCard(String cardName, final Material defaultMaterial) {
        super(cardName);
        material(defaultMaterial);
    }

    public TradingCard(final TradingCard card) {
        super(card);
    }

    @Override
    public TradingCard get() {
        return this;
    }

    @Override
    public ItemStack buildItem(final boolean shiny) {
        ItemStack cardItemStack = new ItemStack(getMaterial());
        ItemMeta cardMeta = cardItemStack.getItemMeta();

        if(cardMeta == null)
            throw new NullPointerException(InternalExceptions.NO_ITEM_META);

        cardMeta.setDisplayName(CardUtil.formatDisplayName(this));

        cardMeta.setLore(CardUtil.formatLore(getInfo(),getAbout(),getRarity().getDisplayName(),shiny,getType().getDisplayName(),getSeries()));
        if (shiny) {
            cardMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        }
        cardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        cardItemStack.setItemMeta(cardMeta);

        return cardItemStack;
    }
}
