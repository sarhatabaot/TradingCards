package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.PacksConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoosterPackManager extends PackManager {
    private ItemStack blankPack;
    private PacksConfig packsConfig;
    private final TradingCards plugin;
    private final Map<String, ItemStack> packs = new HashMap<>();

    public BoosterPackManager(TradingCards plugin) {
        this.plugin = plugin;
        this.packsConfig = plugin.getPacksConfig();
        loadPacks();
        this.blankPack = new ItemStack(plugin.getGeneralConfig().packMaterial());
    }

    private void loadPacks() {

        for (String packName : plugin.getPacksConfig().getPacks()) {
            loadPack(packName);
        }
        plugin.getLogger().info("Loaded " + packs.size() + " packs.");


    }

    @Override
    public Map<String, ItemStack> packs() {
        return packs;
    }

    private void loadPack(final String packName) {
        try {
            packs.put(packName, generatePack(packName));
            plugin.debug("Loaded pack: " + packName);
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public ItemStack generatePack(final String name) throws SerializationException {
        final Pack pack = packsConfig.getPack(name);

        ItemStack itemPack = blankPack.clone();
        ItemMeta itemPackMeta = itemPack.getItemMeta();

        itemPackMeta.setDisplayName(ChatUtil.color(Component.text(plugin.getGeneralConfig().packPrefix())
                .append(Component.text(plugin.getGeneralConfig().colorPackName()))
                .append(Component.text(name.replace("_", " ")))));
        List<String> lore = new ArrayList<>();

        if (pack.getNumNormalCards() > 0) {
            lore.add(ChatUtil.color(Component.text(plugin.getGeneralConfig().colorPackNormal())
                    .append(Component.text(pack.getNumNormalCards()))
                    .append(Component.text(" "))
                    .append(Component.text(plugin.getGeneralConfig().colorPackLore()))
                    .append(Component.text(pack.getNormalCardRarity()))));
        }
        if (pack.getNumSpecialCards() > 0) {
            lore.add(ChatUtil.color(Component.text(plugin.getGeneralConfig().colorPackSpecial())
                    .append(Component.text(pack.getNumSpecialCards()))
                    .append(Component.text(" "))
                    .append(Component.text(plugin.getGeneralConfig().colorPackLore()))
                    .append(Component.text(pack.getSpecialCardsRarity()))));
        }
        if (pack.getNumExtraCards() > 0) {
            lore.add(ChatUtil.color(Component.text(plugin.getGeneralConfig().colorPackExtra())
                    .append(Component.text(pack.getNumExtraCards()))
                    .append(Component.text(" "))
                    .append(Component.text(plugin.getGeneralConfig().colorPackLore()))
                    .append(Component.text(pack.getExtraCardsRarity()))));
        }

        itemPackMeta.setLore(lore);

        itemPack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemPack.setItemMeta(itemPackMeta);
        itemPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);

        NBTItem nbtItem = new NBTItem(itemPack);
        nbtItem.setString("packId", name);
        nbtItem.setBoolean("pack", true);
        return nbtItem.getItem();
    }

    public boolean isPack(final ItemStack item) {
        return new NBTItem(item).getBoolean("pack");
    }

    @Override
    @Nullable
    public Pack getPack(String id) {
        try {
            return plugin.getPacksConfig().getPack(id);
        } catch (SerializationException e) {
            plugin.getLogger().severe(e.getMessage());
            return null;
        }
    }

}
