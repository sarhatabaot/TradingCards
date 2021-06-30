package net.tinetwork.tradingcards.tradingcardsapi.manager;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class PackManager {
    public abstract Map<String,ItemStack> packs();

    public ItemStack getPackItem(String name) {
        return packs().get(name);
    }

    public abstract ItemStack generatePack(String name);
}
