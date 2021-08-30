package net.tinetwork.tradingcards.tradingcardsplugin.core;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;

public abstract class SimpleConfigurate extends SimpleConfigFile{
    protected final YamlConfigurationLoader.Builder loaderBuilder = YamlConfigurationLoader.builder().
            path(Paths.get(folder+"/"+fileName));
    protected final YamlConfigurationLoader loader;
    protected CommentedConfigurationNode rootNode;

    public SimpleConfigurate(TradingCards plugin, final String resourcePath, String fileName, String folder) throws ConfigurateException {
        super(plugin, resourcePath,fileName, folder);
        preLoaderBuild();
        this.loader = loaderBuilder.build();
        this.rootNode = loader.load();
    }

    protected abstract void preLoaderBuild();

    @Override
    public void reloadConfig()  {
        try {
            this.rootNode = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
}
