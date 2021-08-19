package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigFile;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;

public class MessagesConfig extends SimpleConfigFile {
    private final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().
            path(Paths.get("settings/messages",".yml")).build();
    private CommentedConfigurationNode rootNode;

    private String prefix = "&7[&fCards&7]&7";
    private String reload = "Successfully reloaded config!";
    private String noCard = "No such card exists! Make sure to use the EXACT card name!";
    private String noPlayer = "That player does not exist!";
    private String noCmd = "Invalid command!";
    private String noEntity = "That entity/mob does not exist!";
    private String noCreative = "You cannot open booster packs in creative!";
    private String noRarity = "That rarity does not exist!";
    private String noBoosterPack = "That booster pack does not exist!";
    private String scheduledGiveaway = "A card has been given to everyone on the server!";
    private String giveaway = "%player% has given everyone a random card of rarity %rarity%!";
    private String giveawayNatural = "%player% has given everyone a random card!";
    private String giveawayNaturalBoss = "%player% has given everyone a random boss mob card!";
    private String giveawayNaturalHostile = "%player% gave everyone a random hostile mob card!";
    private String giveawayNaturalPassive = "%player% gave everyone a random passive mob card!";
    private String giveawayNaturalNeutral = "%player% gave everyone a random neutral mob card!";
    private String giveawayNaturalBossNoPlayer = "Everyone's received a random boss mob card!";
    private String giveawayNaturalPassiveNoPlayer = "Everyone's received a random passive mob card!";
    private String giveawayNaturalHostileNoPlayer = "Everyone's received a random hostile mob card!";
    private String giveawayNaturalNeutralNoPlayer = "Everyone's received a random neutral mob card!";
    private String giveawayNaturalNoPlayer = "Everyone's received a random card!";
    private String giveRandomCard = "You have been given a random card!";
    private String giveRandomCardMsg = "You have given %player% a random card!";
    private String boosterPackMsg = "You have been given a booster pack!";
    private String openBoosterPack = "You opened a booster pack!";
    private String listError = "%name% is not online, or is not a rarity!";
    private String canBuy = "This card can be bought for %buyAmount%!";
    private String canNotBuy = "This card cannot be bought!";
    private String canSell = "This card can be sold for %sellAmount%!";
    private String canNotSell = "This card cannot be sold!";
    private String chooseCard = "Please specify a card!";
    private String chooseRarity = "Please specify a rarity!";
    private String choosePack = "Please specify a pack!";
    private String cannotBeBought = "This cannot be bought!";
    private String notEnoughMoney = "You do not have enough money to buy this!";
    private String boughtCard = "Successfully bought for %amount%!";
    private String notACard = "You need to be holding a card!";
    private String cardDoesntExist = "That card does not exist! Make sure to use the exact card and rarity names with proper capitalization.";
    private String packDoesntExist = "That pack does not exist! Make sure to use the exact card and rarity names with proper capitalization.";
    private String noVault = "This server has disabled economy interactions!";
    private String deckCreativeError = "You are not allowed to use decks in creative!";
    private String giveDeck = "You got a deck! Hold it and right click to open!";
    private String alreadyHaveDeck = "You already have that deck in your inventory!";
    private String maxDecks = "You do not have permission for this deck!";
    private String createNoName = "Invalid name! Alphanumeric and underscores only!";
    private String createExists = "That card already exists!";
    private String createSuccess = "Successfully created %name%, rarity %rarity%!";
    private String timerMessage = "Next card giveaway in %hour% hour(s)!";
    private String toggleEnabled = "Cards are now enabled!";
    private String toggleDisabled = "Cards are now disabled!";
    private String resolveMsg = "%name% = %uuid%";
    private String resolveError = "%name% is not online!";
    private String rewardError = "That rarity does not exist!";
    private String rewardError2 = "You have not collected all of that rarity!";
    private String rewardError3 = "You have not collected all of that rarity! %shinyName% cards do not count.";
    private String rewardBroadcast = "%player% has collected all %rarity% cards!";
    private String rewardDisabled = "Rewards have been disabled!";


    public MessagesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "messages.yml", "settings");

        this.rootNode = loader.load();
    }
}