package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.towny;


import com.lapzupi.dev.config.Transformation;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;


public class TownyConfigTransformations extends Transformation {

    @Override
    public int getLatestVersion() {
        return 1;
    }

    @Override
    protected ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("config-version")
                .addVersion(0, initialTransformation())
                .addVersion(1, addCreateDefaults())
                .build();
    }

    private ConfigurationTransformation addCreateDefaults() {
        return ConfigurationTransformation.builder()
                .addAction(NodePath.path("", ConfigurationTransformation.WILDCARD_OBJECT), (path, value) -> {
                    value.node("create-defaults").set(true);
                    return null;
                })
                .build();
    }

}
