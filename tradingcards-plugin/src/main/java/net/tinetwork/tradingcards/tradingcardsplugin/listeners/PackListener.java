package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.TradingCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PackListener extends SimpleListener {
    public PackListener(final TradingCards plugin) {
        super(plugin);
    }

    private void removeItemMain(final Player player) {
        if (player.getInventory().getItemInMainHand().getAmount() > 1) {
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            player.getInventory().removeItem(player.getInventory().getItemInMainHand());
        }
    }

    private boolean hasExtra(List<String> lore) {
        return lore.size() > 2;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        EquipmentSlot e = event.getHand();
        if (e == null || !e.equals(EquipmentSlot.HAND)) {
            return;
        }


        Player player = event.getPlayer();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() != Material.valueOf(plugin.getMainConfig().boosterPackMaterial) || !player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.ARROW_INFINITE)) {
            return;
        }

        if (!player.hasPermission("cards.openboosterpack")) {
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage("No permission: cards.openboosterpack"));
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(plugin.cMsg(plugin.getMessagesConfig().prefix + " " + plugin.getMessagesConfig().noCreative));
            return;
        }

        ItemMeta packMeta = itemInMainHand.getItemMeta();
        String packName = packMeta.getDisplayName().split(" ")[1].trim();
        List<String> lore = packMeta.getLore();
        removeItemMain(player);

        boolean hasExtra = hasExtra(lore);

        String[] line1 = (lore.get(0)).split(" ", 2);
        String[] line2 = (lore.get(1)).split(" ", 2);
        String[] line3 = new String[]{""};
        if (hasExtra) {
            line3 = (lore.get(2)).split(" ", 2);
        }

        int normalCardAmount = Integer.parseInt(ChatColor.stripColor(line1[0]));
        String normalCardRarity = WordUtils.capitalizeFully(line1[1]);
        int specialCardAmount = Integer.parseInt(ChatColor.stripColor(line2[0]));
        String specialCardRarity = WordUtils.capitalizeFully(line2[1]);
        int extraCardAmount = 0;
        String extraCardRarity = "";
        if (hasExtra) {
            extraCardAmount = Integer.parseInt(ChatColor.stripColor(line3[0]));
            extraCardRarity = WordUtils.capitalizeFully(line3[1]);
        }

        player.sendMessage(plugin.cMsg(plugin.getMessagesConfig().prefix + " " + plugin.getMessagesConfig().openBoosterPack));
        dropRandomCards(player, normalCardRarity, normalCardAmount, packName);
        dropRandomCards(player, specialCardRarity, specialCardAmount, packName);

        if (hasExtra) {
            dropRandomCards(player, extraCardRarity, extraCardAmount, packName);
        }


    }


    private void dropRandomCards(Player player, final String rarity, int amount, String packName) {
        for (var i = 0; i < amount; i++) {
            if (TradingCardsConfig.getPackSeries(packName).equalsIgnoreCase("active"))
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCard(WordUtils.capitalizeFully(rarity), false).build());
            else
                CardUtil.dropItem(player, plugin.getCardManager().getRandomActiveCard(WordUtils.capitalizeFully(rarity), false).build());
        }
    }


}
