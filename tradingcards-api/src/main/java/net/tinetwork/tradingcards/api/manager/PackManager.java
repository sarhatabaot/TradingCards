package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.model.Pack;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Map;

public interface PackManager {
    Map<String,ItemStack> getCachedPacksItemstacks();

    ItemStack getPackItem(String name);

    ItemStack generatePack(String name) throws SerializationException;

    boolean isPack(final ItemStack item);

    Pack getPack(final String id);

    List<Pack> getPacks();

    boolean containsPack(final String packId);

}
