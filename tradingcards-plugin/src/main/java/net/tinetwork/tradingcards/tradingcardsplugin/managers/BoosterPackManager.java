package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.manager.Cacheable;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BoosterPackManager implements PackManager, Cacheable<String,Pack> {
    private final LoadingCache <String,Pack> packsCache;
    private final ItemStack blankPack;
    private final TradingCards plugin;
    private List<String> packNames;

    private Map<String, ItemStack> packsItemStackCache;


    public BoosterPackManager(@NotNull TradingCards plugin) {
        this.plugin = plugin;
        this.blankPack = new ItemStack(plugin.getGeneralConfig().packMaterial());
        this.packNames = plugin.getStorage().getPacks().stream().map(Pack::id).toList();
        this.packsCache = loadCache();
        loadPacksItemStacks();
        plugin.getLogger().info(() -> InternalLog.Init.LOAD_PACK_MANAGER);
    }

    @Override
    public LoadingCache<String,Pack> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(100)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Pack load(final String key) throws Exception {
                        plugin.debug(TradingCardManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                        return plugin.getStorage().getPack(key);
                    }
                });
    }

    @Override
    public void preLoadCache() {
        try {
            this.packsCache.getAll(packNames);
        } catch (ExecutionException e) {
            //ignored
        }
    }

    private void loadPacksItemStacks() {
        this.packsItemStackCache = new HashMap<>();
        for (Pack pack : plugin.getStorage().getPacks()) {
            loadPack(pack.id());
        }
        plugin.getLogger().info(() -> InternalLog.Init.LOAD_PACKS_AMOUNT.formatted(packsItemStackCache.size()));
        plugin.debug(BoosterPackManager.class, packsItemStackCache.keySet().toString());
        for(ItemStack itemStack: packsItemStackCache.values()) {
            plugin.debug(BoosterPackManager.class,itemStack.toString());
        }
        this.packNames = plugin.getStorage().getPacks().stream().map(Pack::id).toList();
    }

    @Override
    public Map<String, ItemStack> getCachedPacksItemStacks() {
        return packsItemStackCache;
    }

    private void loadPack(final String packName) {
        try {
            packsItemStackCache.put(packName, generatePack(packName));
            plugin.debug(BoosterPackManager.class, InternalDebug.LOAD_PACK.formatted(packName));
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public ItemStack generatePack(final String name) throws SerializationException {
        final Pack pack = plugin.getStorage().getPack(name);

        ItemStack itemPack = blankPack.clone();
        ItemMeta itemPackMeta = itemPack.getItemMeta();
        itemPackMeta.setDisplayName(ChatUtil.color(plugin.getGeneralConfig().packPrefix()
                        + plugin.getGeneralConfig().colorPackName())
                        + pack.getDisplayName().replace("_", " "));
        List<String> lore = new ArrayList<>();

        for(Pack.PackEntry entry: pack.getPackEntryList()) {
            final Rarity rarity = plugin.getRarityManager().getRarity(entry.getRarityId());
            lore.add(ChatUtil.color(plugin.getGeneralConfig().colorPackNormal()
                    +entry.getAmount()
                    +" "
                    +plugin.getGeneralConfig().colorPackLore()
                    +rarity.getDisplayName()));
        }
        itemPackMeta.setLore(lore);



        itemPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        itemPackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemPack.setItemMeta(itemPackMeta);
        NBTItem nbtItem = new NBTItem(itemPack);
        nbtItem.setString(NbtUtils.NBT_PACK_ID, name);
        nbtItem.setBoolean(NbtUtils.NBT_PACK, true);
        return nbtItem.getItem();
    }

    public boolean isPack(final ItemStack item) {
        return new NBTItem(item).getBoolean(NbtUtils.NBT_PACK);
    }

    @Override
    public ItemStack getPackItem(final String packId) {
        return packsItemStackCache.get(packId).clone();
    }

    @Override
    public Pack getPack(final String packId) {
        return packsCache.getUnchecked(packId);
    }

    @Override
    public List<Pack> getPacks() {
        return packsCache.asMap().values().stream().toList();
    }

    @Override
    public boolean containsPack(final String packId) {
        for(Pack pack: getPacks()) {
            if(pack.id().equals(packId))
                return true;
        }
        return false;
    }

    @Override
    public List<String> getPackIds() {
        return packNames;
    }
}
