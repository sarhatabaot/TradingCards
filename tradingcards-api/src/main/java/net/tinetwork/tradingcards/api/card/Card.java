package net.tinetwork.tradingcards.api.card;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Card<T>{
    private final String cardId;

    private Material material;
    private Rarity rarity;
    private DropType type;
    private Series series;

    private boolean hasShiny = false;

    //CardMeta
    private final CardMeta cardMeta;

    protected Card(final String cardId) {
        this.cardId = cardId;
        this.cardMeta = new CardMeta();
    }

    protected Card(final @NotNull Card<T> card) {
        this.cardId = card.getCardId();
        this.material = card.getMaterial();
        this.type = card.getType();
        this.series = card.getSeries();
        this.hasShiny = card.hasShiny();
        this.rarity = card.getRarity();

        this.cardMeta = card.getCardMeta();
    }

    /**
     * Set if a card is shiny
     * @param isShiny isShiny
     * @return if the card is shiny
     */
    // A Card object cannot be shiny. It can have a "shiny" version. But that's purely a cosmetic/nbt.
    // Since this is a generated type.
    public Card<T> isShiny(boolean isShiny) {
        this.cardMeta.setShiny(isShiny);
        return this;
    }

    public Card<T> hasShiny(boolean hasShiny) {
        this.hasShiny = hasShiny;
        return this;
    }

    public Card<T> displayName(String displayName) {
        this.cardMeta.setDisplayName(displayName);
        return this;
    }

    public String getDisplayName() {
        if(this.cardMeta.getDisplayName() == null || this.cardMeta.getDisplayName().isEmpty())
            return cardId.replace("_"," ");
        return this.cardMeta.getDisplayName();
    }

    /**
     * Set custom model nbt
     * @param data custom model nbt
     * @return Builder
     */
    public Card<T> customModelNbt(final int data) {
        this.cardMeta.setCustomModelNbt(data);
        return this;
    }

    public Card<T> material(final Material material) {
        this.material = material;
        return this;
    }


    public Card<T> series(Series name) {
        this.series = name;
        return this;
    }

    public Card<T>  about(String about) {
        this.cardMeta.setAbout(about);
        return this;
    }

    public Card<T>  type(DropType dropType) {
        this.type = dropType;
        return this;
    }

    public Card<T>  info(String info) {
        this.cardMeta.setInfo(info);
        return this;
    }


    public Card<T>  buyPrice(double buyPrice) {
        this.cardMeta.setBuyPrice(buyPrice);
        return this;
    }

    public Card<T>  sellPrice(double sellPrice) {
        this.cardMeta.setSellPrice(sellPrice);
        return this;
    }

    public Card<T>  rarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public Card<T>  isPlayerCard(boolean isPlayerCard) {
        this.cardMeta.setPlayerCard(isPlayerCard);
        return this;
    }

    public String getCardId() {
        return cardId;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public boolean isShiny() {
        return this.cardMeta.isShiny();
    }

    public boolean hasShiny() {
        return hasShiny;
    }

    public boolean isPlayerCard() {
        return this.cardMeta.isPlayerCard();
    }

    public Series getSeries() {
        return series;
    }

    public String getAbout() {
        return this.cardMeta.getAbout();
    }

    public DropType getType() {
        return type;
    }

    public String getInfo() {
        return this.cardMeta.getInfo();
    }

    public double getBuyPrice() {
        return this.cardMeta.getBuyPrice();
    }

    public double getSellPrice() {
        return this.cardMeta.getSellPrice();
    }

    public Material getMaterial() {
        return material;
    }

    public abstract T get();

    public NBTItem buildNBTItem() {
        return buildNBTItem(this.cardMeta.isShiny());
    }

    public NBTItem buildNBTItem(boolean shiny) {
        NBTItem nbtItem = new NBTItem(buildItem(shiny));
        nbtItem.setString(NbtUtils.NBT_CARD_NAME, cardId);
        nbtItem.setString(NbtUtils.NBT_RARITY,rarity.getId());
        nbtItem.setBoolean(NbtUtils.NBT_IS_CARD, true);
        nbtItem.setBoolean(NbtUtils.NBT_CARD_SHINY, shiny);
        nbtItem.setString(NbtUtils.NBT_CARD_SERIES,series.getId());
        nbtItem.setInteger(NbtUtils.NBT_CARD_CUSTOM_MODEL, this.cardMeta.getCustomModelNbt());
        return nbtItem;
    }

    protected CardMeta getCardMeta() {
        return cardMeta;
    }

    public int getCustomModelNbt() {
        return this.cardMeta.getCustomModelNbt();
    }

    public abstract ItemStack buildItem(boolean shiny);

    public ItemStack build(boolean shiny) {
        NBTItem nbtItem = buildNBTItem(shiny);
        return nbtItem.getItem().clone();
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardName='" + cardId + '\'' +
                ", material=" + material +
                ", rarity=" + rarity +
                ", type=" + type +
                ", series=" + series +
                ", hasShiny=" + hasShiny +
                ", cardMeta=" + cardMeta +
                '}';
    }
}
