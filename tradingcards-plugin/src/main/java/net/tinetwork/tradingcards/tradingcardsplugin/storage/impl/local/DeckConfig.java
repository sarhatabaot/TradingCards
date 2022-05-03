package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local;

import com.github.sarhatabaot.kraken.core.config.ConfigurateFile;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.deck.DeckEntrySerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.deck.DeckSerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeckConfig extends ConfigurateFile<TradingCards> {
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

    public Deck getDeck(final UUID playerUuid, final int deckNumber) {
        try {
            return inventoriesNode.node(playerUuid.toString()).node(deckNumber).get(Deck.class);
        } catch (SerializationException e) {
            Util.logWarningException(e);
        }
        return new Deck(playerUuid, deckNumber, new ArrayList<>());
    }

    public List<Deck> getPlayerDecks(final UUID playerUuid) {
        List<Deck> decks = new ArrayList<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : inventoriesNode.node(playerUuid.toString()).childrenMap().entrySet()) {
            final int deckNumber = Integer.parseInt(nodeEntry.getKey().toString());
            final List<StorageEntry> deckEntries = convertToDeckEntries(getDeckEntries(playerUuid, String.valueOf(deckNumber)));
            final Deck deck = new Deck(playerUuid,deckNumber,deckEntries);
            decks.add(deck);
        }
        return decks;
    }

    public Map<UUID,List<Deck>> getAllDecks() {
        Map<UUID,List<Deck>> playerDeckMap = new HashMap<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry:inventoriesNode.childrenMap().entrySet()) {
            final UUID playerUuid = UUID.fromString(nodeEntry.getKey().toString());
            playerDeckMap.put(playerUuid,getPlayerDecks(playerUuid));
        }
        return playerDeckMap;
    }

    @Nullable
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
        for (String entry : list) {
            entries.add(StorageEntry.fromString(entry));
        }
        return entries;
    }

    public void saveEntries(final @NotNull UUID uuid, final int deckNumber, final List<StorageEntry> entries) {
        List<String> stringEntries = getStringListFromEntries(entries);
        plugin.debug(getClass(), stringEntries.toString());
        getConfig().set(INVENTORY_PATH + uuid + "." + deckNumber, stringEntries);
        saveConfig();
    }

    public void saveEntries(final @NotNull UUID uuid, final int deckNumber, final Deck deck) {
        saveEntries(uuid, deckNumber, deck.getDeckEntries());
    }

    public boolean containsDeck(final UUID uuid, String deckNumber) {
        boolean containsDeck = !inventoriesNode.node(uuid).node(deckNumber).isNull();
        plugin.debug(getClass(), "Deck " + deckNumber + " for " + uuid + ":" + containsDeck);
        return containsDeck;
    }

    private @NotNull List<String> getStringListFromEntries(final @NotNull List<StorageEntry> entries) {
        List<String> stringList = new ArrayList<>();
        for (StorageEntry entry : entries) {
            stringList.add(entry.toString());
        }
        plugin.debug(DeckConfig.class, "EntryList Size " + stringList.size());
        return stringList;
    }

    //Returns true if the deck contains this card, regardless if it's shiny or not
    public boolean containsCard(final UUID uuid, final String card, final String rarity) {
        List<Deck> playerDeckList = getPlayerDecks(uuid);
        if(playerDeckList.isEmpty())
            return false;

        for(Deck deck: playerDeckList) {
            if(deck.containsCard(card,rarity)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsShinyCard(final UUID uuid, final String card, final String rarity) {
        List<Deck> playerDeckList = getPlayerDecks(uuid);
        if(playerDeckList.isEmpty())
            return false;

        for(Deck deck: playerDeckList) {
            if(deck.containsCard(card,rarity)) {
                final StorageEntry cardEntry = deck.getCardEntry(card,rarity);
                if(cardEntry != null) {
                    return cardEntry.isShiny();
                }
            }
        }
        return false;
    }

}
