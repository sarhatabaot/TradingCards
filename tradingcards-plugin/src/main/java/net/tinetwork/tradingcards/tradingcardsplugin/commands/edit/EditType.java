package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

/**
 * @author sarhatabaot
 */
public enum EditType implements Edit{
    TYPE,
    DISPLAY_NAME;


    @Override
    public String editName() {
        return "custom type";
    }
}
