package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.chances;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class LevelChancesTypeSerializer implements TypeSerializer<LevelChances> {
    public static final LevelChancesTypeSerializer INSTANCE = new LevelChancesTypeSerializer();


    @Override
    public LevelChances deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final int level = node.getInt();
        final int shinyVersionChance = node.node("shiny-version-chance").getInt();
        final int dropChance = node.node("drop-chance").getInt();

        final Map<String, Integer> rarityChances = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : node.node("rarities").childrenMap().entrySet()) {
            final String rarityId = (String) nodeEntry.getKey();
            final int rarityChance = nodeEntry.getValue().getInt();
            rarityChances.put(rarityId, rarityChance);
        }

        return new LevelChances(level, shinyVersionChance, dropChance, rarityChances);
    }

    @Override
    public void serialize(Type type, @Nullable LevelChances obj, ConfigurationNode node) throws SerializationException {
        //not relevant
    }
}
