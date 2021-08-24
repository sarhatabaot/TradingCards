package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
        if (!plugin.getPackManager().isPack(itemInMainHand)) {
            return;
        }


        if (!player.hasPermission("cards.openboosterpack")) {
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage("No permission: cards.openboosterpack"));
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(plugin.cMsg(plugin.getMessagesConfig().prefix() + " " + plugin.getMessagesConfig().noCreative()));
            return;
        }
        NBTItem nbtPackItem = new NBTItem(itemInMainHand);
        final String packId = nbtPackItem.getString("packId");
        if(packId == null) {
            return;
        }

        Pack pack = plugin.getPackManager().getPack(packId);
        dropRandomCards(player, pack.getNormalCardRarity(),pack.getNumNormalCards(),pack.getSeries());
        dropRandomCards(player, pack.getSpecialCardsRarity(),pack.getNumSpecialCards(),pack.getSeries());
        dropRandomCards(player, pack.getExtraCardsRarity(),pack.getNumExtraCards(),pack.getSeries());
        removeItemMain(player);

    }


    private void dropRandomCards(Player player, final String rarity, int amount, final String series) {
        if (amount <= 0)
            return;
        for (var i = 0; i < amount; i++) {
            if (series.equalsIgnoreCase("active"))
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCard(WordUtils.capitalizeFully(rarity), false).build());
            else
                CardUtil.dropItem(player, plugin.getCardManager().getRandomActiveCard(WordUtils.capitalizeFully(rarity), false).build());
        }
    }


}
