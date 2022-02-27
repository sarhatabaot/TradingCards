package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

/**
 * @author sarhatabaot
 */
public enum EditPack implements Edit{
    PRICE,
    PERMISSION,
    DISPLAY_NAME,
    CONTENTS;

    @Override
    public String editName() {
        return "pack";
    }
}
