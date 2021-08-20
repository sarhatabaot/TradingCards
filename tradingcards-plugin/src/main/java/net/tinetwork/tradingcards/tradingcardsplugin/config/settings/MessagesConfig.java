package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.spongepowered.configurate.ConfigurateException;

public class MessagesConfig extends SimpleConfigurate {
    private String prefix;
    private String reload;
    private String noCard;
    private String noPlayer;
    private String noCmd;
    private String noEntity;
    private String noCreative;
    private String noRarity;
    private String noBoosterPack;
    private String scheduledGiveaway;
    private String giveaway;
    private String giveawayNatural;
    private String giveawayNaturalBoss;
    private String giveawayNaturalHostile;
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

        this.prefix = rootNode.node("prefix").getString("Cards > ");
        this.reload = rootNode.node("reload").getString("Reloaded config.");
        this.noCard = rootNode.node("no-card").getString();
        this.noPlayer = rootNode.node("no-player").getString();
        this.noCmd  = rootNode.node("no-cmd").getString();
        this.noEntity  = rootNode.node("no-entity").getString();
        this.noCreative = rootNode.node("no-creative").getString();
        this.noRarity = rootNode.node("no-rarity").getString();
        this.noBoosterPack = rootNode.node("no-booster-pack").getString();
        this.scheduledGiveaway = rootNode.node("scheduled-giveaway").getString();
        this.giveaway= rootNode.node("giveaway").getString();
        this.giveawayNatural = rootNode.node("giveaway-natural").getString();
        this.giveawayNaturalBoss = rootNode.node("giveaway-natural-boss").getString();
        this.giveawayNaturalHostile = rootNode.node("giveaway-natural-hostile").getString();

    }
}
