package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards collection")
public class CollectionBookCommand extends BaseCommand {
    private final TradingCards plugin;
    
    public CollectionBookCommand(TradingCards plugin) {
        this.plugin = plugin;
    }
    
    /*
      https://github.com/sarhatabaot/TradingCards/issues/230
      https://github.com/sarhatabaot/TradingCards/issues/97
      
      Will implement issue 230.
      
      By default, you will need 1 of every card to "complete" that card.
      You will also be able to unlock a reward (configurable) for every rarity / series complete.
     */
    
    @Default
    public void onDefault() {
    
    }
    
    //TODO, also add a recipe for this.
    @Subcommand("book")
    @Description("Obtain a book for easy access of the collection book. ")
    public void onBook() {
    
    }
}
