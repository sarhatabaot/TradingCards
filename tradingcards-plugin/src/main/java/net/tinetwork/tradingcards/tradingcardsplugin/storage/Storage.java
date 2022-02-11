package net.tinetwork.tradingcards.tradingcardsplugin.storage;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public interface Storage<T extends Card<T>> {

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
     * @param playerUuid Player UUID
     * @param card The card id
     * @param rarity The rarity id
     * @return Returns if the player has this card in a deck. Will return true if the card is shiny.
     */
    boolean hasCard(UUID playerUuid, String card, String rarity);

    /**
     *
     * @param playerUuid Player UUID
     * @param card The card id
     * @param rarity The rarity id
     * @return Returns if the player has this shiny card in a deck.
     */
    boolean hasShinyCard(UUID playerUuid, String card, String rarity);

    /**
     * Returns the storage type used.
     * @return StorageType
     */
    StorageType getType();


    /**
     * Initializes this storage type.
     * Usually, caches the lists.
     * @param plugin The plugin.
     */
    void init(TradingCards plugin);

    @Nullable
    Rarity getRarityById(final String rarityId);
    List<Rarity> getRarities();
    List<String> getRewards(final String rarityId);

    /**
     * @param seriesId Series id.
     * @return Returns a series object.
     */
    Series getSeries(final String seriesId);
    ColorSeries getColorSeries(final String seriesId);
    Collection<Series> getAllSeries();
    Map<String, T> getCardsMap();
    List<T> getCards();
    Pack getPack(final String packsId);
    List<Pack> getPacks();
    Set<DropType> getDropTypes();

    /**
     * Only for flatfile storage types,
     * Just YAML for now.
     */
    void reload();
}
