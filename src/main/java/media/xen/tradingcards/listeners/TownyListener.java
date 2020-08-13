package media.xen.tradingcards.listeners;

import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import media.xen.tradingcards.TradingCards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListener extends SimpleListener {
	public TownyListener(final TradingCards plugin) {
		super(plugin);
	}

	@EventHandler
	public void onNewTown(NewTownEvent e) {
		if (!plugin.getConfig().getBoolean("PluginSupport.Towny.Towny-Enabled")) {
			return;
		}

		if (plugin.getServer().getPluginManager().getPlugin("Towny") == null) {
			warning("Could not detect Towny!");
			return;
		}

		debug("Towny detected, starting card creation..");

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(System.currentTimeMillis());
		int date = gc.get(Calendar.DATE);
		int month = gc.get(Calendar.MONTH) + 1;
		int year = gc.get(Calendar.YEAR);
		String townRarity = plugin.getConfig().getString("PluginSupport.Towny.Town-Rarity");
		String townName = e.getTown().getName();
		String townSeries = plugin.getConfig().getString("PluginSupport.Towny.Town-Series");
		String townType = plugin.getConfig().getString("PluginSupport.Towny.Town-Type");
		debug(townName);
		debug(townSeries);
		debug(townType);

		boolean hasShiny = plugin.getConfig().getBoolean("PluginSupport.Towny.Has-Shiny");
		String prefix = plugin.getConfig().getString("General.Card-Prefix");
		debug(prefix);

		String dPrefix;
		if (plugin.getConfig().contains("Cards." + townRarity + "." + townName)) {
			info("Town already exists!");
			if (plugin.getConfig().getBoolean("PluginSupport.Towny.Allow-Duplicates")) {
				int num = 1;
				dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
				String dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));

				while (plugin.getConfig().contains("Cards." + townRarity + "." + dPrefix + townName + dSuffix)) {
					++num;
					dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
					dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Town-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));
					if (num > 100) {
						warning("Something went wrong!");
						break;
					}
				}


				Player p = Bukkit.getPlayer(e.getTown().getMayor().getName());
				debug("Mayor name: " + e.getTown().getMayor().getName());


				String townInfo = getCalendarMode(month, date, year);
				plugin.createCard(p, townRarity, dPrefix + townName + dSuffix, townSeries, townType, hasShiny, townInfo, "Founder: " + p.getName());
				return;
			}
		}

		Player p = Bukkit.getPlayer(e.getTown().getMayor().getName());
		debug("Mayor name: " + e.getTown().getMayor().getName());

		dPrefix = getCalendarMode(month, date, year);

		plugin.createCard(p, townRarity, townName, townSeries, townType, hasShiny, dPrefix, "Founder: " + p.getName());

	}

	private String getCalendarMode(final int month, final int date, final int year) {
		if (plugin.getConfig().getBoolean("General.American-Mode")) {
			return "Created " + month + "/" + date + "/" + year;
		}

		return "Created " + date + "/" + month + "/" + year;
	}

	@EventHandler
	public void onNewNation(NewNationEvent e) {
		if (!plugin.getConfig().getBoolean("PluginSupport.Towny.Towny-Enabled")) {
			return;
		}

		if (plugin.getServer().getPluginManager().getPlugin("Towny") == null) {
			warning("Could not detect Towny!");
			return;
		}


		if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
			System.out.println("[Cards] Towny detected, starting card creation..");
		}

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(System.currentTimeMillis());
		int date = gc.get(Calendar.DATE);
		int month = gc.get(Calendar.MONTH) + 1;
		int year = gc.get(Calendar.YEAR);
		String townRarity = plugin.getConfig().getString("PluginSupport.Towny.Nation-Rarity");
		String townName = e.getNation().getName();
		if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
			System.out.println("[Cards] " + townName);
		}

		String townSeries = plugin.getConfig().getString("PluginSupport.Towny.Nation-Series");
		if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
			System.out.println("[Cards] " + townSeries);
		}

		String townType = plugin.getConfig().getString("PluginSupport.Towny.Nation-Type");
		if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
			System.out.println("[Cards] " + townType);
		}

		boolean hasShiny = plugin.getConfig().getBoolean("PluginSupport.Towny.Has-Shiny");
		String prefix = plugin.getConfig().getString("General.Card-Prefix");
		if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
			System.out.println("[Cards] " + prefix);
		}

		String dPrefix;
		if (plugin.getConfig().contains("Cards." + townRarity + "." + townName)) {
			System.out.println("[Cards] Nation already exists!");
			if (plugin.getConfig().getBoolean("PluginSupport.Towny.Allow-Duplicates")) {
				int num = 1;
				dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
				String dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));

				while (plugin.getConfig().contains("Cards." + townRarity + "." + dPrefix + townName + dSuffix)) {
					++num;
					dPrefix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Prefix").replaceAll("%num%", String.valueOf(num));
					dSuffix = plugin.getConfig().getString("PluginSupport.Towny.Nation-Duplicate-Suffix").replaceAll("%num%", String.valueOf(num));
					if (num > 100) {
						System.out.println("[Cards] Something went wrong!");
						break;
					}
				}


				Player p = Bukkit.getPlayer(e.getNation().getCapital().getMayor().getName());
				if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
					System.out.println("[Cards] Emperor name: " + e.getNation().getCapital().getMayor().getName());
				}

				String townInfo = getCalendarMode(month,date,year);

				plugin.createCard(p, townRarity, dPrefix + townName + dSuffix, townSeries, townType, hasShiny, townInfo, "Founder: " + p.getName());
			}
			return;
		}


		Player p = Bukkit.getPlayer(e.getNation().getCapital().getMayor().getName());
		if (plugin.getConfig().getBoolean("General.Debug-Mode")) {
			System.out.println("[Cards] Emperor name: " + e.getNation().getCapital().getMayor().getName());
		}

		dPrefix = getCalendarMode(month,date,year);

		plugin.createCard(p, townRarity, townName, townSeries, townType, hasShiny, dPrefix, "Founder: " + p.getName());


	}
}