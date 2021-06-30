package net.tinetwork.tradingcards.tradingcardsapi.card;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public abstract class Card {
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

    public Card isShiny(boolean isShiny) {
        this.isShiny = isShiny;
        return this;
    }

    public Card rarityColour(String rarityColour) {
        this.rarityColour = rarityColour;
        return this;
    }

    public Card prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public Card series(String name, String colour, String display) {
        this.series = new CardInfo(name, colour, display);
        return this;
    }

    public Card about(String name, String colour, String display) {
        this.about = new CardInfo(name, colour, display);
        return this;
    }

    public Card type(String name, String colour, String display) {
        this.type = new CardInfo(name, colour, display);
        return this;
    }

    public Card info(String name, String colour, String display) {
        this.info = new CardInfo(name, colour, display);
        return this;
    }

    public Card shinyPrefix(String shinyPrefix) {
        this.shinyPrefix = shinyPrefix;
        return this;
    }

    public Card cost(String cost) {
        this.cost = cost;
        return this;
    }

    public Card rarity(String rarity) {
        this.rarity = rarity;
        return this;
    }

    public Card isPlayerCard(boolean isPlayerCard) {
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
        return nbtItem.getItem();
    }


}
