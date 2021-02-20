package media.xen.tradingcards.listeners;

import media.xen.tradingcards.CardUtil;
import media.xen.tradingcards.DeckManager;
import media.xen.tradingcards.TradingCards;
import media.xen.tradingcards.uuid.UuidUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static media.xen.tradingcards.TradingCards.sendMessage;

public class DeckListener extends SimpleListener {
	public DeckListener(final TradingCards plugin) {
		super(plugin);
	}

	@EventHandler
	public void onDeckOpen(final PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		EquipmentSlot e = event.getHand();
		if (e == null || !e.equals(EquipmentSlot.HAND)) {
			return;
		}


		Player player = event.getPlayer();
		final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
		if (!DeckManager.isDeck(itemInMainHand))
			return;


		if (player.getGameMode() == GameMode.CREATIVE) {
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().deckCreativeError));
			return;
		}


		String name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
		String[] nameSplit = name.split("#");
		int num = Integer.parseInt(nameSplit[1]);
		DeckManager.openDeck(player, num);


	}


	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		String viewTitle = e.getView().getTitle();
		if (!viewTitle.contains("s Deck #")) {
			return;
		}

		debug("Deck closed");

		String[] title = viewTitle.split("'");
		String[] titleNum = viewTitle.split("#");
		int deckNum = Integer.parseInt(titleNum[1]);
		String playerName = ChatColor.stripColor(title[0]);
		debug("Deck num: " + deckNum);
		debug("Title: " + title[0]);
		debug("Title: " + title[1]);

		UUID id = UuidUtil.getPlayerUuid(playerName);
		List<String> serialized = new ArrayList<>();
		int j = e.getInventory().getContents().length;

		for (int i = 0; i < j; ++i) {
			ItemStack it = e.getInventory().getContents()[i];
			if (it == null || it.getItemMeta().getLore() == null)
				continue;

			if (it.getType() == Material.valueOf(plugin.getMainConfig().cardMaterial) && it.getItemMeta().hasDisplayName()) {
				List<String> lore = it.getItemMeta().getLore();
				String rarity = CardUtil.getRarityName(ChatColor.stripColor(lore.get(lore.size() - 1)));
				String card = CardUtil.getCardName(rarity, it.getItemMeta().getDisplayName());
				String amount = String.valueOf(it.getAmount());
				String shiny = "no";
				if (it.containsEnchantment(Enchantment.ARROW_INFINITE)) {
					shiny = "yes";
				}

				String serializedString = rarity + "," + card + "," + amount + "," + shiny;
				serialized.add(serializedString);
				debug("Added " + serializedString + " to deck file.");
			} else if (plugin.getMainConfig().dropDeckItems) {
				Player p = Bukkit.getPlayer(ChatColor.stripColor(title[0]));
				World w = p.getWorld();
				w.dropItemNaturally(p.getLocation(), it);
			}

			plugin.getDeckConfig().getConfig().set("Decks.Inventories." + id.toString() + "." + deckNum, serialized);
			plugin.getDeckConfig().saveConfig();
		}


	}
}
