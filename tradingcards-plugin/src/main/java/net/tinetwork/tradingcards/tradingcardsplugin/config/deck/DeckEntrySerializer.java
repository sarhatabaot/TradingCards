package net.tinetwork.tradingcards.tradingcardsplugin.config.deck;

import net.tinetwork.tradingcards.api.model.deck.DeckEntry;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * @author sarhatabaot
 */
public class DeckEntrySerializer implements TypeSerializer<DeckEntry> {
    public static final DeckEntrySerializer INSTANCE = new DeckEntrySerializer();

    private DeckEntrySerializer (){
    }


    @Override
    public DeckEntry deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final String content = node.getString();
        return DeckEntry.fromString(content);
    }

    @Override
    public void serialize(final Type type, @org.checkerframework.checker.nullness.qual.Nullable final DeckEntry obj, final ConfigurationNode node) throws SerializationException {

    }
}