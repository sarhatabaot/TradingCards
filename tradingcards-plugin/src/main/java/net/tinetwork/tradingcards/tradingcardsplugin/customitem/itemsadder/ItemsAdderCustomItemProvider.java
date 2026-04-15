package net.tinetwork.tradingcards.tradingcardsplugin.customitem.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.customitem.CustomItemProvider;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ConfiguredMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemsAdderCustomItemProvider implements CustomItemProvider {
    private static final String ID = "itemsadder";

    private final TradingCards plugin;

    public ItemsAdderCustomItemProvider(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String id() {
        return ID;
    }

    @Override
    public boolean isAvailable() {
        return plugin.getServer().getPluginManager().isPluginEnabled("ItemsAdder");
    }

    @Override
    public @Nullable ConfiguredMaterial resolve(final @NotNull String configuredValue) {
        final int separatorIndex = configuredValue.indexOf(':');
        if (separatorIndex <= 0 || separatorIndex == configuredValue.length() - 1) {
            plugin.getLogger().warning(() -> "Invalid ItemsAdder material '%s'. Expected itemsadder:namespace:item_id".formatted(configuredValue));
            return null;
        }

        final String itemId = configuredValue.substring(separatorIndex + 1);
        final CustomStack customStack = CustomStack.getInstance(itemId);
        if (customStack == null) {
            plugin.getLogger().warning(() -> "ItemsAdder item '%s' was not found for configured material '%s'.".formatted(itemId, configuredValue));
            return null;
        }

        return new ConfiguredMaterial(configuredValue, customStack.getItemStack());
    }
}
