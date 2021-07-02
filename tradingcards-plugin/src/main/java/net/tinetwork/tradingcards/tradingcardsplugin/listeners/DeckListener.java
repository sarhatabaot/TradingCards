package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.UuidUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
		if (!plugin.getDeckManager().isDeck(itemInMainHand))
			return;


		if (player.getGameMode() == GameMode.CREATIVE) {
			TradingCards.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().deckCreativeError));
			return;
		}


		String name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
		String[] nameSplit = name.split("#");
		int num = Integer.parseInt(nameSplit[1]);
		plugin.getDeckManager().openDeck(player, num);
	}


	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		String viewTitle = e.getView().getTitle();
		if (!viewTitle.contains("s Deck #")) {
			return;
		}

		debug("Deck closed");

		int deckNum = Integer.parseInt(viewTitle.split("#")[1]);
		String playerName = ChatColor.stripColor(viewTitle.split("'")[0]);
		debug("Deck num: " + deckNum+",PlayerName: " + playerName+",DeckNumber:  "+ deckNum);


		UUID id = UuidUtil.getPlayerUuid(playerName);
		List<String> serialized = new ArrayList<>();

		for (ItemStack it : e.getInventory().getContents()) {
			if (it == null || !it.getItemMeta().hasLore())
				continue;

			if (!CardUtil.isCard(it) && plugin.getMainConfig().dropDeckItems) {
				Player player = Bukkit.getPlayer(id);
				CardUtil.dropItem(player, it);
				continue;
			}

			String serializedString = formatSerializedString(it);
			serialized.add(serializedString);
			debug("Added " + serializedString + " to deck file.");
		}

		plugin.getDeckConfig().getConfig().set("Decks.Inventories." + id.toString() + "." + deckNum, serialized);
		plugin.getDeckConfig().saveConfig();
	}




	private String formatSerializedString(ItemStack itemStack) {
		List<String> lore = itemStack.getItemMeta().getLore();
		String rarity = CardUtil.getRarityName(ChatColor.stripColor(lore.get(lore.size() - 1)));
		String cardName = CardUtil.getCardName(rarity, itemStack.getItemMeta().getDisplayName());
		String amount = String.valueOf(itemStack.getAmount());
		String shiny = "no";
		if (itemStack.containsEnchantment(Enchantment.ARROW_INFINITE)) {
			shiny = "yes";
		}

		return rarity + "," + cardName + "," + amount + "," + shiny;
	}
}
