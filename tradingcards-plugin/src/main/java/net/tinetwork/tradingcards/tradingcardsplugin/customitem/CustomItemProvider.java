package net.tinetwork.tradingcards.tradingcardsplugin.customitem;

import net.tinetwork.tradingcards.tradingcardsplugin.utils.ConfiguredMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomItemProvider {
    @NotNull String id();

    boolean isAvailable();

    @Nullable ConfiguredMaterial resolve(@NotNull String configuredValue);
}
