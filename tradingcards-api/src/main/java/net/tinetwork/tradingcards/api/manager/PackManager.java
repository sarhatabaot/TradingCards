package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.model.pack.Pack;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Map;

public interface PackManager {
    Map<String,ItemStack> getCachedPacksItemStacks();

    ItemStack getPackItem(String name);

    ItemStack generatePack(String name) throws SerializationException;

    ItemStack generatePack(Pack pack) throws SerializationException;

    boolean isPack(final ItemStack item);

    Pack getPack(final String id);

    List<Pack> getPacks();

    List<String> getPackIds();

    boolean containsPack(final String packId);

}
