package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.deck.DeckEntrySerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.config.deck.DeckSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Deprecated
/**
 * Should be implemented via Storage.
 * This is not a config class but rather a data file.
 */
public class DeckConfig extends SimpleConfigurate {
    private static final String INVENTORY_PATH = "decks.inventories.";
    private CommentedConfigurationNode inventoriesNode;

    public DeckConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "data" + File.separator, "decks.yml", "data");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.inventoriesNode = rootNode.node("decks", "inventories");
        loadYamlConfiguration();
    }

    private void loadYamlConfiguration() {
        if (file == null) {
            file = new File(folder, fileName);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean containsPlayer(final @NotNull UUID uuid) {
        return !inventoriesNode.node(uuid.toString()).empty();
    }

    public Deck getDeck(final UUID playerUuid, final int deckNumber) throws SerializationException {
        return inventoriesNode.node(playerUuid).node(deckNumber).get(Deck.class);
    }

    public List<Deck> getPlayerDecks(final UUID playerUuid) {
        List<Deck> decks = new ArrayList<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: inventoriesNode.node(playerUuid).childrenMap().entrySet()) {
            final int deckNumber = Integer.parseInt(nodeEntry.getKey().toString());
            try {
                final Deck deck = getDeck(playerUuid, deckNumber);
                decks.add(deck);
            } catch (SerializationException e){
                //skip and move on, log an error
            }
        }
        return decks;
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
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(Deck.class, DeckSerializer.INSTANCE)
                        .registerExact(StorageEntry.class, DeckEntrySerializer.INSTANCE)));
    }

    public List<String> getDeckEntries(final @NotNull UUID uuid, final String deckNumber) {
        try {
            return inventoriesNode.node(uuid.toString()).node(deckNumber).getList(String.class);
        } catch (SerializationException e) {
            plugin.getLogger().warning(e.getMessage());
        }
        return Collections.emptyList();
    }

    public static @NotNull List<StorageEntry> convertToDeckEntries(final @NotNull List<String> list) {
        List<StorageEntry> entries = new ArrayList<>();
        for(String entry: list) {
            entries.add(StorageEntry.fromString(entry));
        }
        return entries;
    }

    public void saveEntries(final @NotNull UUID uuid, final int deckNumber, final List<StorageEntry> entries) {
        List<String> stringEntries = getStringListFromEntries(entries);
        plugin.debug(getClass(),stringEntries.toString());
        getConfig().set(INVENTORY_PATH + uuid + "." + deckNumber, stringEntries);
        saveConfig();
    }

    public void saveEntries(final @NotNull UUID uuid, final int deckNumber,final Deck deck) {
        saveEntries(uuid,deckNumber,deck.getDeckEntries());
    }

    public boolean containsDeck(final UUID uuid, String deckNumber) {
        boolean containsDeck = !inventoriesNode.node(uuid).node(deckNumber).isNull();
        plugin.debug(getClass(),"Deck "+deckNumber+" for "+uuid+":"+containsDeck);
        return containsDeck;
    }

    private @NotNull List<String> getStringListFromEntries(final @NotNull List<StorageEntry> entries) {
        List<String> stringList = new ArrayList<>();
        for (StorageEntry entry : entries) {
            stringList.add(entry.toString());
        }
        plugin.debug(DeckConfig.class,"EntryList Size " + stringList.size());
        return stringList;
    }

    @Deprecated
    public boolean containsCard(final UUID uuid, final String card, final String rarity) {
        if (getAllDecks(uuid) == null || getAllDecks(uuid).getValues(false).isEmpty())
            return false;
        for (String deckNumber : getAllDecks(uuid).getValues(false).keySet()) {
            for (String cardString : getDeckEntries(uuid, deckNumber)) {
                StorageEntry deckEntry = StorageEntry.fromString(cardString);
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
            for (String cardString : getDeckEntries(uuid, deckNumber)) {
                StorageEntry deckEntry = StorageEntry.fromString(cardString);
                if (deckEntry.getRarityId().equalsIgnoreCase(rarity) && deckEntry.getCardId().equalsIgnoreCase(card) && deckEntry.isShiny())
                    return true;
            }
        }
        return false;
    }


}
