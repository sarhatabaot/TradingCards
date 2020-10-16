package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import net.sarhatabaot.configloader.Config;
import net.sarhatabaot.configloader.ConfigOption;

import java.io.File;


public class MessagesConfig implements Config {
	private File file;

	public MessagesConfig(final TradingCards plugin) {
		this.file = new File(plugin.getDataFolder(),"messages.yml");
	}
	@ConfigOption(path = "Messages.Prefix")
	public String prefix= "&7[&fCards&7]&7";
	@ConfigOption(path = "Messages.Reload")
	public String reload= "Successfully reloaded config!";
	@ConfigOption(path = "Messages.NoCard")
	public String noCard= "No such card exists! Make sure to use the EXACT card name!";
	@ConfigOption(path = "Messages.NoPlayer")
	public String noPlayer= "That player does not exist!";
	@ConfigOption(path = "Messages.NoCmd")
	public String noCmd= "Invalid command!";
	@ConfigOption(path = "Messages.NoEntity")
	public String noEntity= "That entity/mob does not exist!";
	@ConfigOption(path = "Messages.NoCreative")
	public String noCreative= "You cannot open booster packs in creative!";
	@ConfigOption(path = "Messages.NoRarity")
	public String noRarity= "That rarity does not exist!";
	@ConfigOption(path = "Messages.NoBoosterPack")
	public String noBoosterPack= "That booster pack does not exist!";
	@ConfigOption(path = "Messages.ScheduledGiveaway")
	public String scheduledGiveaway= "A card has been given to everyone on the server!";
	@ConfigOption(path = "Messages.Giveaway")
	public String giveaway= "%player% has given everyone a random card of rarity %rarity%!";
	@ConfigOption(path = "Messages.GiveawayNatural")
	public String giveawayNatural= "%player% has given everyone a random card!";
	@ConfigOption(path = "Messages.GiveawayNaturalBoss")
	public String giveawayNaturalBoss= "%player% has given everyone a random boss mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalHostile")
	public String giveawayNaturalHostile= "%player% gave everyone a random hostile mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalPassive")
	public String giveawayNaturalPassive= "%player% gave everyone a random passive mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalNeutral")
	public String giveawayNaturalNeutral= "%player% gave everyone a random neutral mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalBossNoPlayer")
	public String giveawayNaturalBossNoPlayer= "Everyone's received a random boss mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalPassiveNoPlayer")
	public String giveawayNaturalPassiveNoPlayer= "Everyone's received a random passive mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalHostileNoPlayer")
	public String giveawayNaturalHostileNoPlayer= "Everyone's received a random hostile mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalNeutralNoPlayer")
	public String giveawayNaturalNeutralNoPlayer= "Everyone's received a random neutral mob card!";
	@ConfigOption(path = "Messages.GiveawayNaturalNoPlayer")
	public String giveawayNaturalNoPlayer= "Everyone's received a random card!";
	@ConfigOption(path = "Messages.GiveRandomCard")
	public String giveRandomCard= "You have been given a random card!";
	@ConfigOption(path = "Messages.GiveRandomCardMsg")
	public String giveRandomCardMsg= "You have given %player% a random card!";
	@ConfigOption(path = "Messages.BoosterPackMsg")
	public String boosterPackMsg= "You have been given a booster pack!";
	@ConfigOption(path = "Messages.OpenBoosterPack")
	public String openBoosterPack= "You opened a booster pack!";
	@ConfigOption(path = "Messages.ListError")
	public String listError= "%name% is not online, or is not a rarity!";
	@ConfigOption(path = "Messages.CanBuy")
	public String canBuy= "This card can be bought for %buyAmount%!";
	@ConfigOption(path = "Messages.CanNotBuy")
	public String canNotBuy= "This card cannot be bought!";
	@ConfigOption(path = "Messages.ChooseCard")
	public String chooseCard= "Please specify a card!";
	@ConfigOption(path = "Messages.ChooseRarity")
	public String chooseRarity= "Please specify a rarity!";
	@ConfigOption(path = "Messages.ChoosePack")
	public String choosePack= "Please specify a pack!";
	@ConfigOption(path = "Messages.CannotBeBought")
	public String cannotBeBought= "This cannot be bought!";
	@ConfigOption(path = "Messages.NotEnoughMoney")
	public String notEnoughMoney= "You do not have enough money to buy this!";
	@ConfigOption(path = "Messages.BoughtCard")
	public String boughtCard="Successfully bought for %amount%!";
	@ConfigOption(path = "Messages.NotACard")
	public String notACard= "You need to be holding a card!";
	@ConfigOption(path = "Messages.CardDoesntExist")
	public String cardDoesntExist= "That card does not exist! Make sure to use the exact card and rarity names with proper capitalization.";
	@ConfigOption(path = "Messages.PackDoesntExist")
	public String packDoesntExist= "That pack does not exist! Make sure to use the exact card and rarity names with proper capitalization.";
	@ConfigOption(path = "Messages.NoVault")
	public String noVault= "This server has disabled economy interactions!";
	@ConfigOption(path = "Message.DeckCreativeError")
	public String deckCreativeError= "You are not allowed to use decks in creative!";
	@ConfigOption(path = "Messages.GiveDeck")
	public String giveDeck= "You got a deck! Hold it and right click to open!";
	@ConfigOption(path = "Messages.AlreadyHaveDeck")
	public String alreadyHaveDeck= "You already have that deck in your inventory!";
	@ConfigOption(path = "Messages.MaxDecks")
	public String maxDecks= "You do not have permission for this deck!";
	@ConfigOption(path = "Messages.CreateNoName")
	public String createNoName= "Invalid name! Alphanumeric and underscores only!";
	@ConfigOption(path = "Messages.CreateExists")
	public String createExists= "That card already exists!";
	@ConfigOption(path = "Messages.CreateSuccess")
	public String createSuccess= "Successfully created %name%, rarity %rarity%!";
	@ConfigOption(path = "Messages.TimerMessage")
	public String timerMessage= "Next card giveaway in %hour% hour(s)!";
	@ConfigOption(path = "Messages.ToggleEnabled")
	public String toggleEnabled= "Cards are now enabled!";
	@ConfigOption(path = "Messages.ToggleDisabled")
	public String toggleDisabled= "Cards are now disabled!";
	@ConfigOption(path = "Messages.ResolveMsg")
	public String resolveMsg= "%name% = %uuid%";
	@ConfigOption(path = "Messages.ResolveError")
	public String resolveError= "%name% is not online!";
	@ConfigOption(path = "Messages.RewardError")
	public String rewardError= "That rarity does not exist!";
	@ConfigOption(path = "Messages.RewardError2")
	public String rewardError2= "You have not collected all of that rarity!";
	@ConfigOption(path = "Messages.RewardError3")
	public String rewardError3= "You have not collected all of that rarity! %shinyName% cards do not count.";
	@ConfigOption(path = "Messages.RewardBroadcast")
	public String rewardBroadcast= "%player% has collected all %rarity% cards!";
	@ConfigOption(path = "Messages.RewardDisabled")
	public String rewardDisabled= "Rewards have been disabled!";

	@Override
	public File getFile() {
		return file;
	}
}
