package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.tradingcardsplugin.config.DeckConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class YamlStorage implements Storage {
    private final Logger logger = LoggerFactory.getLogger(YamlStorage.class);
    private DeckConfig deckConfig;

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
    public Deck getDeck(final UUID playerUuid, final int deckNumber) {
        try {
            return deckConfig.getDeck(playerUuid, deckNumber);
        } catch (SerializationException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public void save(final UUID playerUuid, final int deckNumber, final Deck deck) {
        deckConfig.saveEntries(playerUuid,deckNumber,deck);
    }
}
