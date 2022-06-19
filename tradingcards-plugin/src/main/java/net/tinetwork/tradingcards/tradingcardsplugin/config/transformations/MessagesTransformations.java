package net.tinetwork.tradingcards.tradingcardsplugin.config.transformations;

import org.spongepowered.configurate.transformation.ConfigurationTransformation;

/**
 * @author sarhatabaot
 */
public class MessagesTransformations extends Transformation{
    @Override
    public int getLatestVersion() {
        return 1;
    }

    @Override
    protected ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .addVersion(0, initialTransformation())
                .build();
    }
}
