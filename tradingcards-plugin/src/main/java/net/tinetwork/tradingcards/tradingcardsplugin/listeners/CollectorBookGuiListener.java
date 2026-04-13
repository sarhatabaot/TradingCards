package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.gui.CollectorBookGuiManager;
import org.jetbrains.annotations.NotNull;

public class CollectorBookGuiListener extends SimpleListener {
    public CollectorBookGuiListener(final @NotNull TradingCards plugin, final @NotNull CollectorBookGuiManager collectorBookGuiManager) {
        super(plugin);
    }
}
