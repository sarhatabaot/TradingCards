package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs;


import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.chances.LevelChances;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsListener implements Listener {
    private TradingCards tradingCards;
    private MythicMobsConfig mythicMobsConfig;

    public MythicMobsListener(TradingCards tradingCards, MythicMobsConfig mythicMobsConfig) {
        this.tradingCards = tradingCards;
        this.mythicMobsConfig = mythicMobsConfig;
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        double mobLevel = e.getMobLevel();
        //we should probably cancel the drop for the normal mob death event...
    }

    private LevelChances getQualifiedLevel(int mobLevel) {
        return mythicMobsConfig.levelChances().keySet().stream().filter(level -> level <= mobLevel).max(Integer::compare).map(mythicMobsConfig::getChances).orElse(null); //needs testing
    }
}
