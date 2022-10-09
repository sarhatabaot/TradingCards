package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Upgrade;
import net.tinetwork.tradingcards.api.model.pack.EmptyPack;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.DropTypeManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeCardKey;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    private final UpgradesConfig upgradesConfig;

    private final Map<CompositeCardKey, TradingCard> cards;
    private final Map<CompositeCardKey, TradingCard> activeCards;
    private final Map<String, List<TradingCard>> rarityCardList;
    private final Map<String, List<TradingCard>> seriesCardList;
    private Map<String,Map<String,List<TradingCard>>> raritySeriesCardList;

    @Override
    public void shutdown() throws Exception {
        //nothing
    }

    private final Set<Series> activeSeries;

    public YamlStorage(final TradingCards plugin) throws ConfigurateException {
        this.plugin = plugin;

        this.deckConfig = new DeckConfig(plugin);

        this.packsConfig = new PacksConfig(plugin);
        this.raritiesConfig = new RaritiesConfig(plugin);
        this.seriesConfig = new SeriesConfig(plugin);
        this.customTypesConfig = new CustomTypesConfig(plugin);
        this.cardsConfig = new CardsConfig(plugin,this);
        this.upgradesConfig = new UpgradesConfig(plugin);

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
    public boolean hasCard(final UUID playerUuid, final String cardId, final String rarityId,final String seriesId) {
        return deckConfig.containsCard(playerUuid, cardId, rarityId);
    }

    @Override
    public boolean hasShinyCard(final UUID playerUuid, final String card, final String rarity,final String seriesId) {
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
    public boolean containsSeries(final String seriesId) {
        return seriesConfig.series().containsKey(seriesId);
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
    public List<TradingCard> getCards() {
        return this.cards.values().stream().toList();
    }

    @Override
    public Pack getPack(final String packsId) {
        try {
            return this.packsConfig.getPack(packsId);
        } catch (SerializationException e){
            Util.logSevereException(e);
            return EmptyPack.emptyPack();
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
            return AllCardManager.NULL_CARD;
        }

        return simpleCardsConfig.getCard(rarityId, cardId).get();
    }

    private void loadCards() {
        for (Map.Entry<String,SimpleCardsConfig> entry: cardsConfig.getCardConfigs().entrySet()) {
            for (final Rarity rarity : raritiesConfig.rarities()) {
                var cardNodes = entry.getValue().getCards(rarity.getId()).entrySet();

                for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : cardNodes) {
                    final String cardId = nodeEntry.getValue().key().toString();
                    final String seriesId = nodeEntry.getValue().node("series").getString("default");
                    final CompositeCardKey cardKey = new CompositeCardKey(rarity.getId(),seriesId,cardId);
                    plugin.debug(YamlStorage.class, InternalDebug.CARD_KEY.formatted(cardKey));
                    cards.put(cardKey, generateCard(entry.getValue(), cardId, rarity.getId()));
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
        for(Map.Entry<CompositeCardKey,TradingCard> entry: cards.entrySet()) {
            final TradingCard card = entry.getValue();
            final Rarity rarity = card.getRarity();
            rarityCardList.putIfAbsent(rarity.getId(),new ArrayList<>());
            rarityCardList.get(rarity.getId()).add(card);
        }
    }


    private void loadSeriesCards() {
        for(Map.Entry<CompositeCardKey,TradingCard> entry: cards.entrySet()) {
            final TradingCard card = entry.getValue();
            final Series series = card.getSeries();
            seriesCardList.putIfAbsent(series.getId(),new ArrayList<>());
            seriesCardList.get(series.getId()).add(card);
        }
    }


    private void loadActiveCards() {
        for(TradingCard card: getCards()) {
            //This only loads on startup, that means that it doesn't update. But only on restarts/reloads TODO
            if(card.getSeries().isActive()) {
                activeCards.put(CompositeCardKey.fromCard(card),card);
            }
        }
    }

    @Override
    public Set<Series> getActiveSeries() {
        return activeSeries;
    }

    @Override
    public List<TradingCard> getCardsInRarity(final String rarityId) {
        return rarityCardList.getOrDefault(rarityId, Collections.emptyList());
    }

    @Override
    public List<TradingCard> getCardsInSeries(final String seriesId) {
        return seriesCardList.getOrDefault(seriesId, Collections.emptyList());
    }

    @Override
    public List<TradingCard> getCardsInRarityAndSeries(final String rarityId, final String seriesId) {
        return raritySeriesCardList.get(rarityId).getOrDefault(seriesId, Collections.emptyList());
    }

    private void loadRaritySeriesCardList() {
        this.raritySeriesCardList = new HashMap<>();
        for(final Rarity rarity : plugin.getStorage().getRarities()) {
            this.raritySeriesCardList.putIfAbsent(rarity.getId(),new HashMap<>());
            Map<String,List<TradingCard>> seriesCardMap = this.raritySeriesCardList.get(rarity.getId());
            if(rarityCardList.get(rarity.getId()) == null)
                continue;
            for(TradingCard tradingCard: rarityCardList.get(rarity.getId())) {
                String series = tradingCard.getSeries().getId();
                seriesCardMap.putIfAbsent(series,new ArrayList<>());
                seriesCardMap.get(series).add(tradingCard);
            }
            this.raritySeriesCardList.put(rarity.getId(),seriesCardMap);
        }
    }

    @Override
    public List<TradingCard> getActiveCards() {
        return activeCards.values().stream().toList();
    }

    @Override
    public Card<TradingCard> getCard(final String cardId, final String rarityId, final String seriesId) {
        return cards.get(new CompositeCardKey(rarityId,seriesId,cardId));
    }

    @Override
    public void createCard(final String cardId, final String rarityId, final String seriesId) {
        SimpleCardsConfig config = cardsConfig.getCardConfigs().get(plugin.getStorageConfig().getDefaultCardsFile());
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

    private String getDefaultEditCardFile() {
        return plugin.getStorageConfig().getDefaultCardsFile();
    }

    @Override
    public void editCardDisplayName(final String rarityId, final String cardId, final String seriesId, final String displayName) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editDisplayName(rarityId,cardId,seriesId,displayName);
    }

    @Override
    public void editCardSeries(final String rarityId, final String cardId, final String seriesId, final Series value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editSeries(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardSellPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editSellPrice(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardType(final String rarityId, final String cardId, final String seriesId, final DropType value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editType(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardInfo(final String rarityId, final String cardId, final String seriesId, final String value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editInfo(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardCustomModelData(final String rarityId, final String cardId, final String seriesId, final int value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editModelData(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardBuyPrice(final String rarityId, final String cardId, final String seriesId, final double value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editBuyPrice(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardHasShiny(final String rarityId, final String cardId, final String seriesId, final boolean value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editHasShiny(rarityId,cardId,seriesId,value);
    }

    @Override
    public void editCardCurrencyId(final String rarityId, final String cardId, final String seriesId, final String value) {
        SimpleCardsConfig simpleCardsConfig = cardsConfig.getCardConfigs().get(getDefaultEditCardFile());
        simpleCardsConfig.editCurrencyId(rarityId,cardId,seriesId,value);
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
    public void editRarityCustomOrder(final String rarityId, final int customOrder) {
        throw new UnsupportedOperationException("In yaml mode, this is done by ordering the rarities in the file.");
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
    public void editPackContents(final String packId, final int lineNumber, final PackEntry packEntry) {
        packsConfig.editContents(packId,lineNumber,packEntry);
    }

    @Override
    public void editPackContentsAdd(final String packId, final PackEntry packEntry) {
        List<PackEntry> packEntries = getPack(packId).getPackEntryList();
        int lineNumber = (packEntries == null) ? 0 : packEntries.size() - 1;
        packsConfig.editContents(packId,lineNumber,packEntry);
    }

    @Override
    public void editPackContentsDelete(final String packId, final int lineNumber) {
        packsConfig.editContents(packId,lineNumber,null);
    }

    @Override
    public void editPackTradeCards(final String packId, final int lineNumber, final PackEntry packEntry) {
        packsConfig.editTradeCards(packId,lineNumber,packEntry);
    }

    @Override
    public void editPackTradeCardsAdd(final String packId, final PackEntry packEntry) {
        List<PackEntry> packEntries = getPack(packId).getTradeCards();
        int lineNumber = (packEntries == null) ? 0 : packEntries.size() - 1;
        packsConfig.editTradeCards(packId,lineNumber,packEntry);
    }

    @Override
    public void editPackTradeCardsDelete(final String packId, final int lineNumber) {
        packsConfig.editTradeCards(packId,lineNumber,null);
    }

    @Override
    public void editPackPermission(final String packId, final String permission) {
        packsConfig.editPermission(packId,permission);
    }

    @Override
    public void editPackPrice(final String packId, final double price) {
        packsConfig.editPrice(packId,price);
    }

    @Override
    public void editPackCurrencyId(final String packId, final String currencyId) {
        packsConfig.editCurrencyId(packId,currencyId);
    }

    @Override
    public int getCardsCount() {
        return cards.keySet().size();
    }

    @Override
    public int getCardsInRarityCount(final String rarityId) {
        return getCardsInRarity(rarityId).size();
    }

    @Override
    public int getCardsInRarityAndSeriesCount(final String rarityId, final String seriesId) {
        return getCardsInRarityAndSeries(rarityId,seriesId).size();
    }

    @Override
    public int getRarityCustomOrder(final String rarityId) {
        return getRarities().indexOf(getRarityById(rarityId));
    }

    @Override
    public List<Upgrade> getUpgrades() {
        return upgradesConfig.getUpgrades();
    }

    @Override
    public void createUpgrade(final String upgradeId, final PackEntry required, final PackEntry result) {
        upgradesConfig.createUpgrade(upgradeId, required, result);
    }

    @Override
    public Upgrade getUpgrade(final String upgradeId) {
        try {
            return upgradesConfig.getUpgrade(upgradeId);
        } catch (SerializationException e){
            Util.logSevereException(e);
            return null;
        }
    }

    @Override
    public void editUpgradeRequired(final String upgradeId, final PackEntry required) {
        upgradesConfig.editUpgradeRequired(upgradeId, required);
    }

    @Override
    public void editUpgradeResult(final String upgradeId, final PackEntry result) {
        upgradesConfig.editUpgradeResult(upgradeId, result);
    }

    @Override
    public void deleteUpgrade(final String upgradeId) {
        upgradesConfig.deleteUpgrade(upgradeId);
    }
}
