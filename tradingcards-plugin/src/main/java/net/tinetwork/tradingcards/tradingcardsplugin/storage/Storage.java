package net.tinetwork.tradingcards.tradingcardsplugin.storage;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.MobGroup;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
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
     * @param cardId The card id
     * @param rarityId The rarity id
     * @return Returns if the player has this card in a deck. Will return true if the card is shiny.
     */
    boolean hasCard(UUID playerUuid, String cardId, String rarityId, String seriesId);

    /**
     *
     * @param playerUuid Player UUID
     * @param card The card id
     * @param rarity The rarity id
     * @return Returns if the player has this shiny card in a deck.
     */
    boolean hasShinyCard(UUID playerUuid, String card, String rarity, String seriesId);

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
     * @param seriesId The seriesId
     * @return Returns if the series exists.
     */
    boolean containsSeries(final String seriesId);

    /**
     * @return Returns a collection of all series
     */
    Collection<Series> getAllSeries();

    /**
     * @return Returns a set of all "active" series.
     */
    Set<Series> getActiveSeries();


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

    @Nullable
    List<T> getCardsInRarityAndSeries(final String rarityId, final String seriesId);

    /**
     * @return A list of all active cards.
     */
    List<T> getActiveCards();


    Card<T> getCard(final String cardId, final String rarityId, final String seriesId);
    /**
     * @param packsId The pack id.
     * @return Returns the pack. Will return null if it doesn't exist.
     */
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

    MobGroup getMobGroup(final String groupId);

    //Create
    void createCard(final String cardId, final String rarityId, final String seriesId);
    void createRarity(final String rarityId);
    void createSeries(final String seriesId);
    void createColorSeries(final String seriesId);
    void createCustomType(final String typeId, final String type);
    void createPack(final String packId);
    void createMobGroup(final String groupId);
    // Edit
    // Edit MobGroup
    void editMobGroupDisplayName(final String groupId, final String displayName);
    void editMobGroupAddEntityType(final String groupId, final EntityType entityType);
    void editMobGroupRemoveEntityType(final String groupId, final EntityType entityType);
    void editMobGroupAddGroup(final String groupId, final String groupToAddId);
    void editMobGroupRemoveGroup(final String groupId, final String groupToRemoveId);
    void deleteMobGroup(final String groupId);
    // Edit Card
    void editCardDisplayName(final String rarityId, final String cardId, final String seriesId, final String displayName);
    void editCardSeries(final String rarityId, final String cardId, final String seriesId, final Series value);
    void editCardSellPrice(final String rarityId, final String cardId, final String seriesId, final double value);
    void editCardType(final String rarityId,final String cardId, final String seriesId, final DropType value);
    void editCardInfo(final String rarityId,final String cardId, final String seriesId, final String value);
    void editCardCustomModelData(final String rarityId,final String cardId,final String seriesId, final int value);
    void editCardBuyPrice(final String rarityId,final String cardId, final String seriesId,final double value);
    void editCardHasShiny(final String rarityId,final String cardId, final String seriesId,final boolean value);
    void editCardCurrencyId(final String rarityId,final String cardId, final String seriesId, final String value);
    // Edit Rarity
    void editRarityBuyPrice(final String rarityId, final double buyPrice);
    void editRarityAddReward(final String rarityId, final String reward);
    void editRarityDefaultColor(final String rarityId, final String defaultColor);
    void editRarityDisplayName(final String rarityId, final String displayName);
    void editRaritySellPrice(final String rarityId, final double sellPrice);
    void editRarityRemoveAllRewards(final String rarityId);
    void editRarityRemoveReward(final String rarityId, final int rewardNumber);
    void editRarityCustomOrder(final String rarityId, final int customOrder);
    int getRarityCustomOrder(final String rarityId);
    // Edit Series
    void editSeriesDisplayName(final String seriesId, final String displayName);
    void editSeriesColors(final String seriesId, final ColorSeries colors);
    void editSeriesMode(final String seriesId, final Mode mode);
    // Edit Series Colors
    void editColorSeries(final String seriesId, final ColorSeries colors);
    // Edit Type
    void editCustomTypeDisplayName(final String typeId, final String displayName);
    void editCustomTypeType(final String typeId, final String type); //It has to be a default type.
    // Edit Pack
    void editPackDisplayName(final String packId, final String displayName);
    void editPackContents(final String packId, final int lineNumber, final Pack.PackEntry packEntry);
    void editPackContentsAdd(final String packId, final Pack.PackEntry packEntry);
    void editPackContentsDelete(final String packId, final int lineNumber);
    void editPackPermission(final String packId, final String permission);
    void editPackPrice(final String packId,final double price);
    void editPackCurrencyId(final String packId, final String currencyId);

    /**
     * @return The total amount of cards.
     */
    int getCardsCount();

    /**
     * @param rarityId The rarity id.
     * @return The total amount of cards in a rarity
     */
    int getCardsInRarityCount(final String rarityId);
    /**
     * @param rarityId The rarity id.
     * @param seriesId The series id.
     * @return The total amount of cards in a rarity & a series
     */
    int getCardsInRarityAndSeriesCount(final String rarityId, final String seriesId);

    void shutdown() throws Exception;
}
