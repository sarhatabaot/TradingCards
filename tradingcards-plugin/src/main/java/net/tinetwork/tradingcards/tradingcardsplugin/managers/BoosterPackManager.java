package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager;
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
import java.util.concurrent.ExecutionException;
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
        return plugin.getStorage().getPacks().stream().map(Pack::id).toList();
    }

    @Override
    public LoadingCache<String, Pack> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getPacks().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getPacks().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Pack load(final String key) throws Exception {
                        plugin.debug(BoosterPackManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                        return plugin.getStorage().getPack(key);
                    }
                });
    }

    @Contract(" -> new")
    private @NotNull LoadingCache<String,ItemStack> loadItemStackCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(100)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public ItemStack load(final String key) throws Exception {
                        return generatePack(key);
                    }
                });
    }

    private void preLoadItemStackCache() {
        try {
            this.packsItemStackCache.getAll(getKeys());
        } catch (ExecutionException e) {
            plugin.debug(getClass(),e.getMessage());
        }
    }


    @Override
    public Map<String, ItemStack> getCachedPacksItemStacks() {
        return packsItemStackCache.asMap();
    }


    @Override
    public ItemStack generatePack(final String name) {
        final Pack pack = plugin.getStorage().getPack(name);
        if (pack == null) {
            plugin.getLogger().warning("Could not get pack %s".formatted(name));
            return new ItemStack(Material.AIR);
        }

        return generatePack(pack);
    }

    @Override
    public ItemStack generatePack(final @NotNull Pack pack) {
        ItemStack itemPack = blankPack.clone();
        ItemMeta itemPackMeta = itemPack.getItemMeta();
        itemPackMeta.setDisplayName(ChatUtil.color(plugin.getGeneralConfig().packPrefix()
                + plugin.getGeneralConfig().colorPackName())
                + pack.getDisplayName().replace("_", " "));
        List<String> lore = new ArrayList<>();

        for (Pack.PackEntry entry : pack.getPackEntryList()) {
            final Rarity rarity = plugin.getRarityManager().getRarity(entry.getRarityId());
            lore.add(ChatUtil.color(plugin.getGeneralConfig().colorPackNormal()
                    + entry.getAmount()
                    + " "
                    + plugin.getGeneralConfig().colorPackLore()
                    + rarity.getDisplayName()));
        }
        itemPackMeta.setLore(lore);


        itemPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        itemPackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemPack.setItemMeta(itemPackMeta);
        NBTItem nbtItem = new NBTItem(itemPack);
        nbtItem.setString(NbtUtils.NBT_PACK_ID, pack.id());
        nbtItem.setBoolean(NbtUtils.NBT_PACK, true);
        return nbtItem.getItem();
    }

    public boolean isPack(final ItemStack item) {
        return new NBTItem(item).getBoolean(NbtUtils.NBT_PACK);
    }

    @Override
    public ItemStack getPackItem(final String packId) {
        return packsItemStackCache.getUnchecked(packId).clone();
    }

    @Override
    public Pack getPack(final String packId) {
        return cache.getUnchecked(packId);
    }

    @Override
    public List<Pack> getPacks() {
        return cache.asMap().values().stream().toList();
    }

    @Override
    public boolean containsPack(final String packId) {
        for (Pack pack : getPacks()) {
            if (pack.id().equals(packId))
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
