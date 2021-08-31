package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.model.Pack;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;

public abstract class PackManager {
    public abstract Map<String,ItemStack> packs();

    public ItemStack getPackItem(String name) {
        return packs().get(name);
    }

    public abstract ItemStack generatePack(String name) throws SerializationException;

    public abstract boolean isPack(final ItemStack item);

    public abstract Pack getPack(final String id);

}
