package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs;


import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Map;

public class MythicMobsConfig extends YamlConfigurateFile<TradingCards> {
    private Map<Integer, MythicMobsLevelChances> levelChances;

    @Override
    protected void initValues() throws ConfigurateException {

    }

    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        //register type serializer for MythicMobsLevelChances
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }
}
