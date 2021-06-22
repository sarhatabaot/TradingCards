package media.xen.tradingcards.listeners;

import media.xen.tradingcards.CardUtil;
import media.xen.tradingcards.TradingCards;
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

import static media.xen.tradingcards.TradingCards.sendMessage;

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

	private boolean hasExtra(List<String> lore){
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
			sendMessage(player, plugin.getPrefixedMessage("No permission: cards.openboosterpack"));
			return;
		}
		if (player.getGameMode() == GameMode.CREATIVE) {
			player.sendMessage(plugin.cMsg(plugin.getMessagesConfig().prefix + " " + plugin.getMessagesConfig().noCreative));
			return;
		}

		ItemMeta packMeta = itemInMainHand.getItemMeta();
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
		int specialCardAmount = Integer.parseInt(ChatColor.stripColor(line2[0]));
		int extraCardAmount = 0;
		if (hasExtra) {
			extraCardAmount = Integer.parseInt(ChatColor.stripColor(line3[0]));
		}

		player.sendMessage(plugin.cMsg(plugin.getMessagesConfig().prefix + " " + plugin.getMessagesConfig().openBoosterPack));

		for (int i = 0; i < normalCardAmount; ++i) {
			CardUtil.dropItem(player, CardUtil.getRandomCard(WordUtils.capitalizeFully(line1[1]), false));
		}

		for (int i = 0; i < specialCardAmount; ++i) {
			CardUtil.dropItem(player, CardUtil.getRandomCard(WordUtils.capitalizeFully(line2[1]), false));
		}

		if (hasExtra) {
			for (int i = 0; i < extraCardAmount; ++i) {
				CardUtil.dropItem(player, CardUtil.getRandomCard(WordUtils.capitalizeFully(line3[1]), false));
			}
		}


	}


}
