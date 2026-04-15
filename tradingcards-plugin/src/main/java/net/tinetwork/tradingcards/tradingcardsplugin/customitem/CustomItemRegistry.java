package net.tinetwork.tradingcards.tradingcardsplugin.customitem;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.customitem.itemsadder.ItemsAdderCustomItemIntegration;
import net.tinetwork.tradingcards.tradingcardsplugin.customitem.itemsadder.ItemsAdderCustomItemProvider;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ConfiguredMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class CustomItemRegistry {
    private final TradingCards plugin;
    private final Map<String, CustomItemProvider> providers = new LinkedHashMap<>();
    private final Map<String, CustomItemIntegration> integrations = new LinkedHashMap<>();

    public CustomItemRegistry(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
        register(
                new ItemsAdderCustomItemProvider(plugin),
                new ItemsAdderCustomItemIntegration(plugin)
        );
    }

    public void registerListeners() {
        integrations.values().stream()
                .filter(CustomItemIntegration::isAvailable)
                .forEach(CustomItemIntegration::register);
    }

    public @NotNull ConfiguredMaterial resolve(
            final @Nullable String configuredValue,
            final @NotNull Material defaultMaterial
    ) {
        return resolve(configuredValue, new ItemStack(defaultMaterial), defaultMaterial.name());
    }

    public @NotNull ConfiguredMaterial resolve(
            final @Nullable String configuredValue,
            final @NotNull ItemStack defaultItem,
            final @NotNull String defaultKey
    ) {
        if (configuredValue == null || configuredValue.isBlank()) {
            return fallback(defaultItem, defaultKey);
        }

        final String trimmedValue = configuredValue.trim();
        final Material material = Material.matchMaterial(trimmedValue);
        if (material != null) {
            return new ConfiguredMaterial(material.name(), new ItemStack(material));
        }

        final String providerId = getProviderId(trimmedValue);
        if (providerId == null) {
            plugin.getLogger().warning(() -> "Unknown configured material '%s', falling back to %s".formatted(trimmedValue, defaultKey));
            return fallback(defaultItem, defaultKey);
        }

        final CustomItemProvider provider = providers.get(providerId);
        if (provider == null) {
            plugin.getLogger().warning(() -> "No custom item provider is registered for '%s', falling back to %s".formatted(providerId, defaultKey));
            return fallback(defaultItem, defaultKey);
        }

        if (!provider.isAvailable()) {
            plugin.getLogger().warning(() -> "Custom item provider '%s' is not available, falling back to %s".formatted(providerId, defaultKey));
            return fallback(defaultItem, defaultKey);
        }

        final ConfiguredMaterial resolved = provider.resolve(trimmedValue);
        if (resolved != null) {
            return resolved;
        }

        plugin.getLogger().warning(() -> "Failed to resolve custom item '%s', falling back to %s".formatted(trimmedValue, defaultKey));
        return fallback(defaultItem, defaultKey);
    }

    private void register(
            final @NotNull CustomItemProvider provider,
            final @NotNull CustomItemIntegration integration
    ) {
        final String id = provider.id().toLowerCase(Locale.ROOT);
        providers.put(id, provider);
        integrations.put(id, integration);
    }

    private @Nullable String getProviderId(final @NotNull String configuredValue) {
        final int separatorIndex = configuredValue.indexOf(':');
        if (separatorIndex <= 0) {
            return null;
        }
        return configuredValue.substring(0, separatorIndex).toLowerCase(Locale.ROOT);
    }

    private @NotNull ConfiguredMaterial fallback(final @NotNull ItemStack defaultItem, final @NotNull String defaultKey) {
        return new ConfiguredMaterial(defaultKey, defaultItem.clone());
    }
}
