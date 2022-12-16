package net.tinetwork.tradingcards.tradingcardsplugin.denylist;

import com.github.sarhatabaot.kraken.core.config.Transformation;
import com.github.sarhatabaot.kraken.core.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.api.denylist.AllowlistMode;
import net.tinetwork.tradingcards.api.denylist.Denylist;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.List;

/**
 * @author ketelsb
 */
public class WorldDenylist extends YamlConfigurateFile<TradingCards> implements Denylist<World> {
    private static final String LISTED_WORLDS_NAME = "worlds";
    private static final String WHITELIST_MODE_NAME = "whitelist-mode";
    private ConfigurationNode worldNode;
    private List<String> listedWorlds;
    private AllowlistMode allowlistMode;

    @Override
    protected void initValues() throws ConfigurateException {
        this.worldNode = rootNode.node(LISTED_WORLDS_NAME);
        loadWorlds();
        setWhitelistMode();
    }

    public WorldDenylist(TradingCards plugin) throws ConfigurateException {
        super(plugin, "lists"+ File.separator, "world-blacklist.yml", "lists");
    }

    @Override
    protected void builderOptions() {
        //No custom type serializer to register
    }

    private void loadWorlds() throws SerializationException {
        listedWorlds = worldNode.getList(String.class);
    }

    private void setWhitelistMode() {
        boolean isWhitelist = rootNode.node(WHITELIST_MODE_NAME).getBoolean();
        if (isWhitelist)
            this.allowlistMode = AllowlistMode.ALLOW;
        else
            this.allowlistMode = AllowlistMode.DENY;
    }

    @Override
    public boolean isAllowed(@NotNull World w) {
        boolean isOnList = listedWorlds.contains(w.getName());

        //If you're not on the blacklist, you're allowed
        if (this.allowlistMode == AllowlistMode.DENY) {
            return !isOnList;
        }

        //If you're on the whitelist, you're allowed
        if (this.allowlistMode == AllowlistMode.ALLOW) {
            return isOnList;
        }
        return false;
    }

    @Override
    public void add(@NotNull World w) {
        listedWorlds.add(w.getName());
        try {
            worldNode.set(null);
            worldNode.set(listedWorlds);
            loader.save(worldNode);
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public void remove(@NotNull World w) {
        listedWorlds.remove(w.getName());
        try {
            worldNode.set(null);
            worldNode.set(listedWorlds);
            loader.save(worldNode);
        } catch (ConfigurateException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public AllowlistMode getMode() {
        return allowlistMode;
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }
}
