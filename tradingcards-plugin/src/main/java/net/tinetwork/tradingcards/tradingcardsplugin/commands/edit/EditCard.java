package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

public enum EditCard implements Edit{
    DISPLAY_NAME,
    CUSTOM_MODEL_DATA,
    BUY_PRICE,
    SELL_PRICE,
    INFO,
    SERIES,
    TYPE;


    @Override
    public String editName() {
        return "card";
    }
}
