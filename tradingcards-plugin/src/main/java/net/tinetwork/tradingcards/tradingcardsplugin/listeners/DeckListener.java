package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.model.deck.DeckEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.UuidUtil;
import org.apache.commons.lang.StringUtils;
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
import org.jetbrains.annotations.NotNull;

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
			ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().deckCreativeError()));
			return;
		}

		int num = plugin.getDeckManager().getDeckNumber(player.getInventory().getItemInMainHand());
		plugin.getDeckManager().openDeck(player, num);
	}


	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		String viewTitle = e.getView().getTitle();
		if (!viewTitle.contains("s Deck #")) {
			return;
		}



		int deckNum = Integer.parseInt(viewTitle.split("#")[1]);
		String playerName = ChatColor.stripColor(viewTitle.split("'")[0]).trim();
		debug("deck: " + deckNum+",player: " + playerName);

		UUID id = UuidUtil.getPlayerUuid(playerName);
		List<DeckEntry> serializedEntries = new ArrayList<>();
		for (ItemStack it : e.getInventory().getContents()) {
			if (it == null || !it.getItemMeta().hasLore())
				continue; //if item is null for some reason.

			if (!CardUtil.isCard(it) && plugin.getGeneralConfig().dropDeckItems()) {
				Player player = Bukkit.getPlayer(id);
				CardUtil.dropItem(player, it);
				continue;
			}

			DeckEntry entry = formatEntryString(it);
			serializedEntries.add(entry);
			debug("Added " + entry + " to deck file.");
		}
		plugin.getDeckConfig().saveEntries(id,deckNum,serializedEntries);
		debug("Deck closed");
		//plugin.getDeckConfig().getConfig().set("decks.inventories." + playerName + "." + deckNum, serialized);
		//plugin.getDeckConfig().saveConfig();
	}



	private DeckEntry formatEntryString(final ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		final String cardId = nbtItem.getString("name");
		final String rarity = nbtItem.getString("rarity");
		final boolean shiny = nbtItem.getBoolean("isShiny");
		return new DeckEntry(rarity,cardId,itemStack.getAmount(),shiny);
	}

	private String formatSerializedString(ItemStack itemStack) {
		List<String> lore = itemStack.getItemMeta().getLore();
		String rarity = CardUtil.getRarityName(ChatColor.stripColor(lore.get(lore.size() - 1)));
		String cardName = getCardName(rarity, itemStack.getItemMeta().getDisplayName());
		String amount = String.valueOf(itemStack.getAmount());
		String shiny = "no";
		if (itemStack.containsEnchantment(Enchantment.ARROW_INFINITE)) {
			shiny = "yes";
		}

		return rarity + "," + cardName + "," + amount + "," + shiny;
	}

	@NotNull
	private String getCardName(@NotNull final String displayRarity, @NotNull final String displayCard) {
		final String strippedRarity = CardUtil.getRarityName(displayRarity);
		final boolean hasPrefix = plugin.getGeneralConfig().cardPrefix() != null || !plugin.getGeneralConfig().cardPrefix().isEmpty();
		final String strippedPrefix = CardUtil.stripAllColor(plugin.getGeneralConfig().cardPrefix());
		final String strippedShiny = CardUtil.stripAllColor(plugin.getGeneralConfig().shinyName());
		final String strippedDisplay = StringUtils.replaceEach(CardUtil.stripAllColor(displayCard), new String[]{strippedPrefix, strippedShiny}, new String[]{"", ""}).trim();
		plugin.debug("stripped|rarity=" + strippedRarity + "|hasPrefix=" + hasPrefix + "|prefix=" + strippedPrefix + "|shiny=" + strippedShiny + "|display=" + strippedDisplay);

		if (plugin.getCardManager().getCard(strippedDisplay,strippedRarity,false).getCardName().equals("nullCard")) {
			plugin.debug("No such card. card=" + strippedDisplay + "rarity=" + strippedRarity);
			return "None";
		}

		if (plugin.getCardManager().getCards().containsKey(strippedRarity+"."+strippedDisplay.replace(" ","_")))
			return strippedDisplay.replace(" ","_");

		return "None";
	}
}
