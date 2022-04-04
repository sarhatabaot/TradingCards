package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;

public class MessagesConfig extends SimpleConfigurate {
    private String prefix;
    private Message reload;
    private Message noCard; //p
    private Message noPlayer; //p
    private Message noCmd;
    private Message noEntity; //p
    private Message noCreative; //p
    private Message noRarity; //p
    private Message noBoosterPack;
    private String scheduledGiveaway;
    private String giveaway;
    private String giveawayNatural;
    private String giveawayNaturalBoss;
    private String giveawayNaturalHostile;
    private String giveawayNaturalPassive;
    private String giveawayNaturalNeutral;
    private String giveawayNaturalBossNoPlayer;
    private String giveawayNaturalPassiveNoPlayer;
    private String giveawayNaturalHostileNoPlayer;
    private String giveawayNaturalNeutralNoPlayer;
    private String giveawayNaturalNoPlayer;
    private Message giveRandomCard;
    private Message giveRandomCardMsg;
    private Message boosterPackMsg; //p
    private String openBoosterPack;
    private String listError;
    private Message canBuy; //p
    private Message canNotBuy; //p
    private Message canSell; //p
    private Message canNotSell; //p
    private String chooseCard;
    private String chooseRarity;
    private String choosePack;
    private Message cannotBeBought; //p
    private Message notEnoughMoney; //p
    private Message boughtCard; //p
    private Message notACard; //p
    private Message cardDoesntExist; //p
    private Message packDoesntExist; //p
    private Message noVault; //p
    private Message deckCreativeError; //p
    private Message giveDeck; //p
    private Message giveCard; //p
    private Message givePack; //p
    private Message alreadyHaveDeck; //p
    private String maxDecks;
    private String createNoName;
    private String createExists;
    private String createSuccess;
    private String timerMessage;
    private String toggleEnabled;
    private String toggleDisabled;
    private String resolveMsg;
    private String resolveError;
    private String rewardError;
    private String rewardError2;
    private String rewardError3;
    private String rewardBroadcast;
    private String rewardDisabled;

    private String sectionFormat;
    private String sectionFormatPlayer;
    private String sectionFormatComplete;
    private String packSection;

    private String deckInventoryTitle;


    public MessagesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "messages.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.prefix = rootNode.node("prefix").getString("Cards > ");
        this.reload = new Message(true, rootNode.node("reload").getString("Reloaded config."));
        this.noCard = new Message(true, rootNode.node("no-card").getString("No such card exists! make sure to use the exact card name!"));
        this.noPlayer = new Message(true, rootNode.node("no-player").getString("That player does not exist!"));
        this.noCmd = new Message(true, rootNode.node("no-cmd").getString("Invalid command!"));
        this.noEntity = new Message(true, rootNode.node("no-entity").getString("that entity/mob does not exist!"));
        this.noCreative = new Message(true, rootNode.node("no-creative").getString("you cannot open booster packs in creative!"));
        this.noRarity = new Message(true, rootNode.node("no-rarity").getString("that rarity does not exist!"));
        this.noBoosterPack = new Message(true, rootNode.node("no-booster-pack").getString("that booster pack does not exist!"));
        this.scheduledGiveaway = rootNode.node("scheduled-giveaway").getString();
        this.giveaway = rootNode.node("giveaway").getString();
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
        this.giveRandomCard = new Message(true, rootNode.node("give-random-card").getString());
        this.giveRandomCardMsg = new Message(true, rootNode.node("give-random-card-msg").getString());
        this.giveCard = new Message(true, rootNode.node("give-card").getString());
        this.boosterPackMsg = new Message(true, rootNode.node("booster-pack-msg").getString());
        this.openBoosterPack = rootNode.node("open-booster-pack").getString();
        this.listError = rootNode.node("list-error").getString();
        this.canBuy = new Message(true, rootNode.node("can-buy").getString());
        this.canNotBuy = new Message(true, rootNode.node("cannot-buy").getString());
        this.canSell = new Message(true, rootNode.node("can-sell").getString());
        this.canNotSell = new Message(true, rootNode.node("cannot-sell").getString());
        this.chooseCard = rootNode.node("choose-card").getString();
        this.choosePack = rootNode.node("choose-pack").getString();
        this.chooseRarity = rootNode.node("choose-rarity").getString();
        this.cannotBeBought = new Message(true, rootNode.node("cannot-be-bought").getString());
        this.notEnoughMoney = new Message(true, rootNode.node("not-enough-money").getString());
        this.boughtCard = new Message(true, rootNode.node("bought-card").getString());
        this.notACard =new Message(true, rootNode.node("not-a-card").getString());
        this.cardDoesntExist = new Message(true, rootNode.node("card-doesnt-exist").getString());
        this.packDoesntExist = new Message(true, rootNode.node("pack-doesnt-exist").getString());
        this.noVault = new Message(true, rootNode.node("no-vault").getString());
        this.deckCreativeError = new Message(true, rootNode.node("deck-creative-error").getString());
        this.giveDeck = new Message(true, rootNode.node("give-deck").getString());
        this.alreadyHaveDeck = new Message(true, rootNode.node("already-have-deck").getString());
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

        this.deckInventoryTitle = rootNode.node("deck-inventory-title").getString();
        this.givePack = new Message(true, rootNode.node("give-booster-pack-msg").getString());
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
        return reload.getFormatted();
    }

    public String noCard() {
        return noCard.getFormatted();
    }

    public String noPlayer() {
        return noPlayer.getFormatted();
    }

    public String noCmd() {
        return noCmd.getFormatted();
    }

    public String noEntity() {
        return noEntity.getFormatted();
    }

    public String noCreative() {
        return noCreative.getFormatted();
    }

    public String noRarity() {
        return noRarity.getFormatted();
    }

    public String noBoosterPack() {
        return noBoosterPack.getFormatted();
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
        return giveRandomCard.getFormatted();
    }

    public String giveRandomCardMsg() {
        return giveRandomCardMsg.getFormatted();
    }

    public String boosterPackMsg() {
        return boosterPackMsg.getFormatted();
    }

    public String openBoosterPack() {
        return openBoosterPack;
    }

    public String listError() {
        return listError;
    }

    public String canBuy() {
        return canBuy.getFormatted();
    }

    public String canNotBuy() {
        return canNotBuy.getFormatted();
    }

    public String canSell() {
        return canSell.getFormatted();
    }

    public String canNotSell() {
        return canNotSell.getFormatted();
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
        return cannotBeBought.getFormatted();
    }

    public String notEnoughMoney() {
        return notEnoughMoney.getFormatted();
    }

    public String boughtCard() {
        return boughtCard.getFormatted();
    }

    public String notACard() {
        return notACard.getFormatted();
    }

    public String cardDoesntExist() {
        return cardDoesntExist.getFormatted();
    }

    public String packDoesntExist() {
        return packDoesntExist.getFormatted();
    }

    public String noVault() {
        return noVault.getFormatted();
    }

    public String deckCreativeError() {
        return deckCreativeError.getFormatted();
    }

    public String giveDeck() {
        return giveDeck.getFormatted();
    }

    public String alreadyHaveDeck() {
        return alreadyHaveDeck.getFormatted();
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

    public String giveCard(){
        return giveCard.getFormatted();
    }

    public String deckInventoryTitle(){
        return deckInventoryTitle;
    }

    public String givePack() {
        return givePack.getFormatted();
    }

    public class Message {
        private final boolean prefixed;
        private final String raw;
        private final String formatted;

        public Message(boolean prefixed, final String raw) {
            this.prefixed = prefixed;
            this.raw = raw;
            this.formatted = ChatUtil.color((prefixed) ? prefix + raw : raw);
        }

        @Override
        public String toString() {
            return "Message{" +
                    "prefixed=" + prefixed +
                    ", raw='" + raw + '\'' +
                    ", formatted='" + formatted + '\'' +
                    '}';
        }

        public String getFormatted() {
            return formatted;
        }

        public String getRaw() {
            return raw;
        }
    }
}
