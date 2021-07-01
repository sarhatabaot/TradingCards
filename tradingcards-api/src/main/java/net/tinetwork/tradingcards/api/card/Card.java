package net.tinetwork.tradingcards.api.card;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public abstract class Card<T> {
    private final String cardName;
    private String rarity;
    private boolean isShiny = false;
    private boolean isPlayerCard = false;
    private String rarityColour;
    private String prefix;
    private CardInfo series;
    private CardInfo about;
    private CardInfo type;
    private CardInfo info;
    private String shinyPrefix = null;
    private String cost;
    private double buyPrice;
    private double sellPrice;

    private NBTItem nbtItem;

    public Card(final String cardName) {
        this.cardName = cardName;
    }

    public Card<T> isShiny(boolean isShiny) {
        this.isShiny = isShiny;
        return this;
    }

    public Card<T>  rarityColour(String rarityColour) {
        this.rarityColour = rarityColour;
        return this;
    }

    public Card<T>  prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public Card<T>  series(String name, String colour, String display) {
        this.series = new CardInfo(name, colour, display);
        return this;
    }

    public Card<T>  about(String name, String colour, String display) {
        this.about = new CardInfo(name, colour, display);
        return this;
    }

    public Card<T>  type(String name, String colour, String display) {
        this.type = new CardInfo(name, colour, display);
        return this;
    }

    public Card<T>  info(String name, String colour, String display) {
        this.info = new CardInfo(name, colour, display);
        return this;
    }

    public Card<T>  shinyPrefix(String shinyPrefix) {
        this.shinyPrefix = shinyPrefix;
        return this;
    }

    public Card<T>  cost(String cost) {
        this.cost = cost;
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

    public String getRarityColour() {
        return rarityColour;
    }

    public String getPrefix() {
        return prefix;
    }

    public CardInfo getSeries() {
        return series;
    }

    public CardInfo getAbout() {
        return about;
    }

    public CardInfo getType() {
        return type;
    }

    public CardInfo getInfo() {
        return info;
    }

    public String getShinyPrefix() {
        return shinyPrefix;
    }

    public String getCost() {
        return cost;
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
        nbtItem.setString("series",series.getName());
        nbtItem.setString("rarity",rarity);
        this.nbtItem = nbtItem;
        return nbtItem;
    }

    public abstract ItemStack buildItem();


    public ItemStack build() {
        if(nbtItem == null)
            nbtItem = buildNBTItem();
        return nbtItem.getItem();
    }


}
