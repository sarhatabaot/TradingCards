package media.xen.tradingcards.config;

import net.sarhatabaot.configloader.Config;
import net.sarhatabaot.configloader.ConfigOption;
import net.sarhatabaot.configloader.transform.ListClone;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TradingCardsConfig implements Config {
	private final File file;

	public TradingCardsConfig(final File file) {
		this.file = file;
	}

	@ConfigOption(path = "General.Server-Name")
	public String serverName = "Server";

	@ConfigOption(path = "General.Debug-Mode")
	public boolean debugMode = false;

	@ConfigOption(path = "General.Hide-Enchants")
	public boolean hideEnchants = true;

	@ConfigOption(path = "General.American-Mode")
	public boolean americanMode = false;

	@ConfigOption(path = "General.Card-Material")
	public String cardMaterial = "PAPER";

	@ConfigOption(path = "General.Card-Prefix")
	public String cardPrefix = "&7[&fCard&7]&f ";

	@ConfigOption(path = "General.BoosterPack-Material")
	public String boosterPackMaterial = "BOOK";
	public String boosterPackPrefix = "&7[&fPack&7]&f ";
	public boolean decksInCreative = false;
	public boolean useLargeDecks = false;
	@ConfigOption(path = "General.Deck-Material")
	public String deckMaterial = "BOOK";
	public String deckPrefix = "&7[&fDeck&7]&f ";
	public String shinyName = "Shiny";
	public boolean scheduleCards = false;
	public boolean scheduleCardNatural = false;
	public String scheduleCardMob = "ZOMBIE";
	public String scheduleCardRarity = "Common";
	public int scheduleCardTimeInHours = 1;
	public boolean spawnerBlock = true;
	public boolean autoAddPlayers = false;
	public String autoAddPlayersRarity = "Common";
	public String playerOpRarity = "Legendary";
	public String playerSeries = "2020";
	public String playerType = "Player";
	public boolean playerHasShinyVersion = true;
	public boolean playerDropCard = true;
	public int playerDropCardRarity = 100;
	public int infoLineLength = 25;
	public boolean dropDeckItems = true;
	public boolean allowRewards = true;
	public boolean eatShinyCards = false;

	@ConfigOption(path = "General.SQLite")
	public boolean SQLite = false;

	@ConfigOption(path = "World-Blacklist", transform = ListClone.class)
	public List<String> worldBlackList = new ArrayList<>();

	@ConfigOption(path = "Blacklist.Whitelist-Mode")
	public boolean blacklistWhitelistMode = false;
	@ConfigOption(path = "Blacklist.Players", transform = ListClone.class)
	public List<String> blacklistPlayers = Collections.singletonList("Herobrine");

	@ConfigOption(path = "PluginSupport.Vault.Vault-Enabled")
	public boolean vaultEnabled = true;
	@ConfigOption(path = "PluginSupport.Vault.Closed-Economy")
	public boolean closedEconomy = false;
	@ConfigOption(path = "PluginSupport.Vault.Server-Account")
	public String serverAccount = "TradingCards-Bank";

	public String cardsTitle = "%PREFIX%%COLOUR%%NAME%";


	public static ItemStack getBlankCard(int quantity) {
		return new ItemStack(Material.getMaterial(configuration.getString("General.Card-Material")), quantity);
	}

	public static ItemStack getBlankBoosterPack() {
		return new ItemStack(Material.getMaterial(configuration.getString("General.BoosterPack-Material")));
	}

	public static ItemStack getBlankDeck() {
		return new ItemStack(Material.getMaterial(configuration.getString("General.Deck-Material")));
	}

	@Override
	public File getFile() {
		return file;
	}
}
