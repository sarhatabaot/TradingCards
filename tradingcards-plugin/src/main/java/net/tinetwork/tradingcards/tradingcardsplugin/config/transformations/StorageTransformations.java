package net.tinetwork.tradingcards.tradingcardsplugin.config.transformations;

import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import static org.spongepowered.configurate.NodePath.path;

/**
 * @author sarhatabaot
 */
public class StorageTransformations extends Transformation{
    @Override
    public int getLatestVersion() {
        return 1;
    }

    @Override
    protected ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .addVersion(0, initialTransformation())
                .addVersion(1, addDefaultMigrationId())
                .build();
    }

    private ConfigurationTransformation addDefaultMigrationId() {
        return ConfigurationTransformation.builder()
                .addAction(path("database-migration", ConfigurationTransformation.WILDCARD_OBJECT), (path, value) -> {
                    value.node("default-series-id").set("default");
                    return null;
                })
                .build();
    }
}
