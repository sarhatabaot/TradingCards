package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mobarena;


import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class MobArenaConfig extends YamlConfigurateFile<TradingCards> {
    @Override
    protected void initValues() throws ConfigurateException {

    }

    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {

    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }
}
