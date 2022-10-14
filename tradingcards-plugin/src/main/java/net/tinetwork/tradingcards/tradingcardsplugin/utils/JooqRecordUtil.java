package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.CustomTypes;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Decks;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Packs;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.PacksContent;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.SeriesColors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class JooqRecordUtil {
    public static @NotNull Series getSeriesFromRecord(final @NotNull Record recordResult, final ColorSeries colorSeries) {
        final String seriesId = recordResult.getValue(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_ID);
        final String displayName = recordResult.getValue(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.DISPLAY_NAME);
        final Mode mode = Mode.getMode(recordResult.getValue(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series.SERIES.SERIES_MODE).getLiteral());
        return new Series(seriesId, mode, displayName, null, colorSeries);
    }

    public static @NotNull PackEntry getPackEntryFromResult(@NotNull Record recordResult) {
        final String rarityId = recordResult.getValue(PacksContent.PACKS_CONTENT.RARITY_ID);
        final String seriesId = recordResult.getValue(PacksContent.PACKS_CONTENT.SERIES_ID);
        final int cardAmount = Integer.parseInt(recordResult.getValue(PacksContent.PACKS_CONTENT.CARD_AMOUNT));
        return new PackEntry(rarityId, cardAmount, seriesId);
    }

    public static @NotNull Pack getPackFromRecord(@NotNull Record recordResult, final List<PackEntry> entries, final List<PackEntry> tradeEntries) {
        final String packId = recordResult.getValue(Packs.PACKS.PACK_ID);
        final String displayName = recordResult.getValue(Packs.PACKS.DISPLAY_NAME);
        final double price = (recordResult.getValue(Packs.PACKS.BUY_PRICE) == null) ? 0.00D : recordResult.getValue(Packs.PACKS.BUY_PRICE);
        final String permission = recordResult.getValue(Packs.PACKS.PERMISSION);
        final String currencyId = recordResult.getValue(Packs.PACKS.CURRENCY_ID);
        return new Pack(packId, entries, displayName, price, currencyId,permission, tradeEntries);
    }
    public static @NotNull ColorSeries getColorSeriesFromRecord(@NotNull Record recordResult) {
        final String about = recordResult.getValue(SeriesColors.SERIES_COLORS.ABOUT);
        final String info = recordResult.getValue(SeriesColors.SERIES_COLORS.INFO);
        final String type = recordResult.getValue(SeriesColors.SERIES_COLORS.TYPE);
        final String rarity = recordResult.getValue(SeriesColors.SERIES_COLORS.RARITY);
        final String series = recordResult.getValue(SeriesColors.SERIES_COLORS.SERIES);
        return new ColorSeries(series, type, info, about, rarity);
    }


    public static @NotNull DropType getDropTypeFromRecord(@NotNull Record recordResult) {
        final String typeId = recordResult.getValue(CustomTypes.CUSTOM_TYPES.TYPE_ID);
        final String type = recordResult.getValue(CustomTypes.CUSTOM_TYPES.DROP_TYPE).getLiteral();
        final String displayName = recordResult.getValue(CustomTypes.CUSTOM_TYPES.DISPLAY_NAME);
        return new DropType(typeId, displayName, type);
    }


    @Contract("_ -> new")
    public static @NotNull Deck getDeckFromRecord(@NotNull Result<Record> result) {
        final String playerUuid = result.getValue(0, Decks.DECKS.UUID);
        final int deckNumber = result.getValue(0,Decks.DECKS.DECK_NUMBER);
        List<StorageEntry> entries = new ArrayList<>();
        for(Record recordResult: result) {
            final String cardId = recordResult.getValue(Decks.DECKS.CARD_ID);
            final String rarityId = recordResult.getValue(Decks.DECKS.RARITY_ID);
            final boolean isShiny = recordResult.getValue(Decks.DECKS.IS_SHINY);
            final int amount = recordResult.getValue(Decks.DECKS.AMOUNT);
            final String seriesId = recordResult.getValue(Decks.DECKS.SERIES_ID);
            entries.add(new StorageEntry(rarityId, cardId, amount, isShiny,seriesId));
        }
        return new Deck(UUID.fromString(playerUuid), deckNumber, entries);
    }

    @NotNull
    public static <T> T getOrDefault(T value, T defaultValue) {
        if (value == null)
            return defaultValue;
        return value;
    }
}
