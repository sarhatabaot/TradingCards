package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
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

    //String is CardKey (rarity_id.card_id) We should just have it as a class..
    private final Map<String, TradingCard> cards;
    private final Map<String, TradingCard> activeCards;
    private final Map<String, List<TradingCard>> rarityCardList;
    private final Map<String, List<TradingCard>> seriesCardList;
    private Map<String,Map<String,List<TradingCard>>> raritySeriesCardList;



    private final Set<Series> activeSeries;

    public YamlStorage(final TradingCards plugin) throws ConfigurateException {
        this.plugin = plugin;

        this.deckConfig = new DeckConfig(plugin);

        this.packsConfig = new PacksConfig(plugin);
        this.raritiesConfig = new RaritiesConfig(plugin);
        this.seriesConfig = new SeriesConfig(plugin);
        this.customTypesConfig = new CustomTypesConfig(plugin);
        this.cardsConfig = new CardsConfig(plugin,this);

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
        loadRaritySeriesCardList();
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
        return this.customTypesConfig.getCustomTypes();
    }

    public TradingCard generateCard(final SimpleCardsConfig simpleCardsConfig, final String cardId, final String rarityId) {
        if ("none".equalsIgnoreCase(rarityId)) {
            return TradingCardManager.NULL_CARD;
        }

        return simpleCardsConfig.getCard(rarityId, cardId).get();
    }

    private void loadCards() {
        for (SimpleCardsConfig simpleCardsConfig : cardsConfig.getCardConfigs()) {
            for (final Rarity rarity : raritiesConfig.rarities()) {
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
            //This only loads on startup, that means that it doesn't update. But only on restarts/reloads TODO
            if(card.getSeries().isActive()) {
                activeCards.put(cardKey(card.getRarity().getName(),card.getCardId()), card);
            }
        }
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
    public List<TradingCard> getCardsInRarityAndSeries(final String rarityId, final String seriesId) {
        return raritySeriesCardList.get(rarityId).get(seriesId);
    }

    //todo
    private void loadRaritySeriesCardList() {
        this.raritySeriesCardList = new HashMap<>();
        for(final Rarity rarity : plugin.getStorage().getRarities()) {
            this.raritySeriesCardList.putIfAbsent(rarity.getName(),new HashMap<>());
            Map<String,List<TradingCard>> seriesCardList = this.raritySeriesCardList.get(rarity.getName());
            if(rarityCardList.get(rarity.getName()) == null)
                continue;
            for(TradingCard tradingCard: rarityCardList.get(rarity.getName())) {
                String series = tradingCard.getSeries().getName();
                seriesCardList.putIfAbsent(series,new ArrayList<>());
                seriesCardList.get(series).add(tradingCard);
            }
        }
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
    public void createCard(final String cardId, final String rarityId, final String seriesId) {
        //use the first available config to create new cards, perhaps this should be configurable.
        SimpleCardsConfig config = cardsConfig.getCardConfigs().get(0);
        config.createCard(cardId,rarityId,seriesId);
    }

    @Override
    public void createRarity(final String rarityId) {
        raritiesConfig.createRarity(rarityId);
    }

    @Override
    public void createSeries(final String seriesId) {
        seriesConfig.createSeries(seriesId);
    }

    @Override
    public void createColorSeries(final String seriesId) {
        //does nothing. in series.
    }

    @Override
    public void createCustomType(final String typeId, final String type) {
        customTypesConfig.createCustomType(typeId,type);
    }

    @Override
    public void createPack(final String packId) {
        packsConfig.createPack(packId);
    }

    @Override
    public DropType getCustomType(final String typeId) {
        try {
            return customTypesConfig.getCustomType(typeId);
        } catch (SerializationException e) {
            Util.logWarningException(e);
        }
        return DropTypeManager.ALL;
    }


    protected CardsConfig getCardsConfig() {
        return cardsConfig;
    }

    protected DeckConfig getDeckConfig() {
        return deckConfig;
    }

    protected RaritiesConfig getRaritiesConfig() {
        return raritiesConfig;
    }

    protected SeriesConfig getSeriesConfig() {
        return seriesConfig;
    }

    protected PacksConfig getPacksConfig() {
        return packsConfig;
    }

    protected CustomTypesConfig getCustomTypesConfig() {
        return customTypesConfig;
    }

    @Override
    public void editCardDisplayName(final String rarityId, final String cardId, final String seriesId, final String displayName) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editDisplayName(rarityId,cardId,seriesId,displayName);
    }

    @Override
    public void editCardSeries(final String rarityId, final String cardId, final String seriesId, final Series value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editSeries(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardSellPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editSellPrice(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardType(final String rarityId, final String cardId, final String seriesId, final DropType value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editType(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardInfo(final String rarityId, final String cardId, final String seriesId, final String value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editInfo(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardCustomModelData(final String rarityId, final String cardId, final String seriesId, final int value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editModelData(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardBuyPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editBuyPrice(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardHasShiny(final String rarityId, final String cardId, final String seriesId, final boolean value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(0);
        simpleCardsConfig.editHasShiny(rarityId,cardId,seriesId,value);
    }
    @Override
    public void editRarityBuyPrice(final String rarityId, final double buyPrice) {
        raritiesConfig.editBuyPrice(rarityId,buyPrice);
    }

    @Override
    public void editRarityAddReward(final String rarityId, final String reward) {
        raritiesConfig.editAddReward(rarityId,reward);
    }

    @Override
    public void editRarityDefaultColor(final String rarityId, final String defaultColor) {
        raritiesConfig.editDefaultColor(rarityId,defaultColor);
    }

    @Override
    public void editRarityDisplayName(final String rarityId, final String displayName) {
        raritiesConfig.editDisplayName(rarityId,displayName);
    }

    @Override
    public void editRaritySellPrice(final String rarityId, final double sellPrice) {
        raritiesConfig.editSellPrice(rarityId,sellPrice);
    }

    @Override
    public void editRarityRemoveAllRewards(final String rarityId) {
        raritiesConfig.editRemoveAllRewards(rarityId);
    }

    @Override
    public void editRarityRemoveReward(final String rarityId, final int rewardNumber) {
        raritiesConfig.editRemoveReward(rarityId,rewardNumber);
    }

    @Override
    public void editSeriesDisplayName(final String seriesId, final String displayName) {
        seriesConfig.editDisplayName(seriesId,displayName);
    }

    @Override
    public void editSeriesColors(final String seriesId, final ColorSeries colors) {
        seriesConfig.editColors(seriesId,colors);
    }

    @Override
    public void editSeriesMode(final String seriesId, final Mode mode) {
        seriesConfig.editMode(seriesId,mode);
    }

    @Override
    public void editColorSeries(final String seriesId, final ColorSeries colors) {
        seriesConfig.editColors(seriesId, colors);
    }

    @Override
    public void editCustomTypeDisplayName(final String typeId, final String displayName) {
        customTypesConfig.editDisplayName(typeId,displayName);
    }

    @Override
    public void editCustomTypeType(final String typeId, final String type) {
        customTypesConfig.editType(typeId, type);
    }

    @Override
    public void editPackDisplayName(final String packId, final String displayName) {
        packsConfig.editDisplayName(packId,displayName);
    }

    @Override
    public void editPackContents(final String packId, final int lineNumber, final Pack.PackEntry packEntry) {
        packsConfig.editContents(packId,lineNumber,packEntry);
    }

    @Override
    public void editPackContentsAdd(final String packId, final Pack.PackEntry packEntry) {
        List<Pack.PackEntry> packEntries = getPack(packId).getPackEntryList();
        int lineNumber = (packEntries == null) ? 0 : packEntries.size() - 1;
        packsConfig.editContents(packId,lineNumber,packEntry);
    }

    @Override
    public void editPackContentsDelete(final String packId, final int lineNumber) {
        packsConfig.editContents(packId,lineNumber,null);
    }

    @Override
    public void editPackPermission(final String packId, final String permission) {
        packsConfig.editPermission(packId,permission);
    }

    @Override
    public void editPackPrice(final String packId, final double price) {
        packsConfig.editPrice(packId,price);
    }
}
