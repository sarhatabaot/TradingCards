package media.xen.tradingcards.listeners;

import media.xen.tradingcards.TradingCards;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
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

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		EquipmentSlot e = event.getHand();
		if (e == null || !e.equals(EquipmentSlot.HAND))
			return;


		Player p = event.getPlayer();
		if (p.getInventory().getItemInMainHand().getType() == Material.valueOf(plugin.getConfig().getString("General.BoosterPack-Material")) && event.getPlayer().hasPermission("cards.openboosterpack") && p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.ARROW_INFINITE)) {
			if (p.getGameMode() == GameMode.CREATIVE) {
				event.getPlayer().sendMessage(plugin.cMsg(plugin.getMessagesConfig().getConfig().getString("Messages.Prefix") + " " + plugin.getMessagesConfig().getConfig().getString("Messages.NoCreative")));
			} else {
				ItemStack boosterPack = event.getPlayer().getInventory().getItemInMainHand();
				ItemMeta packMeta = boosterPack.getItemMeta();
				List<String> lore = packMeta.getLore();
				if (p.getInventory().getItemInMainHand().getAmount() > 1) {
					p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
				} else {
					p.getInventory().removeItem(p.getInventory().getItemInMainHand());
				}

				boolean hasExtra = false;
				if (lore.size() > 2) {
					hasExtra = true;
				}

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

				p.sendMessage(plugin.cMsg(plugin.getMessagesConfig().getConfig().getString("Messages.Prefix") + " " + plugin.getMessagesConfig().getConfig().getString("Messages.OpenBoosterPack")));

				int i;
				World curWorld;
				for (i = 0; i < normalCardAmount; ++i) {
					if (p.getInventory().firstEmpty() != -1) {
						p.getInventory().addItem(plugin.generateRandomCard(WordUtils.capitalizeFully(line1[1]), false));
					} else {
						curWorld = p.getWorld();
						if (p.getGameMode() == GameMode.SURVIVAL) {
							curWorld.dropItem(p.getLocation(), plugin.generateRandomCard(WordUtils.capitalizeFully(line1[1]), false));
						}
					}
				}

				for (i = 0; i < specialCardAmount; ++i) {
					if (p.getInventory().firstEmpty() != -1) {
						p.getInventory().addItem(plugin.generateRandomCard(WordUtils.capitalizeFully(line2[1]), false));
					} else {
						curWorld = p.getWorld();
						if (p.getGameMode() == GameMode.SURVIVAL) {
							curWorld.dropItem(p.getLocation(), plugin.generateRandomCard(WordUtils.capitalizeFully(line2[1]), false));
						}
					}
				}

				if (hasExtra) {
					for (i = 0; i < extraCardAmount; ++i) {
						if (p.getInventory().firstEmpty() != -1) {
							p.getInventory().addItem(plugin.generateRandomCard(WordUtils.capitalizeFully(line3[1]), false));
						} else {
							curWorld = p.getWorld();
							if (p.getGameMode() == GameMode.SURVIVAL) {
								curWorld.dropItem(p.getLocation(), plugin.generateRandomCard(WordUtils.capitalizeFully(line3[1]), false));
							}
						}
					}
				}
			}
		}

		if (p.getInventory().getItemInMainHand().getType() == Material.valueOf(plugin.getConfig().getString("General.Deck-Material"))) {
			debug("Deck material...");
			debug("Not creative...");

			if (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.DURABILITY)) {
				debug("Has enchant...");

				if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DURABILITY) == 10) {
					debug("Enchant is level 10...");

					if (p.getGameMode() != GameMode.CREATIVE) {
						String name = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
						String[] nameSplit = name.split("#");
						int num = Integer.parseInt(nameSplit[1]);
						plugin.openDeck(p, num);
					} else {
						sendMessage(event.getPlayer(), plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.DeckCreativeError")));
					}
				}
			}
		}


	}
}
