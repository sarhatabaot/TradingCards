package net.tinetwork.tradingcards.tradingcardsplugin.config.transformations;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import static org.spongepowered.configurate.NodePath.path;

/**
 * @author sarhatabaot
 */
public abstract class Transformation {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    //Adds an initial config version
    public ConfigurationTransformation initialTransformation() {
        return ConfigurationTransformation.builder()
                // For every direct child of the `section` node, set the value of its child `new-value` to something
                .addAction(path("", ConfigurationTransformation.WILDCARD_OBJECT), (path, value) -> {
                    value.node("config-version").set(0);

                    return null; // don't move the value
                })
                .build();
    }

    public abstract int getLatestVersion();

    protected abstract ConfigurationTransformation.Versioned create();

    /**
     * Apply the transformations to a node.
     *
     * <p>This method also prints information about the version update that
     * occurred</p>
     *
     * @param node the node to transform
     * @param <N> node type
     * @return provided node, after transformation
     */
    public <N extends ConfigurationNode> N updateNode(final @NotNull N node) throws ConfigurateException {
        if (!node.virtual()) { // we only want to migrate existing data
            final ConfigurationTransformation.Versioned trans = create();
            final int startVersion = trans.version(node);
            trans.apply(node);
            final int endVersion = trans.version(node);
            if (startVersion != endVersion) { // we might not have made any changes
                logger.info("Updated config schema from %d to %d".formatted(startVersion, endVersion));
            }
        }
        return node;
    }
}
