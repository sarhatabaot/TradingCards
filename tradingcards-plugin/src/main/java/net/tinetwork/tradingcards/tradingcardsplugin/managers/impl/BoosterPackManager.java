package net.tinetwork.tradingcards.tradingcardsplugin.managers.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.tr7zw.changeme.nbtapi.NBT;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BoosterPackManager extends Manager<String, Pack> implements PackManager {
    private final ItemStack blankPack;

    private final LoadingCache<String, ItemStack> packsItemStackCache;

    public BoosterPackManager(@NotNull TradingCards plugin) {
        super(plugin);
        this.blankPack = new ItemStack(plugin.getGeneralConfig().packMaterial());
        this.packsItemStackCache = loadItemStackCache();
        preLoadItemStackCache();
        plugin.getLogger().info(() -> InternalLog.Init.LOAD_PACK_MANAGER);
    }

    @Override
    public List<String> getKeys() {
        return plugin.getStorage().getPacks().stream().map(Pack::getId).toList();
    }

    @Override
    public LoadingCache<String, Pack> loadCache() {
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getPacks().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getPacks().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> {
                        plugin.debug(BoosterPackManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                        return plugin.getStorage().getPack(key);
                });
    }

    @Contract(" -> new")
    private @NotNull LoadingCache<String,ItemStack> loadItemStackCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(this::generatePack);
    }

    private void preLoadItemStackCache() {
        this.packsItemStackCache.getAll(getKeys());
    }


    @Override
    public Map<String, ItemStack> getCachedPacksItemStacks() {
        return packsItemStackCache.asMap();
    }


    @Override
    public ItemStack generatePack(final String name) {
        final Pack pack = plugin.getStorage().getPack(name);
        if (pack == null) {
            plugin.getLogger().warning(() -> "Could not get pack %s".formatted(name));
            return new ItemStack(Material.AIR);
        }

        return generatePack(pack);
    }

    @Override
    public ItemStack generatePack(final @NotNull Pack pack) {
        ItemStack itemPack = blankPack.clone();
        ItemMeta itemPackMeta = itemPack.getItemMeta();
        Objects.requireNonNull(itemPackMeta).setDisplayName(ChatUtil.color(plugin.getGeneralConfig().packPrefix()
                + plugin.getGeneralConfig().colorPackName())
                + pack.getDisplayName().replace("_", " "));
        List<String> lore = new ArrayList<>();

        for (PackEntry entry : pack.getPackEntryList()) {
            final Rarity rarity = plugin.getRarityManager().getRarity(entry.getRarityId());
            lore.add(ChatUtil.color(plugin.getGeneralConfig().colorPackNormal()
                    + entry.getAmount()
                    + " "
                    + plugin.getGeneralConfig().colorPackLore()
                    + rarity.getDisplayName()));
        }
        itemPackMeta.setLore(lore);


        itemPack.addUnsafeEnchantment(Enchantment.INFINITY, 10);
        itemPackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemPack.setItemMeta(itemPackMeta);
        NBT.modify(itemPack, nbt -> {
            nbt.getOrCreateCompound(NbtUtils.TC_COMPOUND).setString(NbtUtils.TC_PACK_ID, pack.getId());
        });
        return itemPack;
    }

    public boolean isPack(final ItemStack item) {
        return NbtUtils.Pack.isPack(item);
    }

    @Override
    public ItemStack getPackItem(final String packId) {
        return packsItemStackCache.get(packId).clone();
    }

    @Override
    public Pack getPack(final String packId) {
        return cache.get(packId);
    }

    @Override
    public List<Pack> getPacks() {
        return cache.asMap().values().stream().toList();
    }

    @Override
    public boolean containsPack(final String packId) {
        for (Pack pack : getPacks()) {
            if (pack.getId().equals(packId))
                return true;
        }
        return false;
    }

    @Override
    public List<String> getPackIds() {
        return getKeys();
    }

    @Override
    public void forceCacheRefresh() {
        super.forceCacheRefresh();
        this.packsItemStackCache.invalidateAll();
        preLoadItemStackCache();
    }
}
