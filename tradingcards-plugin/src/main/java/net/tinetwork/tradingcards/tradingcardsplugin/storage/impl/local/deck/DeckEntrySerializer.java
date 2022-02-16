package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.deck;

import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * @author sarhatabaot
 */
public class DeckEntrySerializer implements TypeSerializer<StorageEntry> {
    public static final DeckEntrySerializer INSTANCE = new DeckEntrySerializer();

    private DeckEntrySerializer (){
    }


    @Override
    public StorageEntry deserialize(final Type type, final @NotNull ConfigurationNode node) throws SerializationException {
        final String content = node.getString();
        return StorageEntry.fromString(content);
    }

    @Override
    public void serialize(final Type type, @org.checkerframework.checker.nullness.qual.Nullable final StorageEntry obj, final ConfigurationNode node) throws SerializationException {

    }
}