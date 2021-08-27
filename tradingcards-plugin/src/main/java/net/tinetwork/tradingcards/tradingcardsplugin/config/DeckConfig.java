package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.api.model.DeckEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigFile;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;


public class DeckConfig extends SimpleConfigurate {
    private static final String INVENTORY_PATH = "decks.inventories.";
    private final ConfigurationNode inventoriesNode;

    public DeckConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "data" + File.separator, "decks.yml", "data");

        this.inventoriesNode = rootNode.node("decks", "inventories");
    }

    public boolean containsPlayer(final UUID uuid) {
        return !inventoriesNode.node(uuid.toString()).empty();
    }

    @Nullable
    public ConfigurationSection getAllDecks(final UUID uuid) {
        if (containsPlayer(uuid))
            return getConfig().getConfigurationSection(INVENTORY_PATH + uuid);
        return null;
    }

    @Override
    protected void registerTypeSerializer() {

    }

    @Nullable
    public List<String> getDeck(final UUID uuid, String deckNumber) {
        try {
            if (containsDeck(uuid, deckNumber))
                return inventoriesNode.node(uuid).node(deckNumber).getList(String.class);
        } catch (SerializationException e) {
            return null;
        }
        return null;
    }

    public boolean containsDeck(final UUID uuid, String deckNumber) {
        if (containsPlayer(uuid))
            return !inventoriesNode.node(uuid).node(deckNumber).empty();
        return false;
    }


    public boolean containsDeck(final UUID uuid, int deckNumber) {
        return containsDeck(uuid, String.valueOf(deckNumber));
    }

    public boolean containsCard(final UUID uuid, final String card, final String rarity) {
        if (getAllDecks(uuid) == null || getAllDecks(uuid).getValues(false).isEmpty())
            return false;
        for (String deckNumber : getAllDecks(uuid).getValues(false).keySet()) {
            for (String cardString : getDeck(uuid, deckNumber)) {
                DeckEntry deckEntry = DeckEntry.fromString(cardString);
                if(deckEntry.getRarityId().equalsIgnoreCase(rarity) && deckEntry.getCardId().equalsIgnoreCase(cardString) && !deckEntry.isShiny())
                    return true;
            }
        }
        return false;
    }

    public boolean containsShinyCard(final UUID uuid, final String card, final String rarity) {
        if (getAllDecks(uuid) == null || getAllDecks(uuid).getValues(false).isEmpty())
            return false;
        for (String deckNumber : getAllDecks(uuid).getValues(false).keySet()) {
            for (String cardString : getDeck(uuid, deckNumber)) {
                DeckEntry deckEntry = DeckEntry.fromString(cardString);
                if(deckEntry.getRarityId().equalsIgnoreCase(rarity) && deckEntry.getCardId().equalsIgnoreCase(card) && deckEntry.isShiny())
                    return true;
            }
        }
        return false;
    }
}
