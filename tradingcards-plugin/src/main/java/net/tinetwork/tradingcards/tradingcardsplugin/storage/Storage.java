package net.tinetwork.tradingcards.tradingcardsplugin.storage;

import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public interface Storage {

    /**
     *
     * @param playerUuid Player UUID
     * @return a List of decks the player has.
     */
    List<Deck> getPlayerDecks(UUID playerUuid);

    /**
     *
     * @param playerUuid Player UUID
     * @param deckNumber Deck number, must be larger than 1
     * @return Return a corresponding deck object
     */
    Deck getDeck(UUID playerUuid, int deckNumber);

    /**
     * Saves the deck to storage.
     * @param playerUuid Player UUID
     * @param deckNumber Deck number, must be larger than 1
     * @param deck Corresponding deck object.
     */
    void save(UUID playerUuid, int deckNumber, Deck deck);

    /**
     *
     * @param playerUuid
     * @param card
     * @param rarity
     * @return
     */
    boolean hasCard(UUID playerUuid, String card, String rarity);

    /**
     *
     * @param playerUuid
     * @param card
     * @param rarity
     * @return
     */
    boolean hasShinyCard(UUID playerUuid, String card, String rarity);

    /**
     * Returns the storage type used.
     * @return StorageType
     */
    StorageType getType();

    default String getPrefix() {
        return "decks";
    }

    void init(TradingCards plugin);

    @Nullable
    Rarity getRarityById(final String rarityId);
    List<Rarity> getRarities();
    List<String> getRewards(final String rarityId);

    Series getSeries(final String seriesId);
    ColorSeries getColorSeries(final String seriesId);

    /**
     * Only for flatfile storage types,
     * Just YAML for now.
     */
    void reload();
}
