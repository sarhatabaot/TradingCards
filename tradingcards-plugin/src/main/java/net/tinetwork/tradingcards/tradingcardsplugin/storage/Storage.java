package net.tinetwork.tradingcards.tradingcardsplugin.storage;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
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
     * @param playerUuid Player UUID
     * @return a List of decks the player has.
     */
    List<Deck> getPlayerDecks(UUID playerUuid);

    /**
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
    void saveDeck(UUID playerUuid, int deckNumber, Deck deck);

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

    /**
     * Get the rarity object by the id.
     * @param rarityId The rarity id.
     * @return Returns a null if it doesn't exist.
     */
    @Nullable
    Rarity getRarityById(final String rarityId);

    /**
     * @return Returns a list of all rarities.
     */
    List<Rarity> getRarities();

    /**
     * @param rarityId The rarity id. As defined in rarities.
     * @return A list of rewards associated with rarityId
     */
    List<String> getRewards(final String rarityId);

    /**
     * @param seriesId Series id.
     * @return Returns a series object.
     */
    Series getSeries(final String seriesId);

    /**
     * @return Returns a collection of all series
     */
    Collection<Series> getAllSeries();

    /**
     * @return Returns a set of all "active" series.
     */
    Set<Series> getActiveSeries();

    /* TODO
     * @return Returns a <CardsKey, Card> map CardsKey should only be for yaml
     * This should be used only in yaml storage, and via getCard();
     */
    @Deprecated
    Map<String, T> getCardsMap();
    @Deprecated
    Map<String, T> getActiveCardsMap();

    /**
     * @return A list of all cards.
     */
    List<T> getCards();

    /**
     * @param rarityId The rarity id.
     * @return A list of all cards in a rarity.
     */
    List<T> getCardsInRarity(final String rarityId);

    /**
     * @param seriesId The series id.
     * @return A list of all cards in a series.
     */
    List<T> getCardsInSeries(final String seriesId);

    List<T> getCardsInRarityAndSeries(final String rarityId, final String seriesId);

    /**
     * @return A list of all active cards.
     */
    List<T> getActiveCards();

    /**
     * @param cardId The card id.
     * @param rarityId The rarity id.
     * @return Returns a card.
     */
    Card<T> getCard(final String cardId, final String rarityId);

    /**
     * @param cardId The card id.
     * @param rarityId The rarity id.
     * @return Returns an active card.
     */
    Card<T> getActiveCard(final String cardId, final String rarityId);

    /**
     * @param packsId The pack id.
     * @return Returns the pack. Will return null if it doesn't exist.
     */
    @Nullable
    Pack getPack(final String packsId);

    /**
     * @return Returns a list of all packs.
     */
    List<Pack> getPacks();

    /**
     * @return Returns a set of all drop types.
     */
    Set<DropType> getDropTypes();

    /**
     * Only for flatfile storage types,
     * Just YAML for now.
     */
    void reload();

    DropType getCustomType(final String typeId);

    //Create
    void createCard(final String cardId, final String rarityId, final String seriesId);
    void createRarity(final String rarityId);
    void createSeries(final String seriesId);
    void createCustomType(final String typeId, final String type);
    void createPack(final String packId);
    // Edit
    // Edit Card
    void editCardDisplayName(final String rarityId, final String cardId, final String seriesId, final String displayName);
    void editCardSeries(final String rarityId, final String cardId, final String seriesId, final Series value);
    void editCardSellPrice(final String rarityId, final String cardId, final String seriesId, final double value);
    void editCardType(final String rarityId,final String cardId, final String seriesId, final DropType value);
    void editCardInfo(final String rarityId,final String cardId, final String seriesId, final String value);
    void editCardCustomModelData(final String rarityId,final String cardId,final String seriesId, final int value);
    void editCardBuyPrice(final String rarityId,final String cardId, final String seriesId,final double value);
    // Edit Rarity
    void editRarityBuyPrice(final String rarityId, final double buyPrice);
    void editRarityAddReward(final String rarityId, final String reward);
    void editRarityDefaultColor(final String rarityId, final String defaultColor);
    void editRarityDisplayName(final String rarityId, final String displayName);
    void editRaritySellPrice(final String rarityId, final double sellPrice);
    void editRarityRemoveAllRewards(final String rarityId);
    void editRarityRemoveReward(final String rarityId, final int rewardNumber);
    // Edit Series
    void editSeriesDisplayName(final String seriesId, final String displayName);
    void editSeriesColors(final String seriesId, final ColorSeries colors);
    void editSeriesMode(final String seriesId, final Mode mode);
    // Edit Type
    void editCustomTypeDisplayName(final String typeId, final String displayName);
    void editCustomTypeType(final String typeId, final String type); //It has to be a default type.
    // Edit Pack
    void editPackDisplayName(final String packId, final String displayName);
    void editPackContents(final String packId, final int lineNumber, final Pack.PackEntry packEntry);
    void editPackPermission(final String packId, final String permission);
    void editPackPrice(final String packId,final double price);
}
