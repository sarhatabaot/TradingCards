package media.xen.tradingcards;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class MythicMobsListener
        implements Listener
{
    private final TradingCards plugin;
    Random r = new Random();

    public MythicMobsListener(TradingCards plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        boolean drop = false;
        String worldName = "";
        List< String > worlds = new ArrayList< >();
        if (e.getKiller() instanceof Player) {
            Player p = (Player) e.getKiller();
            drop = ((!plugin.isOnList(p) || plugin.blacklistMode() != 'b') && ((!plugin.isOnList(p) && plugin.blacklistMode() == 'b') || (plugin.isOnList(p) && plugin.blacklistMode() == 'w')));

            worldName = p.getWorld().getName();
            worlds = plugin.getConfig().getStringList("World-Blacklist");
        }
        if (drop && !worlds.contains(worldName)) {
            String rare = plugin.calculateRarity(e.getEntity().getType(), false);
            if (plugin.getConfig().getBoolean("PluginSupport.MythicMobs.Per-Level-Chances")) {
                ActiveMob mob = e.getMob();
                int level = (int) mob.getLevel();
                String oldRarity = rare;
                rare = calculateMMRarity(level, false);
                plugin.debugMsg("[TradingCards] (MM) Mob is a mythic mob of level "+level+", rarity changed from " + oldRarity + " to " + rare);
            } else {
                plugin.debugMsg("[TradingCards] (MM) Per-Level-Chances disabled, continuing as normal.");
            }
            if (plugin.getConfig().getBoolean("Chances.Boss-Drop") && plugin.isMobBoss(e.getEntity().getType())) rare = plugin.getConfig().getString("Chances.Boss-Drop-Rarity");
            boolean cancelled = false;

            if (rare != "None") {
                if (plugin.getConfig().getBoolean("General.Spawner-Block") && e.getEntity().getCustomName() != null && e.getEntity().getCustomName().equals(plugin.getConfig().getString("General.Spawner-Mob-Name"))) {
                    plugin.debugMsg("[TradingCards] (MM) Mob came from spawner, not dropping card.");
                    cancelled = true;
                }
                if (!cancelled) {
                    plugin.debugMsg("[TradingCards] (MM) Successfully generated card.");
                    boolean isShiny = false;
                    int shinyRandom = this.r.nextInt(100) + 1;
                    plugin.debugMsg("[TradingCards] (MM) Shiny chance for level " + (int)e.getMobLevel()+" is "+plugin.getConfig().getInt("PluginSupport.MythicMobs.Levels."+(int)e.getMobLevel()+".Shiny-Version-Chance"));
                    if (shinyRandom <= plugin.getConfig().getInt("PluginSupport.MythicMobs.Levels."+(int)e.getMobLevel()+".Shiny-Version-Chance")) {
                        plugin.debugMsg("[TradingCards] (MM) Card is shiny! Yay!");
                        isShiny = true;
                    }
                    if (plugin.generateCard(rare, isShiny) != null) e.getDrops().add(plugin.generateCard(rare, isShiny));
                }
            }
        }
    }

    public String calculateMMRarity(int mobLvl, boolean alwaysDrop) {
        plugin.debugMsg("[TradingCards] (MM) Mythic mobs: Starting rarity calculation for level "+mobLvl+", alwaysDrop is "+alwaysDrop);
        // Get the max level from the config.
        ConfigurationSection levels = plugin.getConfig().getConfigurationSection("PluginSupport.MythicMobs.Levels");
        Set<String> levelKeys = levels.getKeys(false);
        int finalLvl = 0;
        for (String key : levelKeys) {
            int level = Integer.valueOf(key);
            if (level == mobLvl) {
                if(level >= finalLvl) {
                    finalLvl = level;
                }
                plugin.debugMsg("[TradingCards] (MM) Mythic mobs: Correct level is: " + level);
            } else {
                if(level >= finalLvl && level <= mobLvl) {
                    finalLvl = level; 
                }
                plugin.debugMsg("[TradingCards] (MM) Mythic mobs: Not the correct level.. iteration is: " + level);
            }
        }
        
        int shouldItDrop = this.r.nextInt(100) + 1;
        String type = "";
        plugin.debugMsg("[TradingCards] (MM) shouldItDrop Num: " + shouldItDrop);
        if (!alwaysDrop) {
            if (shouldItDrop > plugin.getConfig().getInt("PluginSupport.MythicMobs.Levels."+finalLvl+".Drop-Chance")) return "None";
            type = "MythicMob";
        } else {
            type = "MythicMobs";
        }

        ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("Rarities");
        // Name of each rarity!
        Set < String > rarityKeys = rarities.getKeys(false);
        Map < String,
                Integer > rarityChances = new HashMap < >();
        Map < Integer,
                String > rarityIndexes = new HashMap < >();
        int i = 0;
        int mini = 0;
        int random = this.r.nextInt(100000) + 1;
        plugin.debugMsg("[TradingCards] (MM) Random Card Num: " + random);
        plugin.debugMsg("[TradingCards] (MM) Type: " + type);
        // Loop through the name of each rarity..
        for (String key: rarityKeys) {
            // Put an ID (starting with 0) and the name of the rarity into rarityIndexes.
            rarityIndexes.put(Integer.valueOf(i), key);
            i++;
            plugin.debugMsg("[TradingCards] (MM) " + i + ", " + key);
            if (plugin.getConfig().contains("PluginSupport.MythicMobs.Levels." + finalLvl + ".Rarities." + key) && mini == 0) {
                plugin.debugMsg("[TradingCards] (MM) Path exists: "+"PluginSupport.MythicMobs.Levels." + finalLvl + ".Rarities." + key);
                plugin.debugMsg("[TradingCards] (MM) Mini: " + i);
                mini = i;
            }
            /*int chance = plugin.getConfig().getInt("Chances." + key + "." + type, -1);
            plugin.debugMsg("[TradingCards] (MM) Keys: " + key + ", " + chance + ", i=" + i);
            rarityChances.put(key, Integer.valueOf(chance));*/
        }
        if (mini != 0) {
            plugin.debugMsg("[TradingCards] (MM) Mini: " + mini);
            plugin.debugMsg("[TradingCards] (MM) i: " + i);
            while (i >= mini) {
                i--;
                plugin.debugMsg("[TradingCards] (MM) i: " + i);
                int chance = plugin.getConfig().getInt("PluginSupport.MythicMobs.Levels." + finalLvl + ".Rarities." + rarityIndexes.get(Integer.valueOf(i)), -1);
                plugin.debugMsg("[TradingCards] (MM) Chance: " + chance);
                plugin.debugMsg("[TradingCards] (MM) Rarity: " + rarityIndexes.get(Integer.valueOf(i)));
                if (chance > 0) {
                    plugin.debugMsg("[TradingCards] (MM) Chance > 0");
                    if (random <= chance) {
                        plugin.debugMsg("[TradingCards] (MM) Random <= Chance, returning "+rarityIndexes.get(Integer.valueOf(i)));
                        return rarityIndexes.get(Integer.valueOf(i));
                    }
                }
            }
        } else {
            while (i > 0) {
                i--;
                plugin.debugMsg("[TradingCards] (MM) Final loop iteration " + i);
                plugin.debugMsg("[TradingCards] (MM) Iteration " + i + " in HashMap is: " + rarityIndexes.get(Integer.valueOf(i)) + ", " + plugin.getConfig().getString("Rarities." + rarityIndexes.get(Integer.valueOf(i)) + ".Name"));
                int chance = plugin.getConfig().getInt("PluginSupport.MythicMobs.Levels." + finalLvl + ".Rarities." + rarityIndexes.get(Integer.valueOf(i)), -1);
                plugin.debugMsg("[TradingCards] (MM) " + plugin.getConfig().getString("Rarities." + rarityIndexes.get(Integer.valueOf(i)) + ".Name") + "'s chance of dropping: " + chance + " out of 100,000");
                plugin.debugMsg("[TradingCards] (MM) The random number we're comparing that against is: " + random);
                if (chance > 0 && random <= chance) {
                    plugin.debugMsg("[TradingCards] (MM) Yup, looks like " + random + " is definitely lower than " + chance + "!");
                    plugin.debugMsg("[TradingCards] (MM) Giving a " + plugin.getConfig().getString("Rarities." + rarityIndexes.get(Integer.valueOf(i)) + ".Name") + " card.");
                    return rarityIndexes.get(Integer.valueOf(i));
                }
            }
        }
        return "None";
    }
}

