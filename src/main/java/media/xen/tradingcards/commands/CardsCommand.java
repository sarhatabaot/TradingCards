package media.xen.tradingcards.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import media.xen.tradingcards.CardManager;
import media.xen.tradingcards.CardUtil;
import media.xen.tradingcards.DeckManager;
import media.xen.tradingcards.TradingCards;
import media.xen.tradingcards.api.addons.TradingCardsAddon;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static media.xen.tradingcards.ChatUtil.sendPrefixedMessage;
import static media.xen.tradingcards.TradingCards.econ;
import static media.xen.tradingcards.TradingCards.sendMessage;

@CommandAlias("cards")
public class CardsCommand extends BaseCommand {
	private final TradingCards plugin;

	public CardsCommand(final TradingCards plugin) {
		this.plugin = plugin;
	}

	@CatchUnknown
	@HelpCommand
	public void onHelp(final CommandSender sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("version|ver")
	@CommandPermission("cards.version")
	@Description("Show the plugin version.")
	public void onVersion(final CommandSender sender){
		final String format = "%s %s API-%s";
		sendMessage(sender,String.format(format,plugin.getName(), plugin.getDescription().getVersion(),plugin.getDescription().getAPIVersion()));
	}

	@Subcommand("reload")
	@CommandPermission("cards.reload")
	@Description("Reloads all the configs & restart the timer.")
	public void onReload(final CommandSender sender) {
		final String format = "%s %s";
		sendMessage(sender, String.format(format, plugin.getMessagesConfig().prefix, plugin.getMessagesConfig().reload));
		plugin.reloadAllConfig();
		if (plugin.getMainConfig().scheduleCards) {
			plugin.startTimer();
		}
	}


	@Subcommand("resolve")
	@CommandPermission("cards.resolve")
	@Description("Shows a player's uuid")
	public void onResolve(final CommandSender sender, final Player player) {
		sendMessage(sender, plugin.getMessagesConfig().resolveMsg.replaceAll("%name%", player.getName()).replaceAll("%uuid%", player.getUniqueId().toString()));
	}

	@Subcommand("toggle")
	@CommandPermission("cards.toggle")
	@Description("Toggles card drops from mobs.")
	public void onToggle(final Player player) {
		if (plugin.isOnList(player) && plugin.blacklistMode() == 'b') {
			plugin.removeFromList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleEnabled));
		} else if (plugin.isOnList(player) && plugin.blacklistMode() == 'w') {
			plugin.removeFromList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleDisabled));
		} else if (!plugin.isOnList(player) && plugin.blacklistMode() == 'b') {
			plugin.addToList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleDisabled));
		} else if (!plugin.isOnList(player) && plugin.blacklistMode() == 'w') {
			plugin.addToList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleEnabled));
		}
	}

	@Subcommand("create")
	@CommandPermission("cards.create")
	public class CreateCommands extends BaseCommand {
		@Subcommand("card")
		@CommandPermission("cards.create.card")
		@Description("Creates a card.")
		public void onCreateCard(final Player player, final String rarity, final String name, final String series, final String type, final boolean shiny, final String info, final String about){
			CardUtil.createCard(player, rarity.replaceAll("_", " "), name, series.replaceAll("_", " "), type.replaceAll("_", " "), shiny, info.replaceAll("_", " "), about.replaceAll("_", " "));
		}

		//more commands here, pack, rarity, series etc.
	}

	@Subcommand("give")
	@CommandPermission("cards.give")
	public class GiveCommands extends BaseCommand {
		@Subcommand("card")
		@CommandPermission("cards.give.card")
		@Description("Gives a card.")
		public void onGiveCard(final Player player, final String name, final String rarity){
			if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + name)) {
				player.getInventory().addItem(CardManager.getCard(name, rarity,false));
				return;
			}
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().noCard));
		}

		@Subcommand("card shiny")
		@CommandPermission("cards.give.card.shiny")
		@Description("Gives a shiny card.")
		public void onGiveShinyCard(final Player player, final String name, final String rarity){
			if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + name)) {
				player.getInventory().addItem(CardManager.getCard(name, rarity, true));
				return;
			}
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().noCard));
		}


		@Subcommand("boosterpack|pack")
		@Description("Gives a pack to a player.")
		@CommandPermission("cards.give.pack")
		public void onGiveBoosterPack(final CommandSender sender, final Player player, final String boosterpack){
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().boosterPackMsg));
			CardUtil.dropItem(player,CardManager.generatePack(boosterpack));
		}

		@Subcommand("random")
		@Description("Gives a random card to a player.")
		@CommandPermission("cards.give.random")
		public void onGiveRandomCard(final CommandSender sender, final Player player, final String entityType) {
			try {
				EntityType.valueOf(entityType.toUpperCase());
				String rare = CardUtil.calculateRarity(EntityType.valueOf(entityType.toUpperCase()), true);
				plugin.debug("onCommand.rare: " + rare);
				sendPrefixedMessage(sender, plugin.getMessagesConfig().giveRandomCardMsg.replaceAll("%player%", player.getName()));
				CardUtil.dropItem(player,CardUtil.getRandomCard(rare, false));
			} catch (IllegalArgumentException exception) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().noEntity);
			}
		}
	}


	@Subcommand("debug")
	@CommandPermission("cards.admin.debug")
	public class DebugCommands extends BaseCommand{
		@Subcommand("showcache")
		@CommandPermission("cards.admin.debug.showcache")
		@Description("Shows the card cache")
		public void showCache(final CommandSender sender){
			sender.sendMessage(StringUtils.join(CardManager.getCards().keySet(), ","));
		}

		@Subcommand("modules")
		@CommandPermission("cards.admin.debug.modules")
		@Description("Shows all enabled hooks and addons.")
		public void onModules(final CommandSender sender){
			final StringBuilder builder = new StringBuilder("Enabled Modules:");
			builder.append("\n");
			for (Plugin bukkitPlugin: Bukkit.getPluginManager().getPlugins()) {
				if(plugin instanceof TradingCardsAddon)
					builder.append(ChatColor.GREEN).append(bukkitPlugin.getName()).append(" ");
			}
			for(String depend: plugin.getDescription().getSoftDepend()){
				if(Bukkit.getPluginManager().getPlugin(depend) == null)
					builder.append(ChatColor.GRAY);
				else {
					builder.append(ChatColor.GREEN);
				}
				builder.append(depend).append(" ");
			}
			sender.sendMessage(builder.toString());
		}


		@Subcommand("rarities")
		@CommandPermission("cards.admin.debug.rarities")
		@Description("Shows available rarities.")
		public void onRarities(final CommandSender sender) {
			final List<String> rarities = new ArrayList<>(plugin.getCardsConfig().getConfig().getConfigurationSection("Cards").getKeys(false));
			sender.sendMessage(StringUtils.join(rarities, ", "));
		}

		@Subcommand("exists")
		@CommandPermission("cards.admin.debug.exists")
		@Description("Shows if a card exists or not.")
		public void onExists(final CommandSender sender, final String card, final String rarity){
			if(plugin.getCardsConfig().getConfig().contains("Cards."+rarity+"."+card)) {
				sender.sendMessage(String.format("Card %s.%s exists",rarity,card));
				return;
			}
			sender.sendMessage(String.format("Card %s.%s does not exist",rarity,card));
		}
	}




	@Subcommand("list")
	@CommandPermission("cards.list")
	@Description("Lists all cards by rarities")
	public class ListSubCommand extends BaseCommand{
		@Default
		public void onList(final CommandSender sender,@Optional final String rarity){
			onListPlayer(sender, (Player) sender,rarity);
		}

		@Subcommand("player")
		@CommandPermission("cards.list.player")
		@Description("Lists all cards by a player.")
		public void onListPlayer(final CommandSender sender, final Player target, @Optional final String rarity) {
			if(rarity == null || plugin.isRarity(rarity).equals("None")){
				final String sectionFormat = String.format("&e&l------- &7(&6&l%s's Collection&7)&e&l -------", target.getName());
				sendMessage(sender, String.format(sectionFormat,target.getName()));
				for(String raritySection: plugin.getCardsConfig().getConfig().getConfigurationSection("Cards").getKeys(false)){
					listRarity(sender,target,raritySection);
				}
				return;
			}
			listRarity(sender, target, rarity);
		}

		@Subcommand("pack")
		@CommandPermission("cards.list.pack")
		@Description("Lists all packs.")
		public void onListPack(final CommandSender sender){
			ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("BoosterPacks");
			Set<String> rarityKeys = rarities.getKeys(false);
			int k = 0;
			sendMessage(sender, "&6--- Booster Packs --- ");
			boolean canBuy2 = false;
			boolean hasExtra = false;

			for (Iterator<String> iterator = rarityKeys.iterator(); iterator.hasNext(); hasExtra = false) {
				String rarity = iterator.next();
				if (plugin.getConfig().getBoolean("PluginSupport.Vault.Vault-Enabled") && plugin.getConfig().contains("BoosterPacks." + rarity + ".Price") && plugin.getConfig().getDouble("BoosterPacks." + rarity + ".Price") > 0.0D) {
					canBuy2 = true;
				}

				if (plugin.getConfig().contains("BoosterPacks." + rarity + ".ExtraCardRarity") && plugin.getConfig().contains("BoosterPacks." + rarity + ".NumExtraCards")) {
					hasExtra = true;
				}

				++k;
				if (canBuy2) {
					sendPrefixedMessage(sender, "&6" + k + ") &e" + rarity + " &7(&aPrice: " + plugin.getConfig().getDouble("BoosterPacks." + rarity + ".Price") + "&7)");
				} else {
					sendPrefixedMessage(sender, "&6" + k + ") &e" + rarity);
				}

				if (hasExtra) {
					sendMessage(sender, "  &7- &f&o" + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumNormalCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".NormalCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumExtraCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".ExtraCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumSpecialCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".SpecialCardRarity"));
				} else {
					sendMessage(sender, "  &7- &f&o" + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumNormalCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".NormalCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumSpecialCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".SpecialCardRarity"));
				}

				canBuy2 = false;
			}
		}


		private void listRarity(final CommandSender sender, final Player target, final String rarity){
			final StringBuilder stringBuilder = new StringBuilder();
			final String sectionFormat = "&6--- %s &7(&c%d&f/&a%d&7)&6 ---";
			final String sectionFormatComplete = "&6--- %s &7(%sComplete&7)&6 ---";
			final ConfigurationSection rarityCardSection = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards."+rarity);
			final int cardTotal = rarityCardSection.getKeys(false).size();
			int cardCounter = 0;

			for(String cardName: rarityCardSection.getKeys(false)){
				if(cardCounter > 32){
					if (plugin.hasCard(target, cardName, rarity) > 0) {
						++cardCounter;
					}
					stringBuilder.append(cardName).append("&7and more!");
				} else {
					plugin.debug(rarity + ", " + cardName);

					String colour = plugin.getMainConfig().listHaveCardColour;
					if (plugin.hasShiny(target, cardName, rarity)) {
						++cardCounter;
						colour = plugin.getMainConfig().listHaveShinyCardColour;
						stringBuilder.append(colour).append(cardName.replaceAll("_", " ")).append("&f, ");
					} else if (plugin.hasCard(target, cardName, rarity) > 0 && !plugin.hasShiny(target, cardName, rarity)) {
						++cardCounter;
						stringBuilder.append(colour).append(cardName.replaceAll("_", " ")).append("&f, ");
					} else {
						stringBuilder.append("&7").append(cardName.replaceAll("_", " ")).append("&f, ");
					}
				}
			}
			//send title
			if(cardCounter == cardTotal){
				sendMessage(sender,String.format(sectionFormatComplete, plugin.isRarity(rarity),plugin.getConfig().getString("Colours.ListRarityComplete")));
			} else {
				sendMessage(sender,String.format(sectionFormat,plugin.isRarity(rarity),cardCounter,cardTotal));
			}

			sendMessage(sender,stringBuilder.toString());
		}
	}

	private void execCommand(final CommandSender sender, final String rarity, final String path) {
		if (plugin.getConfig().contains("Rarities." + plugin.isRarity(rarity) + path) && !plugin.getConfig().getString("Rarities." + plugin.isRarity(rarity) + path).equalsIgnoreCase("None")) {
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), plugin.getConfig().getString("Rarities." + plugin.isRarity(rarity + path).replaceAll("%player%", sender.getName())));
		}
	}

	@Subcommand("reward")
	@CommandPermission("cards.reward")
	@Description("Rewards a player with the rarity.")
	public void onReward(final CommandSender sender, final String rarity) {
		if (!plugin.getMainConfig().allowRewards) {
			sendMessage(sender, plugin.getPrefixedMessage(plugin.getMessagesConfig().rewardDisabled));
			return;
		}
		if (plugin.isRarity(rarity).equalsIgnoreCase("None")) {
			sendPrefixedMessage(sender, plugin.getMessagesConfig().rewardError);
			return;
		}

		if (plugin.completedRarity((Player) sender, plugin.isRarity(rarity))) {
			execCommand(sender, rarity, ".RewardCmd1");
			execCommand(sender, rarity, ".RewardCmd2");
			execCommand(sender, rarity, ".RewardCmd3");

			if (plugin.getConfig().getBoolean("General.Reward-Broadcast")) {
				Bukkit.broadcastMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().rewardBroadcast.replaceAll("%player%", sender.getName()).replaceAll("%rarity%", plugin.isRarity(rarity))));
			}
			//TODO wait, why does the plugin delete a rarity once its been completed?
			// instead it should mark in a data file if a player has completed the rarity or not...
			// playeruuid:rarityname,1:
			if (!plugin.deleteRarity((Player) sender, plugin.isRarity(rarity)) && plugin.getMainConfig().debugMode) {
				plugin.getLogger().warning("Cannot delete rarity: " + plugin.isRarity(rarity));
			}
		} else if (plugin.getConfig().getBoolean("General.Eat-Shiny-Cards")) {
			sendPrefixedMessage(sender, plugin.getMessagesConfig().rewardError2);
		} else {
			sendPrefixedMessage(sender, plugin.getMessagesConfig().rewardError3.replaceAll("%shinyName%", plugin.getMainConfig().shinyName));
		}


	}

	@Subcommand("giveaway")
	@CommandPermission("cards.giveaway")
	@Description("Give away a random card by rarity to the server.")
	public void onGiveaway(final CommandSender sender, final String rarity) {
		ConfigurationSection rarities = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards");
		Set<String> rarityKeys = rarities.getKeys(false);
		String keyToUse = "";
		if (plugin.isMob(rarity)) {
			if (sender instanceof ConsoleCommandSender) {
				plugin.giveawayNatural(EntityType.valueOf(rarity.toUpperCase()), null);
			} else {
				plugin.giveawayNatural(EntityType.valueOf(rarity.toUpperCase()), (Player) sender);
			}
		} else {

			for (final String rarityKey : rarityKeys) {
				if (rarityKey.equalsIgnoreCase(rarity.replaceAll("_", " "))) {
					keyToUse = rarityKey;
				}
			}

			if (!keyToUse.equals("")) {
				Bukkit.broadcastMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveaway.replaceAll("%player%", sender.getName()).replaceAll("%rarity%", keyToUse)));

				for (final Player p5 : Bukkit.getOnlinePlayers()) {
					ConfigurationSection cards4 = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + keyToUse);
					Set<String> cardKeys4 = cards4.getKeys(false);
					int rIndex = plugin.r.nextInt(cardKeys4.size());
					int l = 0;
					String cardName = "";

					for (Iterator<String> var51 = cardKeys4.iterator(); var51.hasNext(); ++l) {
						String theCardName = var51.next();
						if (l == rIndex) {
							cardName = theCardName;
							break;
						}
					}
					CardUtil.dropItem(p5, CardManager.getCard(cardName,keyToUse,false));
				}
			} else {
				sendPrefixedMessage(sender, plugin.getMessagesConfig().noRarity);
			}
		}


	}

	@Subcommand("worth")
	@CommandPermission("cards.worth")
	@Description("Shows a card's worth.")
	public void onWorth(final Player player) {
		if (!hasVault(player)) {
			return;
		}
		if (!CardUtil.isCard(player.getInventory().getItemInMainHand())) {
			sendPrefixedMessage(player, plugin.getMessagesConfig().notACard);
			return;
		}

		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		final String keyToUse = itemInHand.getItemMeta().getDisplayName();
		plugin.debug(keyToUse);
		plugin.debug(ChatColor.stripColor(keyToUse));

		String[] splitName = ChatColor.stripColor(keyToUse).split(" ");
		String cardName2 = splitName.length > 1 ? splitName[1] : splitName[0];
		plugin.debug("card="+cardName2);


		List<String> lore = itemInHand.getItemMeta().getLore();
		String rarity = ChatColor.stripColor(lore.get(lore.size()-1));
		plugin.debug("rarity="+rarity);

		double buyPrice = getBuyPrice(rarity,cardName2);
		double sellPrice = getSellPrice(rarity,cardName2);
		String buyMessage = (buyPrice > 0.0D) ? plugin.getMessagesConfig().canBuy.replaceAll("%buyAmount%", String.valueOf(buyPrice)) : plugin.getMessagesConfig().canNotBuy;
		String sellMessage = (buyPrice > 0.0D) ? plugin.getMessagesConfig().canSell.replaceAll("%sellAmount%", String.valueOf(sellPrice)) : plugin.getMessagesConfig().canNotSell;
		plugin.debug("buy="+buyPrice+"|sell="+sellPrice);
		sendPrefixedMessage(player,buyMessage);
		sendPrefixedMessage(player,sellMessage);
	}

	private double getBuyPrice(final String rarity, final String cardName){
		return plugin.getCardsConfig().getConfig().getDouble("Cards." + rarity + "." + cardName + ".Buy-Price", 0.0D);
	}

	private double getSellPrice(final String rarity, final String cardName){
		return plugin.getCardsConfig().getConfig().getDouble("Cards." + rarity + "." + cardName + ".Sell-Price", 0.0D);
	}

	private boolean hasVault(final Player player) {
		if (!plugin.isHasVault()) {
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().noVault));
			return false;
		}
		return true;
	}

	@Subcommand("sell")
	@CommandPermission("cards.sell")
	public class SellSubCommand extends BaseCommand {
		@Default
		@Description("Sells the card in your main hand.")
		public void onSell(final Player player){
			if(!hasVault(player))
				return;

			if (player.getInventory().getItemInMainHand().getType() != Material.valueOf(plugin.getMainConfig().cardMaterial)) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().notACard);
				return;
			}


			final ItemStack itemInHand = player.getInventory().getItemInMainHand();
			final int itemInHandSlot = player.getInventory().getHeldItemSlot();
			final String[] splitName = ChatColor.stripColor(itemInHand.getItemMeta().getDisplayName()).split(" ");
			final String card = (splitName.length>1) ? splitName[1] : splitName[0];

			plugin.debug(card);


			List<String> lore = itemInHand.getItemMeta().getLore();
			Validate.notNull(lore, "Lore cannot be null.");
			String rarity = ChatColor.stripColor(lore.get(lore.size()-1));
			plugin.debug(rarity);

			if(!plugin.getCardsConfig().getConfig().contains("Cards."+rarity+"."+card+".Sell-Price")){
				if(card.contains(plugin.getMainConfig().shinyName))
					sendPrefixedMessage(player,"Cannot sell shiny card.");
				sendPrefixedMessage(player,"Cannot sell this card.");
				return;
			}

			final double sellPrice = getSellPrice(rarity,card);
			if(sellPrice == 0) {
				sendPrefixedMessage(player,"Cannot sell this card.");
				return;
			}

			PlayerInventory inventory = player.getInventory();
			double sellAmount = sellPrice*itemInHand.getAmount();
			EconomyResponse economyResponse = econ.depositPlayer(player,sellAmount);
			if(economyResponse.transactionSuccess()){
				sendPrefixedMessage(player,String.format("You have sold %dx%s for %.2f", itemInHand.getAmount(), (rarity+" "+card),sellAmount));
				inventory.setItem(itemInHandSlot, null);
			}
		}
	}


	@Subcommand("buy")
	@CommandPermission("cards.buy")
	public class BuySubCommand extends BaseCommand {

		@Subcommand("pack|boosterpack")
		@CommandPermission("cards.buy.pack")
		@Description("Buy a pack.")
		public void onBuyPack(final Player player, final String name) {
			if (!hasVault(player))
				return;
			if (!plugin.getConfig().contains("BoosterPacks." + name)) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().packDoesntExist);
				return;
			}


			double buyPrice2 = plugin.getConfig().getDouble("BoosterPacks." + name + ".Price", 0.0D);

			if (buyPrice2 <= 0.0D) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().cannotBeBought);
				return;
			}

			EconomyResponse economyResponse = econ.withdrawPlayer(player, buyPrice2);
			if (economyResponse.transactionSuccess()) {
				if (plugin.getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {
					econ.bankDeposit(plugin.getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
				}
				sendPrefixedMessage(player, plugin.getMessagesConfig().boughtCard.replaceAll("%amount%", String.valueOf(buyPrice2)));
				CardUtil.dropItem(player, CardManager.generatePack(name));
				return;
			}

			sendPrefixedMessage(player, plugin.getMessagesConfig().notEnoughMoney);
		}


		@Subcommand("card")
		@CommandPermission("cards.buy.card")
		@Description("Buy a card.")
		public void onBuyCard(final Player player, @NotNull final String rarity, @NotNull final String card) {
			if (!hasVault(player))
				return;

			if (!plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + card)) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().cardDoesntExist);
				return;
			}


			double buyPrice2 = plugin.getCardsConfig().getConfig().getDouble("Cards." + rarity + "." + card + ".Buy-Price", 0.0D);

			EconomyResponse economyResponse = econ.withdrawPlayer(player, buyPrice2);
			if (economyResponse.transactionSuccess()) {
				if (plugin.getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {
					econ.bankDeposit(plugin.getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
				}
				CardUtil.dropItem(player, CardManager.getCard(card, rarity,false));
				sendPrefixedMessage(player, plugin.getMessagesConfig().boughtCard.replaceAll("%amount%", String.valueOf(buyPrice2)));
				return;
			}
			sendPrefixedMessage(player, plugin.getMessagesConfig().notEnoughMoney);
		}
	}
}





