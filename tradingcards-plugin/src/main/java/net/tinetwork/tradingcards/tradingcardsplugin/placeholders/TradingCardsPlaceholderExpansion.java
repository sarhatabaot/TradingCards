package net.tinetwork.tradingcards.tradingcardsplugin.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sarhatabaot
 */
public class TradingCardsPlaceholderExpansion extends PlaceholderExpansion {
    private TradingCards plugin;

    public TradingCardsPlaceholderExpansion(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tc";
    }

    @Override
    public @NotNull String getAuthor() {
        return "sarhatabaot";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(final OfflinePlayer player, @NotNull final String params) {
        return super.onRequest(player, params);
    }
}
