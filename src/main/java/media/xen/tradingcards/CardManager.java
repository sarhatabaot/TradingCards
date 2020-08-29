
package media.xen.tradingcards;


import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardManager {
	private static TradingCards plugin;
	private static ItemStack blankCard;
	private static Map<String,ItemStack> cards;

	public CardManager(final TradingCards plugin) {
		CardManager.plugin = plugin;
		initialize();
	}

	/**
	 * Pre-loads all existing cards.
	 */
	private void initialize() {
		CardManager.blankCard = new ItemStack(Material.getMaterial(plugin.getConfig().getString("General.Card-Material")));
		for(String rarity: plugin.getCardsConfig().getConfig().getConfigurationSection("Cards").getKeys(false)){
			for(String name: plugin.getCardsConfig().getConfig().getConfigurationSection("Cards."+rarity).getKeys(false)) {
				cards.put(rarity+"."+name, CardUtil.generateCard(name,rarity,false));
			}
		}

		plugin.getLogger().info(String.format("Loaded %d cards.",cards.size()));
	}


	/**
	 *
	 * @param cardName
	 * @param rarity
	 * @return false if cards already exists. True if successfully added.
	 */
	public boolean addCard(final String cardName, final String rarity){
		if(cards.containsKey(rarity+"."+cardName))
			return false;
		cards.put(rarity+"."+cardName, CardUtil.generateCard(cardName,rarity,false));
		return true;
	}

	public ItemStack getCard(final String cardName,final String rarity){
		return cards.get(rarity+"."+cardName);
	}

	public static class CardBuilder {
		private final String cardName;
		private String rarity;
		private boolean isShiny = false;
		private boolean isPlayerCard = false;
		private String rarityColour;
		private String prefix;
		private CardInfo series;
		private CardInfo about;
		private CardInfo type;
		private CardInfo info;
		private String shinyPrefix = null;
		private String cost;

		public CardBuilder(final String cardName) {
			this.cardName = cardName;
		}

		public CardBuilder isShiny(boolean isShiny){
			this.isShiny = isShiny;
			return this;
		}

		public CardBuilder rarityColour(String rarityColour){
			this.rarityColour = rarityColour;
			return this;
		}

		public CardBuilder prefix(String prefix){
			this.prefix = prefix;
			return this;
		}

		public CardBuilder series(String name, String colour, String display){
			this.series = new CardInfo(name,colour,display);
			return this;
		}

		public CardBuilder about(String name, String colour, String display){
			this.about = new CardInfo(name,colour,display);
			return this;
		}

		public CardBuilder type(String name, String colour, String display){
			this.type = new CardInfo(name,colour,display);
			return this;
		}

		public CardBuilder info(String name, String colour, String display){
			this.info = new CardInfo(name,colour,display);
			return this;
		}

		public CardBuilder shinyPrefix(String shinyPrefix){
			this.shinyPrefix = shinyPrefix;
			return this;
		}

		public CardBuilder cost(String cost){
			this.cost = cost;
			return this;
		}

		public CardBuilder rarity(String rarity){
			this.rarity = rarity;
			return this;
		}

		public CardBuilder isPlayerCard(boolean isPlayerCard){
			this.isPlayerCard = isPlayerCard;
			return this;
		}

		public ItemStack build(){
			ItemStack card = blankCard.clone();
			ItemMeta cardMeta = blankCard.getItemMeta();
			cardMeta.setDisplayName(formatDisplayName(isPlayerCard,isShiny,prefix,rarityColour,cardName,cost,shinyPrefix));
			cardMeta.setLore(formatLore());
			if (plugin.getConfig().getBoolean("General.Hide-Enchants", true)) {
				cardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}

			card.setItemMeta(cardMeta);
			return card;
		}

		private List<String> formatLore(){
			List<String> lore = new ArrayList<>();
			lore.add(plugin.cMsg(type.getColour() + type.getDisplay() + ": &f" + type.getName()));
			if (!"None".equals(info) && !"".equals(info)) {
				lore.add(plugin.cMsg(info.getColour() + info.getDisplay() + ":"));
				lore.addAll(plugin.wrapString(info.getName()));
			} else {
				lore.add(plugin.cMsg(info.getColour() + info.getDisplay() + ": &f" + info.getName()));
			}

			lore.add(plugin.cMsg(series.getColour() + series.getDisplay() + ": &f" + series.getName()));
			if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + cardName + ".About")) {
				lore.add(plugin.cMsg(about.getColour() + about.getDisplay() + ": &f" + about.getName()));
			}

			if (isShiny) {
				lore.add(plugin.cMsg(rarityColour + ChatColor.BOLD + plugin.getConfig().getString("General.Shiny-Name") + " " +rarity ));
			} else {
				lore.add(plugin.cMsg(rarityColour + ChatColor.BOLD + rarity));
			}

			return lore;
		}
		@NotNull
		private String formatDisplayName(boolean isPlayerCard, boolean isShiny, String prefix, String rarityColour, String cardName, String cost, String shinyPrefix) {
			final String[] shinyPlayerCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "%SHINYPREFIX%"};
			final String[] shinyCardFormat = new String[]{"%PREFIX%", "%COLOUR%", "%NAME%", "%COST%", "%SHINYPREFIX%", "_"};

			final String[] cardFormat = new String[]{"%PREFIX%","%COLOUR%","%NAME%","%COST%","_"};
			final String[] playerCardFormat = new String[]{"%PREFIX%","%COLOUR%","%NAME%","%COST%"};


			final String shinyTitle = plugin.getConfig().getString("DisplayNames.Cards.ShinyTitle");
			final String title = plugin.getConfig().getString("DisplayNames.Cards.Title");
			if (isShiny && shinyPrefix!= null) {
				if (isPlayerCard) {
					return plugin.cMsg(StringUtils.replaceEach(shinyTitle, shinyPlayerCardFormat, new String[]{prefix,rarityColour, cardName, cost, shinyPrefix}));
				}
				return plugin.cMsg(StringUtils.replaceEach(shinyTitle, shinyCardFormat, new String[]{prefix, rarityColour,cardName, cost, shinyPrefix, " "}));
			}
			if (isPlayerCard) {
				return plugin.cMsg(StringUtils.replaceEach(title, playerCardFormat, new String[]{prefix,rarityColour,cardName,cost}));
			}
			return plugin.cMsg(StringUtils.replaceEach(title, cardFormat, new String[]{prefix,rarityColour,cardName,cost, " "}));
		}

	}

	@Data
	public static class CardInfo {
		private final String name;
		private final String colour;
		private final String display;
	}
}
