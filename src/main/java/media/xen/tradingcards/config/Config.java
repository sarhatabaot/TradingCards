package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Config {
	private static FileConfiguration configuration;

	public static void init(final FileConfiguration configuration){
		Config.configuration = configuration;
	}

	public static ItemStack getBlankCard(int quantity) {
		return new ItemStack(Material.getMaterial(configuration.getString("General.Card-Material")), quantity);
	}

	public static ItemStack getBlankBoosterPack() {
		return new ItemStack(Material.getMaterial(configuration.getString("General.BoosterPack-Material")));
	}

	public static ItemStack getBlankDeck() {
		return new ItemStack(Material.getMaterial(configuration.getString("General.Deck-Material")));
	}
}
