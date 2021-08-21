package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.TradingCardsConfig;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.PacksConfig;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoosterPackManager extends PackManager {
    private ItemStack blankPack;
    private PacksConfig packsConfig;
    private final TradingCards plugin;
    private final Map<String,ItemStack> packs = new HashMap<>();

    public BoosterPackManager(TradingCards plugin) {
        this.plugin = plugin;
        this.packsConfig = plugin.getPacksConfig();
        loadPacks();
        this.blankPack = new ItemStack(plugin.getGeneralConfig().packMaterial());
    }

    private void loadPacks() {
        for(String packName: TradingCardsConfig.getPacks()){
            packs.put(packName,generatePack(packName));
        }
    }

    @Override
    public Map<String, ItemStack> packs() {
        return packs;
    }

    public ItemStack generateNewPack(final String name) throws SerializationException {
        final PacksConfig.Pack pack = packsConfig.getPack(name);

        ItemStack itemPack = blankPack.clone();
        ItemMeta itemPackMeta = itemPack.getItemMeta();

        itemPackMeta.displayName(Component.text(plugin.getGeneralConfig().packPrefix())
                .append(Component.text(plugin.getGeneralConfig().getColorPackName()))
                .append(Component.text(name.replace("_"," "))));
        List<Component> lore = new ArrayList<>();

        if(pack.getNumNormalCards() > 0) {
            lore.add(Component.text(plugin.getGeneralConfig().getColorPackNormal())
                    .append(Component.text(pack.getNumNormalCards()))
                    .append(Component.text(" "))
                    .append(Component.text(plugin.getGeneralConfig().getColorPackLore()))
                    .append(Component.text(pack.getNormalCardRarity())));
        }
        if(pack.getNumSpecialCards() > 0) {
            lore.add(Component.text(plugin.getGeneralConfig().getColorPackSpecial())
                    .append(Component.text(pack.getNumSpecialCards()))
                    .append(Component.text(" "))
                    .append(Component.text(plugin.getGeneralConfig().getColorPackLore()))
                    .append(Component.text(pack.getSpecialCardsRarity())));
        }
        if(pack.getNumExtraCards() > 0) {
            lore.add(Component.text(plugin.getGeneralConfig().getColorPackExtra())
                    .append(Component.text(pack.getNumExtraCards()))
                    .append(Component.text(" "))
                    .append(Component.text(plugin.getGeneralConfig().getColorPackLore()))
                    .append(Component.text(pack.getExtraCardsRarity())));
        }

        itemPackMeta.lore(lore);

        itemPack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemPack.setItemMeta(itemPackMeta);
        itemPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);

        NBTItem nbtItem = new NBTItem(itemPack);
        nbtItem.setString("packId", name);
        nbtItem.setBoolean("pack",true);
        return nbtItem.getItem();
    }

    public boolean isPack(final ItemStack item) {
        return new NBTItem(item).getBoolean("pack");
    }

    @Override
    public ItemStack generatePack(final String name) {
        ItemStack boosterPack = TradingCardsConfig.getBlankBoosterPack();
        int numNormalCards = plugin.getConfig().getInt("BoosterPacks." + name + ".NumNormalCards");
        int numSpecialCards = plugin.getConfig().getInt("BoosterPacks." + name + ".NumSpecialCards");
        String prefix = plugin.getMainConfig().boosterPackPrefix;
        String normalCardColour = plugin.getConfig().getString("Colours.BoosterPackNormalCards");
        String extraCardColour = plugin.getConfig().getString("Colours.BoosterPackExtraCards");
        String loreColour = plugin.getMainConfig().boosterPackLoreColour;
        String nameColour = plugin.getMainConfig().boosterPackNameColour;
        String normalRarity = plugin.getConfig().getString("BoosterPacks." + name + ".NormalCardRarity");
        String specialRarity = plugin.getConfig().getString("BoosterPacks." + name + ".SpecialCardRarity");
        String extraRarity = "";
        int numExtraCards = 0;
        boolean hasExtraRarity = false;
        if (plugin.getConfig().contains("BoosterPacks." + name + ".ExtraCardRarity") && plugin.getConfig().contains("BoosterPacks." + name + ".NumExtraCards")) {
            hasExtraRarity = true;
            extraRarity = plugin.getConfig().getString("BoosterPacks." + name + ".ExtraCardRarity");
            numExtraCards = plugin.getConfig().getInt("BoosterPacks." + name + ".NumExtraCards");
        }

        String specialCardColour = plugin.getConfig().getString("Colours.BoosterPackSpecialCards");
        ItemMeta pMeta = boosterPack.getItemMeta();
        pMeta.setDisplayName(plugin.cMsg(prefix + nameColour + name.replace("_", " ")));
        List<String> lore = new ArrayList<>();
        lore.add(plugin.cMsg(normalCardColour + numNormalCards + loreColour + " " + normalRarity.toUpperCase()));
        if (hasExtraRarity) {
            lore.add(plugin.cMsg(extraCardColour + numExtraCards + loreColour + " " + extraRarity.toUpperCase()));
        }

        lore.add(plugin.cMsg(specialCardColour + numSpecialCards + loreColour + " " + specialRarity.toUpperCase()));
        pMeta.setLore(lore);
        if (plugin.getMainConfig().hideEnchants) {
            pMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        boosterPack.setItemMeta(pMeta);
        boosterPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        return boosterPack;
    }
}
