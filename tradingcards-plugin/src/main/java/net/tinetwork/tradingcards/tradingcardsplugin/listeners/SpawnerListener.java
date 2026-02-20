package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public class SpawnerListener extends SimpleListener {
    public SpawnerListener(final @NotNull TradingCards plugin) {
        super(plugin);
    }

    @EventHandler
    public void onMobSpawn(@NotNull SpawnerSpawnEvent event) {
        NBT.modifyPersistentData(event.getEntity(), nbt -> {
            nbt.getOrCreateCompound(NbtUtils.TC_COMPOUND).setBoolean(NbtUtils.TC_SPAWNER_MOB, true);
        });
        debug("Set key %s to true on entity %s @ %s ".formatted(NbtUtils.TC_SPAWNER_MOB, event.getEntityType(), event.getLocation().toString()));
    }

}
