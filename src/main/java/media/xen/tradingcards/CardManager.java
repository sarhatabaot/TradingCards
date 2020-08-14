
package media.xen.tradingcards;


import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.smartcardio.Card;
import java.util.List;
import java.util.Map;

public class CardManager {
	private final TradingCards plugin;
	private static ItemStack blankCard;
	private static Map<String,ItemStack> cards;

	public CardManager(final TradingCards plugin) {
		this.plugin = plugin;
	}

	private void initialize() {
		blankCard = new ItemStack(Material.getMaterial(plugin.getConfig().getString("General.Card-Material")));
	}


	public ItemStack getCard(final String cardName){
		return cards.get(cardName.toLowerCase());
	}

	public static class CardBuilder {
		private final String cardName;
		private boolean isShiny = false;
		private boolean isPlayerCard = false;
		private String rarityColour;
		private String prefix;
		private CardInfo series;
		private CardInfo about;
		private CardInfo type;
		private CardInfo info;
		private String shinyPrefix;
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

		public CardBuilder isPlayerCard(boolean isPlayerCard){
			this.isPlayerCard = isPlayerCard;
			return this;
		}

		public ItemStack build(){
			ItemStack card = blankCard.clone();
			ItemMeta cardMeta = card.getItemMeta();

			if(isShiny){
				if(isPlayerCard) {

				} else {

				}
			} else if(isPlayerCard){

			} else {

			}

			
			return null;
		}

		private String getDisplayName(boolean isShiny, boolean isPlayerCard,)


	}

	@Data
	public static class CardInfo {
		private final String name;
		private final String colour;
		private final String display;
	}
}
