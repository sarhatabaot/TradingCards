package media.xen.tradingcards.listeners;


import media.xen.tradingcards.TradingCards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckListener extends SimpleListener{
	public DeckListener(final TradingCards plugin) {
		super(plugin);
	}


	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		String viewTitle = e.getView().getTitle();
		debug("Title: "+viewTitle);

		if (viewTitle.contains("s Deck #")) {
			debug("Deck closed");

			ItemStack[] contents = e.getInventory().getContents();
			String[] title = viewTitle.split("'");
			String[] titleNum = viewTitle.split("#");
			int deckNum = Integer.parseInt(titleNum[1]);
			debug("Deck num: " + deckNum);
			debug("Title: " + title[0]);
			debug("Title: " + title[1]);

			UUID id = Bukkit.getOfflinePlayer(ChatColor.stripColor(title[0])).getUniqueId();
			debug("New ID: " + id.toString());

			List<String> serialized = new ArrayList<>();
			ItemStack[] arrayOfItemStack1 = contents;
			int j = contents.length;

			for (int i = 0; i < j; ++i) {
				ItemStack it = arrayOfItemStack1[i];
				if (it != null && it.getType() == Material.valueOf(plugin.getConfig().getString("General.Card-Material")) && it.getItemMeta().hasDisplayName()) {
					List<String> lore = it.getItemMeta().getLore();
					String shinyPrefix = plugin.getConfig().getString("General.Shiny-Name");
					String rarity = ChatColor.stripColor(lore.get(lore.size() - 1)).replaceAll(shinyPrefix + " ", "");
					String card = plugin.getCardName(rarity, it.getItemMeta().getDisplayName());
					String amount = String.valueOf(it.getAmount());
					String shiny = "no";
					if (it.containsEnchantment(Enchantment.ARROW_INFINITE)) {
						shiny = "yes";
					}

					String serializedString = rarity + "," + card + "," + amount + "," + shiny;
					serialized.add(serializedString);
					debug("Added " + serializedString + " to deck file.");
				} else if (it != null && plugin.getConfig().getBoolean("General.Drop-Deck-Items")) {
					Player p = Bukkit.getPlayer(ChatColor.stripColor(title[0]));
					World w = p.getWorld();
					w.dropItemNaturally(p.getLocation(), it);
				}

				plugin.getDeckConfig().getConfig().set("Decks.Inventories." + id.toString() + "." + deckNum, serialized);
				plugin.getDeckConfig().saveConfig();
			}
		}

	}
}
