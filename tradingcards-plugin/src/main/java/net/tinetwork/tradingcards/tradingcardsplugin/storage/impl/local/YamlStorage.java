package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.CardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.SimpleCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.CustomTypesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.PacksConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.RaritiesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.SeriesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.DropTypeManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil.cardKey;

/**
 * @author sarhatabaot
 */
public class YamlStorage implements Storage<TradingCard> {
    private final TradingCards plugin;
    private final CardsConfig cardsConfig;
    private final DeckConfig deckConfig;
    private final RaritiesConfig raritiesConfig;
    private final SeriesConfig seriesConfig;
    private final PacksConfig packsConfig;
    private final CustomTypesConfig customTypesConfig;

    //String is CardKey (rarity.name) We should just have it as a class..
    private final Map<String, TradingCard> cards;
    private final Map<String, TradingCard> activeCards;
    private final Map<String, List<TradingCard>> rarityCardList;
    private final Map<String, List<TradingCard>> seriesCardList;
    private final Set<Series> activeSeries;

    public YamlStorage(final TradingCards plugin) throws ConfigurateException {
        this.plugin = plugin;

        this.deckConfig = new DeckConfig(plugin);

        this.packsConfig = new PacksConfig(plugin);
        this.raritiesConfig = new RaritiesConfig(plugin);
        this.seriesConfig = new SeriesConfig(plugin);
        this.customTypesConfig = new CustomTypesConfig(plugin);
        this.cardsConfig = new CardsConfig(plugin);

        this.cards = new HashMap<>();
        this.rarityCardList = new HashMap<>();
        this.seriesCardList = new HashMap<>();

        this.activeSeries = new HashSet<>();
        this.activeCards = new HashMap<>();
    }

    @Override
    public StorageType getType() {
        return StorageType.YAML;
    }

    @Override
    public List<Deck> getPlayerDecks(final UUID playerUuid) {
        return deckConfig.getPlayerDecks(playerUuid);
    }

    @Override
    public void init(final TradingCards plugin) {
        loadCards();
        loadActiveSeries();
        loadActiveCards();
        loadRarityCards();
        loadSeriesCards();
    }

    @Override
    public Deck getDeck(final UUID playerUuid, final int deckNumber) {
        final List<String> deckEntries = deckConfig.getDeckEntries(playerUuid, String.valueOf(deckNumber));
        final List<StorageEntry> storageEntries = DeckConfig.convertToDeckEntries(deckEntries);
        return new Deck(playerUuid,deckNumber,storageEntries);
    }

    @Override
    public void saveDeck(final UUID playerUuid, final int deckNumber, final Deck deck) {
        deckConfig.saveEntries(playerUuid,deckNumber,deck);
        deckConfig.reloadConfig();
    }

    @Override
    public boolean hasCard(final UUID playerUuid, final String card, final String rarity) {
        return deckConfig.containsCard(playerUuid,card,rarity);
    }

    @Override
    public boolean hasShinyCard(final UUID playerUuid, final String card, final String rarity) {
        return deckConfig.containsShinyCard(playerUuid,card,rarity);
    }

    public Map<UUID,List<Deck>> getAllDecks() {
        return deckConfig.getAllDecks();
    }


    @Override
    public Rarity getRarityById(final String rarityId) {
        try {
            return raritiesConfig.getRarity(rarityId);
        } catch (SerializationException e){
            return null;
        }
    }


    @Override
    public List<String> getRewards(final String rarityId) {
        final Rarity rarity = getRarityById(rarityId);
        if(rarity == null)
            return null;

        return rarity.getRewards();
    }

    @Override
    @Nullable
    public Series getSeries(final String seriesId) {
        return seriesConfig.series().get(seriesId);
    }

    @Override
    public void reload() {
        this.raritiesConfig.reloadConfig();
        this.seriesConfig.reloadConfig();
        this.customTypesConfig.reloadConfig();
        this.cardsConfig.initValues();
        this.packsConfig.reloadConfig();
    }

    @Override
    public List<Rarity> getRarities() {
        return this.raritiesConfig.rarities();
    }

    @Override
    public ColorSeries getColorSeries(final String seriesId) {
        return this.seriesConfig.getColorSeries(seriesId);
    }

    @Override
    public Collection<Series> getAllSeries() {
        return this.seriesConfig.series().values();
    }

    @Override
    public Map<String, TradingCard> getCardsMap() {
        return this.cards;
    }

    @Override
    public List<TradingCard> getCards() {
        return this.cards.values().stream().toList();
    }

    @Override
    public Pack getPack(final String packsId) {
        try {
            return this.packsConfig.getPack(packsId);
        } catch (SerializationException e){
            Util.logSevereException(e);
            return null;
        }
    }

    @Override
    public List<Pack> getPacks() {
        return this.packsConfig.getPacks();
    }

    @Override
    public Set<DropType> getDropTypes() {
        return this.customTypesConfig.getDropTypes();
    }

    public TradingCard generateCard(final SimpleCardsConfig simpleCardsConfig, final String cardId, final String rarityId) {
        if ("none".equalsIgnoreCase(rarityId)) {
            return TradingCardManager.NULL_CARD;
        }

        return simpleCardsConfig.getCard(rarityId, cardId).get();
    }

    private void loadCards() {
        for (SimpleCardsConfig simpleCardsConfig : cardsConfig.getCardConfigs()) {
            for (final Rarity rarity : plugin.getRarityManager().getRarities()) {
                var cardNodes = simpleCardsConfig.getCards(rarity.getName()).entrySet();

                for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : cardNodes) {
                    final String cardName = nodeEntry.getValue().key().toString();
                    final String cardKey = cardKey(rarity.getName(), cardName);
                    plugin.debug(TradingCardManager.class,"CardKey="+cardKey);
                    cards.put(cardKey, generateCard(simpleCardsConfig, cardName, rarity.getName()));
                }
            }
        }
    }

    private void loadActiveSeries() {
        for(Series series: getAllSeries()) {
            if(series.isActive()) {
                activeSeries.add(series);
            }
        }
    }

    private void loadRarityCards() {
        for(Map.Entry<String,TradingCard> entry: cards.entrySet()) {
            final TradingCard card = entry.getValue();
            final Rarity rarity = card.getRarity();
            rarityCardList.putIfAbsent(rarity.getName(),new ArrayList<>());
            rarityCardList.get(rarity.getName()).add(card);
        }
    }


    private void loadSeriesCards() {
        for(Map.Entry<String,TradingCard> entry: cards.entrySet()) {
            final TradingCard card = entry.getValue();
            final Series series = card.getSeries();
            seriesCardList.putIfAbsent(series.getName(),new ArrayList<>());
            seriesCardList.get(series.getName()).add(card);
        }
    }


    private void loadActiveCards() {
        for(TradingCard card: getCards()) {
            //This only loads on startup, that means that it doesn't update. But only on restarts TODO
            if(card.getSeries().isActive()) {
                activeCards.put(cardKey(card.getRarity().getName(),card.getCardName()), card);
            }
        }
    }

    @Override
    public Map<String, TradingCard> getActiveCardsMap() {
        return activeCards;
    }

    @Override
    public Set<Series> getActiveSeries() {
        return activeSeries;
    }

    @Override
    public List<TradingCard> getCardsInRarity(final String rarityId) {
        return rarityCardList.get(rarityId);
    }

    @Override
    public List<TradingCard> getCardsInSeries(final String seriesId) {
        return seriesCardList.get(seriesId);
    }

    @Override
    public List<TradingCard> getActiveCards() {
        return activeCards.values().stream().toList();
    }

    @Override
    public Card<TradingCard> getCard(final String cardId, final String rarityId) {
        return cards.get(cardKey(rarityId,cardId));
    }

    @Override
    public Card<TradingCard> getActiveCard(final String cardId, final String rarityId) {
        return activeCards.get(cardKey(rarityId,cardId));
    }

    @Override
    public DropType getCustomType(final String typeId) {
        try {
            return customTypesConfig.getDropType(typeId);
        } catch (SerializationException e) {
            Util.logWarningException(e);
        }
        return DropTypeManager.ALL;
    }
}
