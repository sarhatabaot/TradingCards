package net.tinetwork.tradingcards.tradingcardsplugin.whitelist;

import com.github.sarhatabaot.kraken.core.config.ConfigurateFile;
import net.tinetwork.tradingcards.api.blacklist.Blacklist;
import net.tinetwork.tradingcards.api.blacklist.WhitelistMode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ketelsb
 */
public class PlayerBlacklist extends ConfigurateFile<TradingCards> implements Blacklist<Player> {
    private static final String LISTED_PLAYERS_NAME = "players";
    private static final String WHITELIST_MODE = "whitelist-mode";
    private List<String> listedPlayers;
    private WhitelistMode whitelistMode;

    public PlayerBlacklist(TradingCards plugin) throws ConfigurateException {
        super(plugin, "lists" + File.separator, "player-blacklist.yml", "lists");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        loadPlayers();
        setWhitelistMode();
        loadYamlConfiguration();
    }

    @Override
    protected void preLoaderBuild() {
        //No custom type serializer to register
    }

    private void loadYamlConfiguration() {
        if (file == null) {
            file = new File(folder, fileName);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    private void loadPlayers() {
        try {
            listedPlayers = new ArrayList<>(rootNode.node(LISTED_PLAYERS_NAME).getList(String.class));
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    private void setWhitelistMode() {
        boolean isWhitelist = rootNode.node(WHITELIST_MODE).getBoolean();
        if (isWhitelist)
            this.whitelistMode = WhitelistMode.WHITELIST;
        else
            this.whitelistMode = WhitelistMode.BLACKLIST;

        plugin.debug(getClass(), InternalDebug.WHITELIST_MODE.formatted(whitelistMode));
    }

    @Override
    public boolean isAllowed(@NotNull Player p) {
        boolean isOnList = listedPlayers.contains(p.getName());

        //If you're not on the blacklist, you're allowed
        if (this.whitelistMode == WhitelistMode.BLACKLIST) {
            return !isOnList;
        }

        //If you're on the whitelist, you're allowed
        if (this.whitelistMode == WhitelistMode.WHITELIST) {
            return isOnList;
        }

        return false;
    }

    @Override
    public void add(@NotNull Player p) {
        listedPlayers.add(p.getName());
        getConfig().set(LISTED_PLAYERS_NAME, listedPlayers);
    }

    @Override
    public void remove(@NotNull Player p) {
        listedPlayers.remove(p.getName());
        getConfig().set(LISTED_PLAYERS_NAME, listedPlayers);
    }

    @Override
    public WhitelistMode getMode() {
        return whitelistMode;
    }
}
