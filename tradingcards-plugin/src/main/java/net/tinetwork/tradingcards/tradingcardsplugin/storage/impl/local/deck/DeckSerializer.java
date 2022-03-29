package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.deck;

import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class DeckSerializer implements TypeSerializer<Deck> {
    public static final DeckSerializer INSTANCE = new DeckSerializer();


    private DeckSerializer() {
    }

    //uuid is known and deck number is known
    @Override
    public Deck deserialize(final Type type, final @NotNull ConfigurationNode node) throws SerializationException {
        final List<String> content = node.getList(String.class);

        final int deckNumber = Integer.parseInt(node.key().toString());
        final UUID playerUuid = UUID.fromString(node.parent().key().toString());

        List<StorageEntry> deckEntries = new ArrayList<>();
        if (content != null) {
            for (String deckEntryString : content) {
                deckEntries.add(StorageEntry.fromString(deckEntryString));
            }
        }
        return new Deck(playerUuid, deckNumber, deckEntries);
    }

    @Override
    public void serialize(final Type type, @org.checkerframework.checker.nullness.qual.Nullable final Deck obj, final ConfigurationNode node) throws SerializationException {
        final List<String> deckEntriesStrings = new ArrayList<>();
        if (obj.getDeckEntries() != null) {
            for (StorageEntry entry : obj.getDeckEntries())
                deckEntriesStrings.add(entry.toString());
        }
        node.setList(String.class, deckEntriesStrings);
    }
}
