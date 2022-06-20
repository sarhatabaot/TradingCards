package net.tinetwork.tradingcards.tradingcardsplugin.config.transformations;

import com.github.sarhatabaot.kraken.core.config.Transformation;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

/**
 * @author sarhatabaot
 */
public class ChancesTransformations extends Transformation {
    @Override
    protected ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("config-version")
                .addVersion(0, initialTransformation())
                .build();
    }

    @Override
    public int getLatestVersion() {
        return 0;
    }
}
