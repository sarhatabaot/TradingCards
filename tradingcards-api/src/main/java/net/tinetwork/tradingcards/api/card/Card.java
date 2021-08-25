package net.tinetwork.tradingcards.api.card;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public abstract class Card<T> {
    private final String cardName;
    private String displayName;
    private String rarity;
    private boolean isShiny = false;
    private boolean isPlayerCard = false;
    private String series;
    private String about;
    private String type;
    private String info;
    private int customModelNbt;
    private double buyPrice;
    private double sellPrice;

    private NBTItem nbtItem;

    public Card(final String cardName) {
        this.cardName = cardName;
    }

    /**
     * Set if a card is shiny
     * @param isShiny
     * @return
     */
    public Card<T> isShiny(boolean isShiny) {
        this.isShiny = isShiny;
        return this;
    }

    public Card<T> displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set custom model nbt
     * @param data custom model nbt
     * @return Builder
     */
    public Card<T> customModelNbt(final int data) {
        this.customModelNbt(data);
        return this;
    }


    public Card<T> series(String name) {
        this.series = name;
        return this;
    }

    public Card<T>  about(String name) {
        this.about = name;
        return this;
    }

    public Card<T>  type(String name) {
        this.type = name;
        return this;
    }

    public Card<T>  info(String name) {
        this.info = name;
        return this;
    }


    public Card<T>  buyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
        return this;
    }

    public Card<T>  sellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
        return this;
    }

    public Card<T>  rarity(String rarity) {
        this.rarity = rarity;
        return this;
    }

    public Card<T>  isPlayerCard(boolean isPlayerCard) {
        this.isPlayerCard = isPlayerCard;
        return this;
    }

    public String getCardName() {
        return cardName;
    }

    public String getRarity() {
        return rarity;
    }

    public boolean isShiny() {
        return isShiny;
    }

    public boolean isPlayerCard() {
        return isPlayerCard;
    }

    public String getSeries() {
        return series;
    }

    public String getAbout() {
        return about;
    }

    public String getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public abstract T get();

    public NBTItem buildNBTItem() {
        NBTItem nbtItem = new NBTItem(buildItem());
        nbtItem.setString("name",cardName);
        nbtItem.setString("rarity",rarity);
        nbtItem.setBoolean("isCard", true);
        nbtItem.setBoolean("isShiny",isShiny);
        nbtItem.setString("series",series);
        nbtItem.setString("rarity",rarity);
        nbtItem.setInteger("CustomModelData", customModelNbt);
        this.nbtItem = nbtItem;
        return nbtItem;
    }

    public int getCustomModelNbt() {
        return customModelNbt;
    }

    public abstract ItemStack buildItem();


    public ItemStack build() {
        if(nbtItem == null)
            nbtItem = buildNBTItem();
        return nbtItem.getItem();
    }


}
