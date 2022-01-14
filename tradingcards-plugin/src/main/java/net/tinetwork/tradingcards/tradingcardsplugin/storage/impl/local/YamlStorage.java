package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class YamlStorage implements Storage {
    private final Logger logger = LoggerFactory.getLogger(YamlStorage.class);
    private final DeckConfig deckConfig;

    public YamlStorage(final DeckConfig deckConfig) {
        this.deckConfig = deckConfig;
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

}
