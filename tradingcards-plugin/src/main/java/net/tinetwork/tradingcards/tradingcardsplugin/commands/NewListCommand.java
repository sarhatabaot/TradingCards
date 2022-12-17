package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.github.sarhatabaot.kraken.core.logging.LoggerUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

/**
 * @author sarhatabaot
 */
/*
 This will be an "admin" command.
 It will just display the cards in every rarity, series, rarity+series etc
 You'll be able to click on the cards if you have that permission to view more info on the card.

 The old list functionality will be deprecated, and instead we will introduce a new feature, the "collection book".
 */
@CommandAlias("cards nlist")
public class NewListCommand extends BaseCommand {
    private final TradingCards plugin;
    private final LoadingCache<String, Integer> pages = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<>() {
                        @Override
                        public @NotNull Integer load(final @NotNull String key) throws Exception {
                            return calculatePageAmount(key);
                        }
                    }
            );

    public NewListCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("rarity")
    public void onRarity(final CommandSender sender, final String rarityId, @Default("1") final Integer page) {
        int pageAmount = getPageAmount(rarityId);

        if(page > pageAmount) {
            //No such page, page amount: %d
            return;
        }


    }

    private int getPageAmount(final String rarityId) {
        try {
            return pages.get(rarityId);
        } catch (ExecutionException e) {
            LoggerUtil.logSevereException(e);
            return  1;
        }
    }

    private int calculatePageAmount(final String rarityId) {
        int cardAmount = this.plugin.getStorage().getCardsInRarityCount(rarityId);
        if (cardAmount < 19)
            return 1;
        return this.plugin.getStorage().getCardsInRarityCount(rarityId) / 19;
    }
}
