package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.CardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.CustomTypesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.PacksConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.RaritiesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.SeriesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class YamlStorage implements Storage {
    private final CardsConfig cardsConfig;
    private final DeckConfig deckConfig;
    private final RaritiesConfig raritiesConfig;
    private final SeriesConfig seriesConfig;
    private final PacksConfig packsConfig;
    private final CustomTypesConfig customTypesConfig;

    public YamlStorage(final TradingCards plugin) throws ConfigurateException {
        this.deckConfig = new DeckConfig(plugin);

        this.packsConfig = new PacksConfig(plugin);
        this.raritiesConfig = new RaritiesConfig(plugin);
        this.seriesConfig = new SeriesConfig(plugin);
        this.customTypesConfig = new CustomTypesConfig(plugin);
        this.cardsConfig = new CardsConfig(plugin);
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
        //nothing to init here
    }

    @Override
    public Deck getDeck(final UUID playerUuid, final int deckNumber) {
        final List<String> deckEntries = deckConfig.getDeckEntries(playerUuid, String.valueOf(deckNumber));
        final List<StorageEntry> storageEntries = DeckConfig.convertToDeckEntries(deckEntries);
        return new Deck(playerUuid,deckNumber,storageEntries);
    }

    @Override
    public void save(final UUID playerUuid, final int deckNumber, final Deck deck) {
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
}
