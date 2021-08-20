package net.tinetwork.tradingcards.tradingcardsplugin.core;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;

public class SimpleConfigurate extends SimpleConfigFile{
    protected final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get(folder+"/"+fileName)).build();
    protected CommentedConfigurationNode rootNode;

    public SimpleConfigurate(TradingCards plugin, String fileName, String folder) throws ConfigurateException {
        super(plugin, fileName, folder);
        this.rootNode = loader.load();
    }

    @Override
    public void reloadConfig()  {
        try {
            this.rootNode = loader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }
}
