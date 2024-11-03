package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.towny;


import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;

public class TownyConfig extends YamlConfigurateFile<TradingCards> {
    private boolean enabled;
    private boolean createDefaults;

    private boolean allowDuplicates;
    private String townDuplicatePrefix;
    private String townDuplicateSuffix;
    private String townRarity;
    private String townSeries;
    private String townType;

    private String nationDuplicatePrefix;
    private String nationDuplicateSuffix;
    private String nationRarity;
    private String nationSeries;
    private String nationType;

    private boolean hasShiny;

    private String calendarMode;

    private int configVersion;

    public TownyConfig(@NotNull TradingCards plugin) throws ConfigurateException {
        super(plugin, "hooks" + File.separator, "towny.yml", "hooks");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.enabled = rootNode.node("towny-enabled").getBoolean(false);
        this.createDefaults = rootNode.node("create-defaults").getBoolean(true);

        this.allowDuplicates = rootNode.node("allow-duplicates").getBoolean(true);
        this.townDuplicatePrefix = rootNode.node("town-duplicate-prefix").getString("");
        this.townDuplicateSuffix = rootNode.node("town-duplicate-suffix").getString("_v%num%");
        this.townRarity = rootNode.node("town-rarity").getString("Common");
        this.townSeries = rootNode.node("town-series").getString("Towny");
        this.townType = rootNode.node("town-type").getString("Town");

        this.nationDuplicatePrefix = rootNode.node("nation-duplicate-prefix").getString("");
        this.nationDuplicateSuffix = rootNode.node("nation-duplicate-suffix").getString("_v%num%");
        this.nationRarity = rootNode.node("nation-rarity").getString("Uncommon");
        this.nationSeries = rootNode.node("nation-series").getString("Towny");
        this.nationType = rootNode.node("nation-type").getString("Nation");

        this.hasShiny = rootNode.node("has-shiny").getBoolean(true);

        this.calendarMode = rootNode.node("calendar-mode").getString("american");
        this.configVersion = rootNode.node("config-version").getInt(1);
    }

    @Override
    protected void builderOptions(TypeSerializerCollection.Builder builder) {
        //nothing
    }

    @Override
    protected Transformation getTransformation() {
        return new TownyConfigTransformations();
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean createDefaults() {
        return createDefaults;
    }

    public boolean allowDuplicates() {
        return allowDuplicates;
    }

    public String townDuplicatePrefix() {
        return townDuplicatePrefix;
    }

    public String townDuplicateSuffix() {
        return townDuplicateSuffix;
    }

    public String townRarity() {
        return townRarity;
    }

    public String townSeries() {
        return townSeries;
    }

    public String townType() {
        return townType;
    }

    public String nationDuplicatePrefix() {
        return nationDuplicatePrefix;
    }

    public String nationDuplicateSuffix() {
        return nationDuplicateSuffix;
    }

    public String nationRarity() {
        return nationRarity;
    }

    public String nationSeries() {
        return nationSeries;
    }

    public String nationType() {
        return nationType;
    }

    public boolean hasShiny() {
        return hasShiny;
    }

    public int configVersion() {
        return configVersion;
    }

    public String calendarMode() {
        return calendarMode;
    }
}
