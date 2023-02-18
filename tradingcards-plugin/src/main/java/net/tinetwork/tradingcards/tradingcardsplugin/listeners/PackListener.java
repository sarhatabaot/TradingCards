package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PackListener extends SimpleListener {
    public PackListener(final TradingCards plugin) {
        super(plugin);
    }

    private void removeItemMain(final @NotNull Player player) {
        if (player.getInventory().getItemInMainHand().getAmount() > 1) {
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        } else {
            player.getInventory().removeItem(player.getInventory().getItemInMainHand());
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        EquipmentSlot e = event.getHand();
        if (e == null || !e.equals(EquipmentSlot.HAND)) {
            return;
        }


        Player player = event.getPlayer();
        final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if(itemInMainHand.getType() == Material.AIR)
            return;
        if (!plugin.getPackManager().isPack(itemInMainHand)) {
            return;
        }


        if (!player.hasPermission(Permissions.User.Use.PACK)) {
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage("No permission: "+Permissions.User.Use.PACK));
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            ChatUtil.sendMessage(player, plugin.getMessagesConfig().noCreative());
            return;
        }
        NBTItem nbtPackItem = new NBTItem(itemInMainHand);
        final String packId = NbtUtils.Pack.getPackId(nbtPackItem);
        if(packId == null) {
            return;
        }

        Pack pack = plugin.getPackManager().getPack(packId);
        for(PackEntry entry: pack.getPackEntryList()) {
            dropRandomCards(player, entry.getRarityId(), entry.getAmount(),entry.getSeries());
        }

        removeItemMain(player);

    }


    private void dropRandomCards(Player player, final String rarity, int amount, final String series) {
        if (amount <= 0)
            return;
        for (var i = 0; i < amount; i++) {
            TradingCard randomCard = plugin.getCardManager().getRandomCardByRarityAndSeries(rarity,series);
            boolean isShiny = randomCard.hasShiny() && CardUtil.calculateIfShiny(false);
            CardUtil.dropItem(player, randomCard.build(isShiny));
        }
    }


}
