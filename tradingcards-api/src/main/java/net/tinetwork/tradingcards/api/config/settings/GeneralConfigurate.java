package net.tinetwork.tradingcards.api.config.settings;

import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.api.TradingCardsPlugin;
import net.tinetwork.tradingcards.api.card.Card;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public abstract class GeneralConfigurate extends YamlConfigurateFile<TradingCardsPlugin<?>> {
    protected GeneralConfigurate(final TradingCardsPlugin<? extends Card<?>> plugin, final String resourcePath, final String fileName, final String folder) throws ConfigurateException {
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

    public abstract int deckCustomModelData();
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

    public abstract boolean deckInCreative();

    public abstract boolean useDeckItem();

    public abstract boolean debugMode();

    public abstract String cardPrefix();

    public abstract String shinyName();

    public abstract Material cardMaterial();

    public abstract Material packMaterial();

    public abstract String colorPackName();

    public abstract String colorPackLore();

    public abstract String colorPackNormal();

    public abstract String colorListHaveCard();

    public abstract String colorListHaveCardShiny();

    public abstract String colorRarityCompleted();

    public abstract String packPrefix();


    public abstract String displayTitle();

    public abstract String displayShinyTitle();

    public abstract String displaySeries();

    public abstract String displayType() ;

    public abstract String displayInfo();

    public abstract String displayAbout();

    public abstract boolean useDefaultCardsFile();

    public abstract int deckRows();

    public abstract boolean treasuryEnabled();

    public abstract boolean collectorBookEnabled();
}
