package media.xen.tradingcards.listeners;

import com.garbagemule.MobArena.framework.Arena;
import media.xen.tradingcards.TradingCards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;


@Deprecated
public class MobArenaListener extends SimpleListener {
	public MobArenaListener(final TradingCards plugin) {
		super(plugin);
	}


	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		boolean drop = false;
		String worldName = "";
		List<String> worlds = new ArrayList<>();

		if (e.getEntity().getKiller() != null) {
			Player p = e.getEntity().getKiller();
			drop = (!plugin.isOnList(p) || plugin.blacklistMode() != 'b') && (!plugin.isOnList(p) && plugin.blacklistMode() == 'b' || plugin.isOnList(p) && plugin.blacklistMode() == 'w');
			worldName = p.getWorld().getName();
			worlds = plugin.getConfig().getStringList("World-Blacklist");
			if (plugin.hasMobArena) {
				int i = 0;

				plugin.debug("Mob Arena checks starting.");

				if (plugin.am.getArenas() != null && !plugin.am.getArenas().isEmpty()) {
					plugin.debug("There is at least 1 arena!");

					for(Arena arena: plugin.am.getArenas()) {
						++i;
						plugin.debug("For arena #" + i + "...");
						plugin.debug(" In arena?: " + arena.inArena(p));
						if (!arena.inArena(p) && !arena.inLobby(p)) {
							plugin.debug("Killer is not in this arena!");
						} else {
							plugin.debug("Killer is in an arena/lobby, so let's mess with the drops.");

							if (plugin.getConfig().getBoolean("PluginSupport.MobArena.Disable-In-Arena")) {
								drop = false;
							}

							plugin.debug("Drops are now: " + drop);
						}
					}

				}
			}
		}

		if (drop && ! worlds.contains(worldName)) {
			String rare = plugin.calculateRarity(e.getEntityType(), false);
			if (plugin.getConfig().getBoolean("Chances.Boss-Drop") && plugin.isMobBoss(e.getEntityType())) {
				rare = plugin.getConfig().getString("Chances.Boss-Drop-Rarity");
			}

			boolean cancelled = false;
			if (!rare.equalsIgnoreCase("None")) {
				if (plugin.getConfig().getBoolean("General.Spawner-Block") && e.getEntity().getCustomName() != null && e.getEntity().getCustomName().equals(plugin.getConfig().getString("General.Spawner-Mob-Name"))) {
					plugin.debug("Mob came from spawner, not dropping card.");
					cancelled = true;
				}

				if (!cancelled) {
					plugin.debug("Successfully generated card.");
					if (plugin.generateRandomCard(rare, false) != null) {
						e.getDrops().add(plugin.generateRandomCard(rare, false));
					}
				}
			}
		}

	}
}