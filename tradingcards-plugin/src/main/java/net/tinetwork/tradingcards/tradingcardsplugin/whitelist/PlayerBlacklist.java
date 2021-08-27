package net.tinetwork.tradingcards.tradingcardsplugin.whitelist;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.api.blacklist.WhitelistMode;
import net.tinetwork.tradingcards.api.blacklist.Blacklist;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.List;

/**
 * @author ketelsb
 */
public class PlayerBlacklist extends SimpleConfigurate implements Blacklist<Player> {
    private static final String LISTED_PLAYERS_NAME = "players";
    private static final String WHITELIST_MODE = "whitelist-mode";
    private List<String> listedPlayers;
    private WhitelistMode whitelistMode;

    public PlayerBlacklist(TradingCards plugin) throws ConfigurateException {
        super(plugin, "lists"+ File.separator, "player-blacklist.yml", "lists");
        saveDefaultConfig();
        loadPlayers();
        setWhitelistMode();
    }

    @Override
    protected void registerTypeSerializer() {
        //No custom type serializer to register
    }

    private void loadPlayers() {
        try {
            listedPlayers = rootNode.node(LISTED_PLAYERS_NAME).getList(String.class);
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
    }

    @Override
    public boolean isAllowed(Player p) {
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
    public void add(Player p) {
        final ConfigurationNode playerNode = rootNode.node(LISTED_PLAYERS_NAME);
        listedPlayers.add(p.getName());
        try {
            playerNode.set(null);
            playerNode.set(listedPlayers);
            loader.save(playerNode);
        } catch (ConfigurateException e){
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public void remove(Player p) {
        final ConfigurationNode playerNode = rootNode.node(LISTED_PLAYERS_NAME);
        listedPlayers.remove(p.getName());
        try {
            playerNode.set(null);
            playerNode.set(listedPlayers);
            loader.save(playerNode);
        } catch (ConfigurateException e){
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public WhitelistMode getMode() {
        return whitelistMode;
    }
}
