package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mobarena;


import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;

public class MobArenaConfig extends YamlConfigurateFile<TradingCards> {
    private boolean enabled;
    private boolean disableInArena;
    private boolean useActiveSeries;

    public MobArenaConfig(@NotNull TradingCards plugin) throws ConfigurateException {
        super(plugin, "hooks" + File.separator, "mobarena.yml", "hooks");
    }


    @Override
    protected void initValues() throws ConfigurateException {
        this.enabled = rootNode.node("enabled").getBoolean(false);
        this.disableInArena = rootNode.node("disable-in-arena").getBoolean(true);
        this.useActiveSeries = rootNode.node("use-active-series").getBoolean(true);
    }

    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {

    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean disableInArena() {
        return disableInArena;
    }

    public boolean useActiveSeries() {
        return useActiveSeries;
    }
}
