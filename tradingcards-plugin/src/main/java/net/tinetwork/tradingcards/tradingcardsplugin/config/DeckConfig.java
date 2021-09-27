package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.DeckEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DeckConfig extends SimpleConfigurate {
    private static final String INVENTORY_PATH = "decks.inventories.";
    private CommentedConfigurationNode inventoriesNode;

    public DeckConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "data" + File.separator, "decks.yml", "data");
        this.config = getConfig();
    }

    public boolean containsPlayer(final UUID uuid) {
        return !inventoriesNode.node(uuid.toString()).empty();
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.inventoriesNode = rootNode.node("decks", "inventories");
    }

    @Nullable
    @Deprecated
    public ConfigurationSection getAllDecks(final UUID uuid) {
        if (containsPlayer(uuid))
            return getConfig().getConfigurationSection(INVENTORY_PATH + uuid);
        return null;
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder -> builder.registerExact(Deck.class, DeckSerializer.INSTANCE)));
    }

    @Nullable
    @Deprecated
    public List<String> getDeck(final UUID uuid, String deckNumber) {
        try {
            if (containsDeck(uuid, deckNumber))
                return inventoriesNode.node(uuid).node(deckNumber).getList(String.class);
        } catch (SerializationException e) {
            return null;
        }
        return null;
    }

    @Nullable
    public Deck getDeck(final UUID uuid, int deckNumber) throws ConfigurateException {
        final ConfigurationNode deckNode = inventoriesNode.node(uuid, deckNumber);
        if (deckNode.empty())
            loader.save(deckNode);
        return inventoriesNode.node(uuid, deckNumber).get(Deck.class);
    }

    public CommentedConfigurationNode getDeckNode(final UUID uuid, int deckNumber) throws SerializationException {
        if (inventoriesNode.node(uuid).isNull())
            inventoriesNode.set(uuid).set(deckNumber);

        return inventoriesNode.node(uuid, deckNumber);
    }

    public void saveEntries(final UUID uuid, final int deckNumber, final List<DeckEntry> entries) {
        List<String> stringEntries = getStringListFromEntries(entries);
        plugin.debug(stringEntries.toString());


        plugin.getDeckConfig().getConfig().set(INVENTORY_PATH + uuid + "." + deckNumber, stringEntries);
        plugin.getDeckConfig().saveConfig();
    }

    public boolean containsDeck(final UUID uuid, String deckNumber) {
        if (containsPlayer(uuid))
            return !inventoriesNode.node(uuid).node(deckNumber).empty();
        return false;
    }

    private List<String> getStringListFromEntries(final List<DeckEntry> entries) {
        List<String> stringList = new ArrayList<>();
        for (DeckEntry entry : entries) {
            stringList.add(entry.toString());
        }
        plugin.debug("EntryList Size " + stringList.size());
        return stringList;
    }

    @Deprecated
    public boolean containsCard(final UUID uuid, final String card, final String rarity) {
        if (getAllDecks(uuid) == null || getAllDecks(uuid).getValues(false).isEmpty())
            return false;
        for (String deckNumber : getAllDecks(uuid).getValues(false).keySet()) {
            for (String cardString : getDeck(uuid, deckNumber)) {
                DeckEntry deckEntry = DeckEntry.fromString(cardString);
                if (deckEntry.getRarityId().equalsIgnoreCase(rarity) && deckEntry.getCardId().equalsIgnoreCase(cardString) && !deckEntry.isShiny())
                    return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean containsShinyCard(final UUID uuid, final String card, final String rarity) {
        if (getAllDecks(uuid) == null || getAllDecks(uuid).getValues(false).isEmpty())
            return false;
        for (String deckNumber : getAllDecks(uuid).getValues(false).keySet()) {
            for (String cardString : getDeck(uuid, deckNumber)) {
                DeckEntry deckEntry = DeckEntry.fromString(cardString);
                if (deckEntry.getRarityId().equalsIgnoreCase(rarity) && deckEntry.getCardId().equalsIgnoreCase(card) && deckEntry.isShiny())
                    return true;
            }
        }
        return false;
    }

    public static class DeckSerializer implements TypeSerializer<Deck> {
        public static final DeckSerializer INSTANCE = new DeckSerializer();


        private DeckSerializer() {
        }

        //uuid is known and deck number is known
        @Override
        public Deck deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final List<String> content = node.getList(String.class);

            final int deckNumber = Integer.parseInt(node.key().toString());
            final UUID playerUuid = UUID.fromString(node.parent().key().toString());

            List<DeckEntry> deckEntries = new ArrayList<>();
            if (content != null) {
                for (String deckEntryString : content) {
                    deckEntries.add(DeckEntry.fromString(deckEntryString));
                }
            }
            return new Deck(playerUuid, deckNumber, deckEntries);
        }

        @Override
        public void serialize(final Type type, @org.checkerframework.checker.nullness.qual.Nullable final Deck obj, final ConfigurationNode node) throws SerializationException {
            //We should actually implement this here? TODO
            final List<String> deckEntriesStrings = new ArrayList<>();
            if (obj.getDeckEntries() != null) {
                for (DeckEntry entry : obj.getDeckEntries())
                    deckEntriesStrings.add(entry.toString());
            }
            node.setList(String.class, deckEntriesStrings);
        }
    }
}
