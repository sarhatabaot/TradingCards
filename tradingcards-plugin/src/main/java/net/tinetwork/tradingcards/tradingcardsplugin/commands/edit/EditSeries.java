package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

/**
 * @author sarhatabaot
 */
public enum EditSeries implements Edit{
    DISPLAY_NAME,
    MODE,
    COLORS;


    @Override
    public String editName() {
        return "series";
    }
}
