package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs;


import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class MythicMobsUtil {
    private MythicMobsUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean isMythicMob(final Entity entity) {
        if (!Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            return false;
        }

        return MythicBukkit.inst().getMobManager().isMythicMob(entity);
    }
}
