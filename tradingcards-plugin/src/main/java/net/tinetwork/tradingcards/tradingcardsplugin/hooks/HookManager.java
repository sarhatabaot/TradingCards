package net.tinetwork.tradingcards.tradingcardsplugin.hooks;


import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mobarena.MobArenaHook;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.MythicMobsHook;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.towny.TownyHook;

import java.util.Map;
import java.util.WeakHashMap;

public class HookManager {
    // 3 states in the command, enabled, disabled, and could not register
    private final TradingCards tradingCards;
    private WeakHashMap<String, PluginHook> hooks;

    public HookManager(final TradingCards plugin) {
        this.tradingCards = plugin;
        this.hooks = new WeakHashMap<>();
        this.hooks.put("Towny", new TownyHook(tradingCards));
        this.hooks.put("MythicMobs", new MythicMobsHook(tradingCards));
        this.hooks.put("MobArena", new MobArenaHook(tradingCards));
    }

    public void load() {
        for (Map.Entry<String, PluginHook> hook : hooks.entrySet()) {
            hook.getValue().register();
        }
    }
}
