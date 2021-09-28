package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.DeckEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.deck.DeckEntrySerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.config.deck.DeckSerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


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

    public boolean containsPlayer(final UUID uuid) {
        return !inventoriesNode.node(uuid.toString()).empty();
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
                        .registerExact(DeckEntry.class, DeckEntrySerializer.INSTANCE)));
    }

    public List<String> getDeckEntries(final UUID uuid, final String deckNumber) {
        try {
            return inventoriesNode.node(uuid.toString()).node(deckNumber).getList(String.class);
        } catch (SerializationException e) {
            plugin.getLogger().warning(e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<DeckEntry> convertToDeckEntries(final List<String> list) {
        List<DeckEntry> entries = new ArrayList<>();
        for(String entry: list) {
            entries.add(DeckEntry.fromString(entry));
        }
        return entries;
    }

    public void saveEntries(final UUID uuid, final int deckNumber, final List<DeckEntry> entries) {
        List<String> stringEntries = getStringListFromEntries(entries);
        plugin.debug(stringEntries.toString());
        plugin.getDeckConfig().getConfig().set(INVENTORY_PATH + uuid + "." + deckNumber, stringEntries);
        plugin.getDeckConfig().saveConfig();
    }

    public boolean containsDeck(final UUID uuid, String deckNumber) {
        boolean containsDeck = !inventoriesNode.node(uuid).node(deckNumber).isNull();
        plugin.debug("Deck "+deckNumber+" for "+uuid+":"+containsDeck);
        return containsDeck;
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
            for (String cardString : getDeckEntries(uuid, deckNumber)) {
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
            for (String cardString : getDeckEntries(uuid, deckNumber)) {
                DeckEntry deckEntry = DeckEntry.fromString(cardString);
                if (deckEntry.getRarityId().equalsIgnoreCase(rarity) && deckEntry.getCardId().equalsIgnoreCase(card) && deckEntry.isShiny())
                    return true;
            }
        }
        return false;
    }


}
