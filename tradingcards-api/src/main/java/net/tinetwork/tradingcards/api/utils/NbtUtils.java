package net.tinetwork.tradingcards.api.utils;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author sarhatabaot
 */
/*
 IMPORTANT: !!Remember, if you change anything in this class items won't stack
 properly anymore.!!
 */
public class NbtUtils {
    public static final String NBT_CARD_CUSTOM_MODEL = "CustomModelData";
    public static final String TC_COMPOUND = "trading-cards";

    // Deck Stuff
    public static final String TC_DECK_NUMBER = "tc-deck-number";
    // Card Stuff
    public static final String TC_CARD_ID = "tc-card-id";
    public static final String TC_CARD_RARITY = "tc-card-rarity";
    public static final String TC_CARD_SHINY = "tc-card-shiny";
    public static final String TC_CARD_SERIES = "tc-card-series";

    public static final String TC_PACK_ID = "tc-pack-id";

    public static final String TC_SPAWNER_MOB = "tc-spawner-mob";

    private NbtUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isCardSimilar(final @NotNull NBTItem item1, final @NotNull NBTItem item2) {
        return isCardSimilar((ReadableNBT) item1, item2);
    }

    public static boolean isCardSimilar(final @NotNull ReadableNBT item1, final @NotNull ReadableNBT item2) {
        return Objects.equals(Card.isShiny(item1), Card.isShiny(item2)) &&
                Card.getCardId(item1).equals(Card.getCardId(item2)) &&
                Card.getRarityId(item1).equals(Card.getRarityId(item2)) &&
                Card.getSeriesId(item1).equals(Card.getSeriesId(item2));
    }

    public static boolean isCardSimilar(final @NotNull ItemStack item1, final @NotNull ItemStack item2) {
        return getItemNbt(item1, nbtItem1 -> getItemNbt(item2, nbtItem2 -> isCardSimilar(nbtItem1, nbtItem2)));
    }

    private static @Nullable ReadableNBT getTcCompound(final @NotNull ReadableNBT item) {
        return item.getCompound(TC_COMPOUND);
    }

    private static <T> T getItemNbt(final @NotNull ItemStack item, final @NotNull Function<ReadableItemNBT, T> reader) {
        return NBT.get(item, reader);
    }


    public static class Legacy {
        //Deck Item
        public static final String NBT_DECK_NUMBER = "deckNumber";
        public static final String NBT_IS_DECK = "isDeck"; //we should consider not having this at all

        //Card Item
        public static final String NBT_IS_CARD = "isCard"; //we should consider not having this at all
        public static final String NBT_CARD_NAME = "name";
        public static final String NBT_RARITY = "rarity";
        public static final String NBT_CARD_SHINY = "shiny";
        public static final String NBT_CARD_SERIES = "series";


        //Pack Item
        public static final String NBT_PACK = "pack";
        public static final String NBT_PACK_ID = "packId";

        private Legacy() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Deck {
        private Deck() {
            throw new UnsupportedOperationException();
        }

        public static int getDeckNumber(final @NotNull NBTItem item) {
            return getDeckNumber((ReadableNBT) item);
        }

        public static int getDeckNumber(final @NotNull ReadableNBT item) {
            if (isLegacy(item)) {
                return item.getInteger(Legacy.NBT_DECK_NUMBER);
            }
            ReadableNBT tcCompound = getTcCompound(item);
            if (tcCompound == null) {
                return 0;
            }

            return tcCompound.getInteger(TC_DECK_NUMBER);
        }

        public static int getDeckNumber(final @NotNull ItemStack item) {
            return getItemNbt(item, Deck::getDeckNumber);
        }

        public static boolean isDeck(final @NotNull NBTItem item) {
            return isDeck((ReadableNBT) item);
        }

        public static boolean isDeck(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.hasTag(Legacy.NBT_IS_DECK);

            ReadableNBT tcCompound = getTcCompound(item);
            return tcCompound != null && tcCompound.hasTag(TC_DECK_NUMBER);
        }

        public static boolean isDeck(final @NotNull ItemStack item) {
            return getItemNbt(item, Deck::isDeck);
        }
    }

    public static class Card {
        private Card() {
            throw new UnsupportedOperationException();
        }

        public static String getCardId(final @NotNull NBTItem item) {
            return getCardId((ReadableNBT) item);
        }

        public static String getCardId(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.getString(Legacy.NBT_CARD_NAME);

            ReadableNBT tcCompound = getTcCompound(item);
            if (tcCompound == null) {
                return null;
            }

            return tcCompound.getString(TC_CARD_ID);
        }

        public static String getCardId(final @NotNull ItemStack item) {
            return getItemNbt(item, Card::getCardId);
        }

        public static String getRarityId(final @NotNull NBTItem item) {
            return getRarityId((ReadableNBT) item);
        }

        public static String getRarityId(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.getString(Legacy.NBT_RARITY);

            ReadableNBT tcCompound = getTcCompound(item);
            if (tcCompound == null) {
                return null;
            }

            return tcCompound.getString(TC_CARD_RARITY);
        }

        public static String getRarityId(final @NotNull ItemStack item) {
            return getItemNbt(item, Card::getRarityId);
        }

        public static String getSeriesId(final @NotNull NBTItem item) {
            return getSeriesId((ReadableNBT) item);
        }

        public static String getSeriesId(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.getString(Legacy.NBT_CARD_SERIES);

            ReadableNBT tcCompound = getTcCompound(item);
            if (tcCompound == null) {
                return null;
            }

            return tcCompound.getString(TC_CARD_SERIES);
        }

        public static String getSeriesId(final @NotNull ItemStack item) {
            return getItemNbt(item, Card::getSeriesId);
        }

        public static boolean isShiny(final @NotNull NBTItem item) {
            return isShiny((ReadableNBT) item);
        }

        public static boolean isShiny(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.getBoolean(Legacy.NBT_CARD_SHINY);

            ReadableNBT tcCompound = getTcCompound(item);
            return tcCompound != null && tcCompound.getBoolean(TC_CARD_SHINY);
        }

        public static boolean isShiny(final @NotNull ItemStack item) {
            return getItemNbt(item, Card::isShiny);
        }

        public static boolean isCard(final @NotNull NBTItem item) {
            return isCard((ReadableNBT) item);
        }

        public static boolean isCard(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.getBoolean(Legacy.NBT_IS_CARD);

            ReadableNBT tcCompound = getTcCompound(item);
            return tcCompound != null && tcCompound.hasTag(TC_CARD_ID);
        }

        public static boolean isCard(final @NotNull ItemStack item) {
            return getItemNbt(item, Card::isCard);
        }
    }

    public static class Pack {
        private Pack() {
            throw new UnsupportedOperationException();
        }

        public static String getPackId(final @NotNull NBTItem item) {
            return getPackId((ReadableNBT) item);
        }

        public static String getPackId(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.getString(Legacy.NBT_PACK_ID);

            ReadableNBT tcCompound = getTcCompound(item);
            if (tcCompound == null) {
                return null;
            }

            return tcCompound.getString(TC_PACK_ID);
        }

        public static String getPackId(final @NotNull ItemStack item) {
            return getItemNbt(item, Pack::getPackId);
        }

        public static boolean isPack(final @NotNull NBTItem item) {
            return isPack((ReadableNBT) item);
        }

        public static boolean isPack(final @NotNull ReadableNBT item) {
            if (isLegacy(item))
                return item.getBoolean(Legacy.NBT_PACK);

            ReadableNBT tcCompound = getTcCompound(item);
            return tcCompound != null && tcCompound.hasTag(TC_PACK_ID);
        }

        public static boolean isPack(final @NotNull ItemStack item) {
            return getItemNbt(item, Pack::isPack);
        }
    }


    public static boolean isLegacy(final @NotNull NBTItem item) {
        return isLegacy((ReadableNBT) item);
    }

    public static boolean isLegacy(final @NotNull ReadableNBT item) {
        return !item.hasTag(TC_COMPOUND);
    }
}
