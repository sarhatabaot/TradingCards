package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.github.sarhatabaot.kraken.core.config.Transformation;
import com.github.sarhatabaot.kraken.core.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.config.transformations.MessagesTransformations;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.settings.Messages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;

public class MessagesConfig extends YamlConfigurateFile<TradingCards> {
    private String prefix;
    private Message reload;
    private Message noCard;
    private Message noPlayer;
    private Message noCmd;
    private Message noEntity;
    private Message noCreative;
    private Message noRarity;
    private Message noBoosterPack;
    private Message noSeries;
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
    private Message boosterPackMsg;
    private String openBoosterPack;
    private String listError;
    private Message canBuy;
    private Message canNotBuy;
    private Message canSell;
    private Message canNotSell;
    private String chooseCard;
    private String chooseRarity;
    private String choosePack;
    private Message cannotBeBought;
    private Message notEnoughMoney;
    private Message boughtCard;
    private Message notACard;
    private Message cardDoesntExist;
    private Message packDoesntExist;
    private Message noVault;
    private Message deckCreativeError;
    private Message giveDeck;
    private Message giveCard;
    private Message givePack;
    private Message alreadyHaveDeck;
    private String maxDecks;
    private String createNoName;
    private String createExists;
    private String createSuccess;
    private String timerMessage;
    private Message toggleEnabled;
    private Message toggleDisabled;
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

    private Message deckInventoryTitle;


    public MessagesConfig(TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "messages.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.prefix = rootNode.node("prefix").getString(Messages.PREFIX);
        this.reload = new Message(true, rootNode.node("reload").getString(Messages.RELOAD));
        this.noCard = new Message(true, rootNode.node("no-card").getString(Messages.NO_CARD));
        this.noPlayer = new Message(true, rootNode.node("no-player").getString(Messages.NO_PLAYER));
        this.noCmd = new Message(true, rootNode.node("no-cmd").getString(Messages.NO_CMD));
        this.noEntity = new Message(true, rootNode.node("no-entity").getString(Messages.NO_ENTITY));
        this.noCreative = new Message(true, rootNode.node("no-creative").getString(Messages.NO_CREATIVE));
        this.noRarity = new Message(true, rootNode.node("no-rarity").getString(Messages.NO_RARITY));
        this.noBoosterPack = new Message(true, rootNode.node("no-booster-pack").getString(Messages.NO_BOOSTER_PACK));
        this.noSeries = new Message(true,rootNode.node("no-series").getString(Messages.NO_SERIES));
        this.scheduledGiveaway = rootNode.node("scheduled-giveaway").getString(Messages.SCHEDULED_GIVEAWAY);
        this.giveaway = rootNode.node("giveaway").getString(Messages.GIVEAWAY);
        this.giveawayNatural = rootNode.node("giveaway-natural").getString(Messages.GIVEAWAY_NATURAL);
        this.giveawayNaturalBoss = rootNode.node("giveaway-natural-boss").getString(Messages.GIVEAWAY_NATURAL_BOSS);
        this.giveawayNaturalHostile = rootNode.node("giveaway-natural-hostile").getString(Messages.GIVEAWAY_NATURAL_HOSTILE);
        this.giveawayNaturalPassive = rootNode.node("giveaway-natural-passive").getString(Messages.GIVEAWAY_NATURAL_PASSIVE);
        this.giveawayNaturalNeutral = rootNode.node("giveaway-natural-neutral").getString(Messages.GIVEAWAY_NATURAL_NEUTRAL);
        this.giveawayNaturalBossNoPlayer = rootNode.node("giveaway-natural-boss-no-player").getString(Messages.GIVEAWAY_NATURAL_BOSS_NO_PLAYER);
        this.giveawayNaturalPassiveNoPlayer = rootNode.node("giveaway-natural-passive-no-player").getString(Messages.GIVEAWAY_NATURAL_PASSIVE_NO_PLAYER);
        this.giveawayNaturalHostileNoPlayer = rootNode.node("giveaway-natural-hostile-no-player").getString(Messages.GIVEAWAY_NATURAL_HOSTILE_NO_PLAYER);
        this.giveawayNaturalNeutralNoPlayer = rootNode.node("giveaway-natural-neutral-no-player").getString(Messages.GIVEAWAY_NATURAL_NEUTRAL_NO_PLAYER);
        this.giveawayNaturalNoPlayer = rootNode.node("giveaway-natural-no-player").getString(Messages.GIVEAWAY_NATURAL_NO_PLAYER);
        this.giveRandomCard = new Message(true, rootNode.node("give-random-card").getString(Messages.GIVE_RANDOM_CARD));
        this.giveRandomCardMsg = new Message(true, rootNode.node("give-random-card-msg").getString(Messages.GIVE_RANDOM_CARD_MSG));
        this.giveCard = new Message(true, rootNode.node("give-card").getString(Messages.GIVE_CARD));
        this.boosterPackMsg = new Message(true, rootNode.node("booster-pack-msg").getString(Messages.BOOSTER_PACK_MSG));
        this.openBoosterPack = rootNode.node("open-booster-pack").getString(Messages.OPEN_BOOSTER_PACK);
        this.listError = rootNode.node("list-error").getString(Messages.LIST_ERROR);
        this.canBuy = new Message(true, rootNode.node("can-buy").getString(Messages.CAN_BUY));
        this.canNotBuy = new Message(true, rootNode.node("cannot-buy").getString(Messages.CANNOT_BUY));
        this.canSell = new Message(true, rootNode.node("can-sell").getString(Messages.CAN_SELL));
        this.canNotSell = new Message(true, rootNode.node("cannot-sell").getString(Messages.CANNOT_SELL));
        this.chooseCard = rootNode.node("choose-card").getString(Messages.CHOOSE_CARD);
        this.choosePack = rootNode.node("choose-pack").getString(Messages.CHOOSE_PACK);
        this.chooseRarity = rootNode.node("choose-rarity").getString(Messages.CHOOSE_RARITY);
        this.cannotBeBought = new Message(true, rootNode.node("cannot-be-bought").getString(Messages.CANNOT_BE_BOUGHT));
        this.notEnoughMoney = new Message(true, rootNode.node("not-enough-money").getString(Messages.NOT_ENOUGH_MONEY));
        this.boughtCard = new Message(true, rootNode.node("bought-card").getString(Messages.BOUGHT_CARD));
        this.notACard =new Message(true, rootNode.node("not-a-card").getString(Messages.NOT_A_CARD));
        this.cardDoesntExist = new Message(true, rootNode.node("card-doesnt-exist").getString(Messages.CARD_DOESNT_EXIST));
        this.packDoesntExist = new Message(true, rootNode.node("pack-doesnt-exist").getString(Messages.PACK_DOESNT_EXIST));
        this.noVault = new Message(true, rootNode.node("no-vault").getString(Messages.NO_VAULT));
        this.deckCreativeError = new Message(true, rootNode.node("deck-creative-error").getString(Messages.DECK_CREATIVE_ERROR));
        this.giveDeck = new Message(true, rootNode.node("give-deck").getString(Messages.GIVE_DECK));
        this.alreadyHaveDeck = new Message(true, rootNode.node("already-have-deck").getString(Messages.ALREADY_HAVE_DECK));
        this.maxDecks = rootNode.node("max-decks").getString(Messages.MAX_DECKS);
        this.createNoName = rootNode.node("create-no-name").getString(Messages.CREATE_NO_NAME);
        this.createExists = rootNode.node("create-exists").getString(Messages.CREATE_EXISTS);
        this.createSuccess = rootNode.node("create-success").getString(Messages.CREATE_SUCCESS);
        this.timerMessage = rootNode.node("timer-message").getString(Messages.TIMER_MESSAGE);
        this.toggleEnabled = new Message(true, rootNode.node("toggle-enabled").getString(Messages.TOGGLE_ENABLED));
        this.toggleDisabled = new Message(true, rootNode.node("toggle-disabled").getString(Messages.TOGGLE_DISABLED));
        this.resolveMsg = rootNode.node("resolve-msg").getString(Messages.RESOLVE_MSG);
        this.resolveError = rootNode.node("resolve-error").getString(Messages.RESOLVE_ERROR);
        this.rewardError = rootNode.node("reward-error").getString(Messages.REWARD_ERROR);
        this.rewardError2 = rootNode.node("reward-error2").getString(Messages.REWARD_ERROR2);
        this.rewardError3 = rootNode.node("reward-error3").getString(Messages.REWARD_ERROR3);
        this.rewardBroadcast = rootNode.node("reward-broadcast").getString(Messages.REWARD_BROADCAST);
        this.rewardDisabled = rootNode.node("reward-disabled").getString(Messages.REWARD_DISABLED);
        this.sectionFormat = rootNode.node("section-format").getString(Messages.SECTION_FORMAT);
        this.sectionFormatComplete = rootNode.node("section-format-complete").getString(Messages.SECTION_FORMAT_COMPLETE);
        this.packSection = rootNode.node("pack-section").getString(Messages.PACK_SECTION);
        this.sectionFormatPlayer = rootNode.node("section-format-player").getString(Messages.SECTION_FORMAT_PLAYER);

        this.deckInventoryTitle = new Message(false, rootNode.node("deck-inventory-title").getString(Messages.DECK_INVENTORY_TITLE));
        this.givePack = new Message(true, rootNode.node("give-booster-pack-msg").getString(Messages.GIVE_BOOSTER_PACK_MSG));
    }

    @Override
    protected void builderOptions() {
        //No custom type serializer to register
    }

    @Override
    protected Transformation getTransformation() {
        return new MessagesTransformations();
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

    public String noSeries() {
        return noSeries.getFormatted();
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
        return toggleEnabled.getFormatted();
    }

    public String toggleDisabled() {
        return toggleDisabled.getFormatted();
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
        return deckInventoryTitle.getFormatted();
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
