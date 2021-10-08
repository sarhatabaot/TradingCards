package net.tinetwork.tradingcards.api.config.settiings;

import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurateException;

import java.util.List;

/**
 * @author sarhatabaot
 */
public abstract class GeneralConfigurate extends SimpleConfigurate {
    public GeneralConfigurate(final TradingCardsPlugin<? extends Card<?>> plugin, final String resourcePath, final String fileName, final String folder) throws ConfigurateException {
        super(plugin, resourcePath, fileName, folder);
    }


    public abstract ItemStack blankCard();

    public abstract ItemStack blankBoosterPack();

    public abstract ItemStack blankDeck();

    public abstract Material deckMaterial();

    public abstract String deckPrefix() ;

    public abstract boolean dropDeckItems();

    public abstract String playerOpRarity();

    public abstract String playerSeries();

    public abstract String playerType();

    public abstract boolean playerHasShinyVersion();

    public abstract boolean allowRewards();

    public abstract boolean rewardBroadcast();

    public abstract boolean eatShinyCards();

    public abstract boolean playerDropsCard();

    public abstract int playerDropsCardRarity();

    public abstract boolean vaultEnabled();

    public abstract boolean closedEconomy();

    public abstract String serverAccount();

    public abstract boolean spawnerBlock();

    public abstract String spawnerMobName();

    public abstract int infoLineLength();

    public abstract List<String> activeSeries();

    public abstract boolean deckInCreative();

    public abstract boolean useDeckItem();
    public abstract boolean useLargeDecks();

    public abstract boolean debugMode();

    public abstract String cardPrefix();

    public abstract String shinyName();

    public abstract Material cardMaterial();

    public abstract Material packMaterial();

    public abstract String getColorSeries();

    public abstract String colorType();

    public abstract String colorInfo();

    public abstract String colorAbout();

    public abstract String colorRarity();

    public abstract String colorPackName();

    public abstract String colorPackLore();

    public abstract String colorPackNormal();

    public abstract String colorPackSpecial();

    public abstract String colorPackExtra();

    public abstract String colorListHaveCard();

    public abstract String colorListHaveCardShiny();

    public abstract String colorRarityCompleted();

    public abstract String packPrefix();

    public abstract String colorSeries();

    public abstract String displayTitle();

    public abstract String displayShinyTitle();

    public abstract String displaySeries();

    public abstract String displayType() ;

    public abstract String displayInfo();

    public abstract String displayAbout();

    public abstract boolean useDefaultCardsFile();
}
