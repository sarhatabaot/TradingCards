package net.tinetwork.tradingcards.tradingcardsplugin.config.transformations;

import com.lapzupi.dev.config.Transformation;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.Storage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import static org.spongepowered.configurate.NodePath.path;

/**
 * @author sarhatabaot
 */
public class StorageTransformations extends Transformation {
    @Override
    public int getLatestVersion() {
        return 2;
    }

    @Override
    protected ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("config-version")
                .addVersion(0, initialTransformation())
                .addVersion(1, addDefaultMigrationId())
                .addVersion(2,addSqlFirstTime())
                .build();
    }

    @Contract(" -> new")
    private @NotNull ConfigurationTransformation addDefaultMigrationId() {
        return ConfigurationTransformation.builder()
                .addAction(path("database-migration", ConfigurationTransformation.WILDCARD_OBJECT), (path, value) -> {
                    value.node("default-series-id").set("default");
                    return null;
                })
                .build();
    }

    @Contract(" -> new")
    private @NotNull ConfigurationTransformation addSqlFirstTime() {
        return ConfigurationTransformation.builder()
                .addAction(path("sql", ConfigurationTransformation.WILDCARD_OBJECT), (path, value) -> {
                    value.node("first-time-values").set(Storage.Sql.FIRST_TIME_VALUES);
                    return null;
                })
                .build();
    }
}
