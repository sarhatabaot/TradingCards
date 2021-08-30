package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigurate;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;

public class MessagesConfig extends SimpleConfigurate {
    private final String prefix;
    private final String reload;
    private final String noCard;
    private final String noPlayer;
    private final String noCmd;
    private final String noEntity;
    private final String noCreative;
    private final String noRarity;
    private final String noBoosterPack;
    private final String scheduledGiveaway;
    private final String giveaway;
    private final String giveawayNatural;
    private final String giveawayNaturalBoss;
    private final String giveawayNaturalHostile;
    private final String giveawayNaturalPassive;
    private final String giveawayNaturalNeutral;
    private final String giveawayNaturalBossNoPlayer;
    private final String giveawayNaturalPassiveNoPlayer;
    private final String giveawayNaturalHostileNoPlayer;
    private final String giveawayNaturalNeutralNoPlayer;
    private final String giveawayNaturalNoPlayer;
    private final String giveRandomCard;
    private final String giveRandomCardMsg;
    private final String boosterPackMsg;
    private final String openBoosterPack;
    private final String listError;
    private final String canBuy;
    private final String canNotBuy;
    private final String canSell;
    private final String canNotSell;
    private final String chooseCard;
    private final String chooseRarity;
    private final String choosePack;
    private final String cannotBeBought;
    private final String notEnoughMoney;
    private final String boughtCard;
    private final String notACard;
    private final String cardDoesntExist;
    private final String packDoesntExist;
    private final String noVault;
    private final String deckCreativeError;
    private final String giveDeck;
    private final String alreadyHaveDeck;
    private final String maxDecks;
    private final String createNoName;
    private final String createExists;
    private final String createSuccess;
    private final String timerMessage;
    private final String toggleEnabled;
    private final String toggleDisabled;
    private final String resolveMsg;
    private final String resolveError;
    private final String rewardError;
    private final String rewardError2;
    private final String rewardError3;
    private final String rewardBroadcast;
    private final String rewardDisabled;

    private final String sectionFormat;
    private final String sectionFormatPlayer;
    private final String sectionFormatComplete;
    private final String packSection;


    public MessagesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin,"settings"+ File.separator ,"messages.yml", "settings");

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
        this.giveawayNaturalPassive = rootNode.node("giveaway-natural-passive").getString();
        this.giveawayNaturalNeutral = rootNode.node("giveaway-natural-neutral").getString();
        this.giveawayNaturalBossNoPlayer = rootNode.node("giveaway-natural-boss-no-player").getString();
        this.giveawayNaturalPassiveNoPlayer = rootNode.node("giveaway-natural-passive-no-player").getString();
        this.giveawayNaturalHostileNoPlayer = rootNode.node("giveaway-natural-hostile-no-player").getString();
        this.giveawayNaturalNeutralNoPlayer = rootNode.node("giveaway-natural-neutral-no-player").getString();
        this.giveawayNaturalNoPlayer = rootNode.node("giveaway-natural-no-player").getString();
        this.giveRandomCard = rootNode.node("give-random-card").getString();
        this.giveRandomCardMsg = rootNode.node("give-random-card-msg").getString();
        this.boosterPackMsg = rootNode.node("booster-pack-msg").getString();
        this.openBoosterPack = rootNode.node("open-booster-pack").getString();
        this.listError = rootNode.node("list-error").getString();
        this.canBuy = rootNode.node("can-buy").getString();
        this.canNotBuy = rootNode.node("cannot-buy").getString();
        this.canSell = rootNode.node("can-sell").getString();
        this.canNotSell = rootNode.node("cannot-sell").getString();
        this.chooseCard = rootNode.node("choose-card").getString();
        this.choosePack = rootNode.node("choose-pack").getString();
        this.chooseRarity = rootNode.node("choose-rarity").getString();
        this.cannotBeBought = rootNode.node("cannot-be-bought").getString();
        this.notEnoughMoney = rootNode.node("not-enough-money").getString();
        this.boughtCard = rootNode.node("bought-card").getString();
        this.notACard = rootNode.node("not-a-card").getString();
        this.cardDoesntExist = rootNode.node("card-doesnt-exist").getString();
        this.packDoesntExist = rootNode.node("pack-doesnt-exist").getString();
        this.noVault = rootNode.node("no-vault").getString();
        this.deckCreativeError = rootNode.node("deck-creative-error").getString();
        this.giveDeck = rootNode.node("give-deck").getString();
        this.alreadyHaveDeck = rootNode.node("already-have-deck").getString();
        this.maxDecks = rootNode.node("max-decks").getString();
        this.createNoName = rootNode.node("create-no-name").getString();
        this.createExists = rootNode.node("create-exists").getString();
        this.createSuccess = rootNode.node("create-success").getString();
        this.timerMessage = rootNode.node("timer-message").getString();
        this.toggleEnabled = rootNode.node("toggle-enabled").getString();
        this.toggleDisabled = rootNode.node("toggle-disabled").getString();
        this.resolveMsg = rootNode.node("resolve-msg").getString();
        this.resolveError = rootNode.node("resolve-error").getString();
        this.rewardError = rootNode.node("reward-error").getString();
        this.rewardError2 = rootNode.node("reward-error2").getString();
        this.rewardError3 = rootNode.node("reward-error3").getString();
        this.rewardBroadcast = rootNode.node("reward-broadcast").getString();
        this.rewardDisabled = rootNode.node("reward-disabled").getString();
        this.sectionFormat = rootNode.node("section-format").getString();
        this.sectionFormatComplete = rootNode.node("section-format-complete").getString();
        this.packSection = rootNode.node("pack-section").getString();
        this.sectionFormatPlayer = rootNode.node("section-format-player").getString();

    }

    @Override
    protected void preLoaderBuild() {
        //No custom type serializer to register
    }

    public String packSection() {
        return packSection;
    }

    public String sectionFormatPlayer() {
        return sectionFormatPlayer;
    }

    public String sectionFormat() {
        return sectionFormat;
    }

    public String sectionFormatComplete() {
        return sectionFormatComplete;
    }

    public String prefix() {
        return prefix;
    }

    public String reload() {
        return reload;
    }

    public String noCard() {
        return noCard;
    }

    public String noPlayer() {
        return noPlayer;
    }

    public String noCmd() {
        return noCmd;
    }

    public String noEntity() {
        return noEntity;
    }

    public String noCreative() {
        return noCreative;
    }

    public String noRarity() {
        return noRarity;
    }

    public String noBoosterPack() {
        return noBoosterPack;
    }

    public String scheduledGiveaway() {
        return scheduledGiveaway;
    }

    public String giveaway() {
        return giveaway;
    }

    public String giveawayNatural() {
        return giveawayNatural;
    }

    public String giveawayNaturalBoss() {
        return giveawayNaturalBoss;
    }

    public String giveawayNaturalHostile() {
        return giveawayNaturalHostile;
    }

    public String giveawayNaturalPassive() {
        return giveawayNaturalPassive;
    }

    public String giveawayNaturalNeutral() {
        return giveawayNaturalNeutral;
    }

    public String giveawayNaturalBossNoPlayer() {
        return giveawayNaturalBossNoPlayer;
    }

    public String giveawayNaturalPassiveNoPlayer() {
        return giveawayNaturalPassiveNoPlayer;
    }

    public String giveawayNaturalHostileNoPlayer() {
        return giveawayNaturalHostileNoPlayer;
    }

    public String giveawayNaturalNeutralNoPlayer() {
        return giveawayNaturalNeutralNoPlayer;
    }

    public String giveawayNaturalNoPlayer() {
        return giveawayNaturalNoPlayer;
    }

    public String giveRandomCard() {
        return giveRandomCard;
    }

    public String giveRandomCardMsg() {
        return giveRandomCardMsg;
    }

    public String boosterPackMsg() {
        return boosterPackMsg;
    }

    public String openBoosterPack() {
        return openBoosterPack;
    }

    public String listError() {
        return listError;
    }

    public String canBuy() {
        return canBuy;
    }

    public String canNotBuy() {
        return canNotBuy;
    }

    public String canSell() {
        return canSell;
    }

    public String canNotSell() {
        return canNotSell;
    }

    public String chooseCard() {
        return chooseCard;
    }

    public String chooseRarity() {
        return chooseRarity;
    }

    public String choosePack() {
        return choosePack;
    }

    public String cannotBeBought() {
        return cannotBeBought;
    }

    public String notEnoughMoney() {
        return notEnoughMoney;
    }

    public String boughtCard() {
        return boughtCard;
    }

    public String notACard() {
        return notACard;
    }

    public String cardDoesntExist() {
        return cardDoesntExist;
    }

    public String packDoesntExist() {
        return packDoesntExist;
    }

    public String noVault() {
        return noVault;
    }

    public String deckCreativeError() {
        return deckCreativeError;
    }

    public String giveDeck() {
        return giveDeck;
    }

    public String alreadyHaveDeck() {
        return alreadyHaveDeck;
    }

    public String maxDecks() {
        return maxDecks;
    }

    public String createNoName() {
        return createNoName;
    }

    public String createExists() {
        return createExists;
    }

    public String createSuccess() {
        return createSuccess;
    }

    public String timerMessage() {
        return timerMessage;
    }

    public String toggleEnabled() {
        return toggleEnabled;
    }

    public String toggleDisabled() {
        return toggleDisabled;
    }

    public String resolveMsg() {
        return resolveMsg;
    }

    public String resolveError() {
        return resolveError;
    }

    public String rewardError() {
        return rewardError;
    }

    public String rewardError2() {
        return rewardError2;
    }

    public String rewardError3() {
        return rewardError3;
    }

    public String rewardBroadcast() {
        return rewardBroadcast;
    }

    public String rewardDisabled() {
        return rewardDisabled;
    }
}
