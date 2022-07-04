package net.tinetwork.tradingcards.tradingcardsplugin.config.transformations;

import com.github.sarhatabaot.kraken.core.config.Transformation;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import static org.spongepowered.configurate.NodePath.path;

/**
 * @author sarhatabaot
 */
public class GeneralTransformations extends Transformation {
    @Override
    public int getLatestVersion() {
        return 1;
    }

    @Override
    protected ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("config-version")
                .addVersion(0, initialTransformation())
                .build();
    }
}
