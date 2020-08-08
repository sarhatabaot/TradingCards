package media.xen.tradingcards;

import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.GregorianCalendar;

public class TownyListener
        implements Listener
{
    public static TradingCards plugin;

    public TownyListener(TradingCards plugin)
    {
        plugin = plugin;
    }

    @EventHandler
    public void onNewTown(NewTownEvent e)
    {
        if (plugin.getConfig().getBoolean("PluginSupport.Towny.Towny-Enabled")) {
            if (plugin.getServer().getPluginManager().getPlugin("Towny") != null)
            {
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] Towny detected, starting card creation..");
                }
                GregorianCalendar gc = new GregorianCalendar();

                gc.setTimeInMillis(System.currentTimeMillis());
                int date = gc.get(5);
                int month = gc.get(2) + 1;
                int year = gc.get(1);
                String townRarity = plugin.getConfig().getString("PluginSupport.Towny.Town-Rarity");
                String townName = e.getTown().getName();
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + townName);
                }
                String townSeries = plugin.getConfig().getString("PluginSupport.Towny.Town-Series");
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + townSeries);
                }
                String townType = plugin.getConfig().getString("PluginSupport.Towny.Town-Type");
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + townType);
                }
                boolean hasShiny = plugin.getConfig().getBoolean("PluginSupport.Towny.Has-Shiny");
                String prefix = plugin.getConfig().getString("General.Card-Prefix");
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + prefix);
                }
                if (plugin.getConfig().contains("Cards." + townRarity + "." + townName))
                {
                    System.out.println("[TradingCards] Town already exists!");
                    if (plugin.getConfig().getBoolean("PluginSupport.Towny.Allow-Duplicates"))
                    {
                        int num = 1;
                        String dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
                        String dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));
                        while (plugin.getConfig().contains("Cards." + townRarity + "." + dPrefix + townName + dSuffix))
                        {
                            num++;
                            dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
                            dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));
                            if (num > 100)
                            {
                                System.out.println("[TradingCards] Something went wrong!");
                                break;
                            }
                        }
                        if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                            System.out.println("[TradingCards] Let's do this!");
                        }
                        Player p = Bukkit.getPlayer(e.getTown().getMayor().getName());
                        if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                            System.out.println("[TradingCards] Mayor name: " + e.getTown().getMayor().getName());
                        }
                        String townInfo = "";
                        if (plugin.getConfig().getBoolean("General.American-Mode")) {
                            townInfo = "Created " + month + "/" + date + "/" + year;
                        } else {
                            townInfo = "Created " + date + "/" + month + "/" + year;
                        }
                        plugin.createCard(p, townRarity, dPrefix + townName + dSuffix, townSeries, townType, hasShiny, townInfo, "Founder: " + p.getName());
                    }
                }
                else
                {
                    if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                        System.out.println("[TradingCards] Let's do this!");
                    }
                    Player p = Bukkit.getPlayer(e.getTown().getMayor().getName());
                    if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                        System.out.println("[TradingCards] Mayor name: " + e.getTown().getMayor().getName());
                    }
                    String townInfo = "";
                    if (plugin.getConfig().getBoolean("General.American-Mode")) {
                        townInfo = "Created " + month + "/" + date + "/" + year;
                    } else {
                        townInfo = "Created " + date + "/" + month + "/" + year;
                    }
                    plugin.createCard(p, townRarity, townName, townSeries, townType, hasShiny, townInfo, "Founder: " + p.getName());
                }
            }
            else
            {
                System.out.println("[TradingCards] Cannot detect Towny!");
            }
        }
    }

    @EventHandler
    public void onNewNation(NewNationEvent e)
    {
        if (plugin.getConfig().getBoolean("PluginSupport.Towny.Towny-Enabled")) {
            if (plugin.getServer().getPluginManager().getPlugin("Towny") != null)
            {
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] Towny detected, starting card creation..");
                }
                GregorianCalendar gc = new GregorianCalendar();

                gc.setTimeInMillis(System.currentTimeMillis());
                int date = gc.get(5);
                int month = gc.get(2) + 1;
                int year = gc.get(1);
                String townRarity = plugin.getConfig().getString("PluginSupport.Towny.Nation-Rarity");
                String townName = e.getNation().getName();
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + townName);
                }
                String townSeries = plugin.getConfig().getString("PluginSupport.Towny.Nation-Series");
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + townSeries);
                }
                String townType = plugin.getConfig().getString("PluginSupport.Towny.Nation-Type");
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + townType);
                }
                boolean hasShiny = plugin.getConfig().getBoolean("PluginSupport.Towny.Has-Shiny");
                String prefix = plugin.getConfig().getString("General.Card-Prefix");
                if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                    System.out.println("[TradingCards] " + prefix);
                }
                if (plugin.getConfig().contains("Cards." + townRarity + "." + townName))
                {
                    System.out.println("[TradingCards] Nation already exists!");
                    if (plugin.getConfig().getBoolean("PluginSupport.Towny.Allow-Duplicates"))
                    {
                        int num = 1;
                        String dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
                        String dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));
                        while (plugin.getConfig().contains("Cards." + townRarity + "." + dPrefix + townName + dSuffix))
                        {
                            num++;
                            dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
                            dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));
                            if (num > 100)
                            {
                                System.out.println("[TradingCards] Something went wrong!");
                                break;
                            }
                        }
                        if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                            System.out.println("[TradingCards] Let's do this!");
                        }
                        Player p = Bukkit.getPlayer(e.getNation().getCapital().getMayor().getName());
                        if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                            System.out.println("[TradingCards] Emperor name: " + e.getNation().getCapital().getMayor().getName());
                        }
                        String townInfo = "";
                        if (plugin.getConfig().getBoolean("General.American-Mode")) {
                            townInfo = "Created " + month + "/" + date + "/" + year;
                        } else {
                            townInfo = "Created " + date + "/" + month + "/" + year;
                        }
                        plugin.createCard(p, townRarity, dPrefix + townName + dSuffix, townSeries, townType, hasShiny, townInfo, "Founder: " + p.getName());
                    }
                }
                else
                {
                    if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                        System.out.println("[TradingCards] Let's do this!");
                    }
                    Player p = Bukkit.getPlayer(e.getNation().getCapital().getMayor().getName());
                    if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
                        System.out.println("[TradingCards] Emperor name: " + e.getNation().getCapital().getMayor().getName());
                    }
                    String townInfo = "";
                    if (plugin.getConfig().getBoolean("General.American-Mode")) {
                        townInfo = "Created " + month + "/" + date + "/" + year;
                    } else {
                        townInfo = "Created " + date + "/" + month + "/" + year;
                    }
                    plugin.createCard(p, townRarity, townName, townSeries, townType, hasShiny, townInfo, "Founder: " + p.getName());
                }
            }
            else
            {
                System.out.println("[TradingCards] Cannot detect Towny!");
            }
        }
    }
}

