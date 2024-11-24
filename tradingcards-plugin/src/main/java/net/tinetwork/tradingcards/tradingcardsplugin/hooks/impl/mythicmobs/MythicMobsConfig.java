package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs;


import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.chances.LevelChances;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.chances.LevelChancesTypeSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MythicMobsConfig extends YamlConfigurateFile<TradingCards> {
    private boolean enabled;
    private boolean perLevelChances;

    private Map<Double, LevelChances> levelChances;

    public MythicMobsConfig(@NotNull TradingCards plugin) throws ConfigurateException {
        super(plugin, "hooks" + File.separator, "mythicmobs.yml", "hooks");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.enabled = rootNode.node("mythicmobs-enabled").getBoolean(true);
        this.perLevelChances = rootNode.node("per-level-chances").getBoolean(true);

        levelChances = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : rootNode.node("levels").childrenMap().entrySet()) {
            levelChances.put((Double) nodeEntry.getKey(), nodeEntry.getValue().get(LevelChances.class));
        }
    }

    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        builder.register(LevelChances.class, LevelChancesTypeSerializer.INSTANCE);
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }

    public Map<Double, LevelChances> levelChances() {
        return levelChances;
    }

    public LevelChances getChances(final double level) {
        return levelChances.get(level);
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean perLevelChances() {
        return perLevelChances;
    }
}
