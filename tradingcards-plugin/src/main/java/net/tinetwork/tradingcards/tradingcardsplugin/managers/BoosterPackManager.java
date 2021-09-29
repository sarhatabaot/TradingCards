package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.PacksConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoosterPackManager implements PackManager {
    private final ItemStack blankPack;
    private final TradingCards plugin;

    private PacksConfig packsConfig;
    private Map<String, ItemStack> packs;

    public BoosterPackManager(TradingCards plugin) {
        this.plugin = plugin;
        this.blankPack = new ItemStack(plugin.getGeneralConfig().packMaterial());
        initValues();
    }

    public void initValues() {
        this.packsConfig = plugin.getPacksConfig();
        this.packs = new HashMap<>();
        loadPacks();
    }

    private void loadPacks() {
        for (String packName : plugin.getPacksConfig().getPacks()) {
            loadPack(packName);
        }
        plugin.getLogger().info("Loaded " + packs.size() + " packs.");
        plugin.debug(BoosterPackManager.class,packs.keySet().toString());
        for(ItemStack itemStack: packs.values()) {
            plugin.debug(BoosterPackManager.class,itemStack.toString());
        }
    }

    @Override
    public Map<String, ItemStack> packs() {
        return packs;
    }

    private void loadPack(final String packName) {
        try {
            packs.put(packName, generatePack(packName));
            plugin.debug(BoosterPackManager.class,"Loaded pack: " + packName);
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public ItemStack generatePack(final String name) throws SerializationException {
        final Pack pack = packsConfig.getPack(name);

        ItemStack itemPack = blankPack.clone();
        ItemMeta itemPackMeta = itemPack.getItemMeta();
        itemPackMeta.setDisplayName(ChatUtil.color(plugin.getGeneralConfig().packPrefix()
                        + plugin.getGeneralConfig().colorPackName())
                        + pack.getDisplayName().replace("_", " "));
        List<String> lore = new ArrayList<>();

        for(Pack.PackEntry entry: pack.getPackEntryList()) {
            final Rarity rarity = plugin.getRaritiesConfig().getRarity(entry.getRarityId());
            lore.add(ChatUtil.color(plugin.getGeneralConfig().colorPackNormal()
                    +entry.getAmount()
                    +" "
                    +plugin.getGeneralConfig().colorPackLore()
                    +rarity.getDisplayName()));
        }
        itemPackMeta.setLore(lore);


        itemPack.setItemMeta(itemPackMeta);
        itemPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        itemPack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        NBTItem nbtItem = new NBTItem(itemPack);
        nbtItem.setString("packId", name);
        nbtItem.setBoolean("pack", true);
        return nbtItem.getItem();
    }

    public boolean isPack(final ItemStack item) {
        return new NBTItem(item).getBoolean("pack");
    }

    @Override
    public ItemStack getPackItem(final String name) {
        return packs.get(name).clone();
    }

    @Override
    public Pack getPack(String id) {
        try {
            return plugin.getPacksConfig().getPack(id);
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
            return null;
        }
    }

}
