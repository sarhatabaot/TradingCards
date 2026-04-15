package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ConfiguredMaterial(@NotNull String key, @NotNull ItemStack itemStack) {
    public @NotNull ItemStack createItemStack() {
        return itemStack.clone();
    }

    public @NotNull Material material() {
        return itemStack.getType();
    }
}
