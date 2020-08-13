package media.xen.tradingcards.listeners;


import com.garbagemule.MobArena.framework.Arena;
import media.xen.tradingcards.TradingCards;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DropListener extends SimpleListener {
	public DropListener(final TradingCards plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (plugin.getConfig().getBoolean("General.Player-Drops-Card") && plugin.getConfig().getBoolean("General.Auto-Add-Players")) {
			Player player = e.getEntity().getKiller();
			if (player != null) {
				ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("Rarities");
				Set<String> rarityKeys = rarities.getKeys(false);
				String k = null;

				for (final String key : rarityKeys) {
					if(plugin.getCardsConfig().getConfig().contains("Cards." + key + "." + e.getEntity().getName())) {
						plugin.debug(key);
						k = key;
					}
				}

				if (k != null) {
					int rndm = plugin.r.nextInt(100) + 1;
					if (rndm <= plugin.getConfig().getInt("General.Player-Drops-Card-Rarity")) {
						e.getDrops().add(plugin.createPlayerCard(e.getEntity().getName(), k, 1, false));
						plugin.debug(e.getDrops().toString());
					}
				} else {
					plugin.getLogger().info("k is null");
				}
			}
		}

	}
}
