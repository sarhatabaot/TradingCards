package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.card;


import com.github.sarhatabaot.tradingcards.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.SimpleCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.LoggerUtil;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * @author sarhatabaot
 */
public abstract class EditCardConfig<R> {
    private final CommentedConfigurationNode rootNode;
    private final ConfigurationNode cardsNode;
    private final YamlConfigurationLoader loader;
    private final SimpleCardsConfig simpleCardsConfig;

    protected EditCardConfig(final CommentedConfigurationNode rootNode, final ConfigurationNode cardsNode, final YamlConfigurationLoader loader, final SimpleCardsConfig simpleCardsConfig) {
        this.rootNode = rootNode;
        this.cardsNode = cardsNode;
        this.loader = loader;
        this.simpleCardsConfig = simpleCardsConfig;
    }

    public void updateValue(final String rarityId, final String cardId, final String seriesId, final R value) {
        ConfigurationNode rarityNode = cardsNode.node(rarityId);
        ConfigurationNode cardNode = rarityNode.node(cardId);
        try {
            TradingCard card = cardNode.get(TradingCard.class);
            if (card == null) {
                throw new ConfigurateException();
            }
            onUpdate(card, value);
            cardNode.set(card);
            loader.save(rootNode);
            simpleCardsConfig.reloadConfig();
        } catch (ConfigurateException e) {
            LoggerUtil.logSevereException(e);
        }
    }

    protected abstract void onUpdate(TradingCard card, R value);
}
