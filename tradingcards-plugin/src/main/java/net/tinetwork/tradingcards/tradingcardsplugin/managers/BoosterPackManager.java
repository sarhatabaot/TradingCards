package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.TradingCardsConfig;
import net.tinetwork.tradingcards.api.manager.PackManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoosterPackManager extends PackManager {
    private final TradingCards plugin;
    private final Map<String,ItemStack> packs = new HashMap<>();

    public BoosterPackManager(TradingCards plugin) {
        this.plugin = plugin;
        loadPacks();
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
