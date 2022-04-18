package net.tinetwork.tradingcards.api.utils;

import de.tr7zw.nbtapi.NBTItem;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author sarhatabaot
 */
/*
 IMPORTANT: !!Remember, if you change anything in this class items won't stack
 properly anymore.!!
 */
public class NbtUtils {
    public static final String NBT_DECK_NUMBER = "deckNumber";
    public static final String NBT_IS_DECK = "isDeck";
    public static final String NBT_IS_CARD = "isCard";
    public static final String NBT_CARD_NAME = "name";
    public static final String NBT_RARITY = "rarity";
    public static final String NBT_CARD_SHINY = "shiny";
    public static final String NBT_CARD_SERIES = "series";
    public static final String NBT_CARD_CUSTOM_MODEL = "CustomModelData";
    public static final String NBT_PACK = "pack";
    public static final String NBT_PACK_ID = "packId";


    public static boolean isCardSimilar(final @NotNull NBTItem item1, final @NotNull NBTItem item2) {
        return Objects.equals(item1.getBoolean(NBT_CARD_SHINY), item2.getBoolean(NBT_CARD_SHINY)) &&
                item1.getString(NBT_CARD_NAME).equals(item2.getString(NBT_CARD_NAME)) &&
                item1.getString(NBT_RARITY).equals(item2.getString(NBT_RARITY)) &&
                item1.getString(NBT_CARD_SERIES).equals(item2.getString(NBT_CARD_SERIES));
    }
}
