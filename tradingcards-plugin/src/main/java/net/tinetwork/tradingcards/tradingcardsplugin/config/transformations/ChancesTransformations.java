package net.tinetwork.tradingcards.tradingcardsplugin.config.transformations;

import org.spongepowered.configurate.transformation.ConfigurationTransformation;

/**
 * @author sarhatabaot
 */
public class ChancesTransformations extends Transformation{
    @Override
    protected ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .addVersion(0, initialTransformation())
                .build();
    }

    @Override
    public int getLatestVersion() {
        return 1;
    }
}
