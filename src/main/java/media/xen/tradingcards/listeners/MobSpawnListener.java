package media.xen.tradingcards.listeners;


import media.xen.tradingcards.TradingCards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener extends SimpleListener{
	public MobSpawnListener(final TradingCards plugin) {
		super(plugin);
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e) {
		if (!(e.getEntity() instanceof Player) && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER && plugin.getConfig().getBoolean("General.Spawner-Block")) {
			e.getEntity().setCustomName(plugin.getConfig().getString("General.Spawner-Mob-Name"));
			debug("Spawner mob renamed.");
			e.getEntity().setRemoveWhenFarAway(true);
		}

	}
}
