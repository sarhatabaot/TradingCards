package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.towny;


import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TownyListener implements Listener {
    private final TradingCards tradingCards;
    private final TownyConfig townyConfig;

    public TownyListener(TradingCards tradingCards, TownyConfig townyConfig) {
        this.tradingCards = tradingCards;
        this.townyConfig = townyConfig;
    }

    @EventHandler
    public void onNewTown(final NewTownEvent e) {
        if (!townyConfig.enabled()) {
            return;
        }

        final String townRarity = townyConfig.townRarity();
        final String townType = townyConfig.townType();
        final String townSeries = townyConfig.townSeries();
        final String townName = e.getTown().getName();
        final boolean hasShiny = townyConfig.hasShiny();

        final Storage<TradingCard> storage = tradingCards.getStorage();
        final DropType type = tradingCards.getDropTypeManager().getType(townType);
        if (storage.getCard(townName, townRarity, townSeries) == null) {
            storage.createCard(townName, townRarity, townSeries);
            storage.editCardDisplayName(townRarity, townName, townSeries, e.getTown().getFormattedName());
            storage.editCardHasShiny(townRarity, townName, townSeries, hasShiny);
            storage.editCardType(townRarity, townName, townSeries, type);
            storage.editCardInfo(townRarity,townName,townSeries, getCalendarInfo());
        }
    }

    @EventHandler
    public void onNewNation(final NewNationEvent e) {
        if (!townyConfig.enabled()) {
            return;
        }

        final String nationRarity = townyConfig.nationRarity();
        final String nationType = townyConfig.nationType();
        final String nationSeries = townyConfig.nationSeries();
        final String nationName = e.getNation().getName();
        final boolean hasShiny = townyConfig.hasShiny();

        final Storage<TradingCard> storage = tradingCards.getStorage();
        final DropType type = tradingCards.getDropTypeManager().getType(nationType);
        if (storage.getCard(nationName, nationRarity, nationSeries) == null) {
            storage.createCard(nationName, nationRarity, nationSeries);
            storage.editCardDisplayName(nationRarity, nationName, nationSeries, e.getNation().getFormattedName());
            storage.editCardHasShiny(nationRarity, nationName, nationSeries, hasShiny);
            storage.editCardType(nationRarity, nationName, nationSeries, type);
            storage.editCardInfo(nationRarity, nationName, nationSeries, getCalendarInfo());
        }

    }

    private String getCalendarInfo() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(System.currentTimeMillis());
        int date = gc.get(Calendar.DATE);
        int month = gc.get(Calendar.MONTH) + 1;
        int year = gc.get(Calendar.YEAR);

        if (townyConfig.calendarMode().equalsIgnoreCase("american")) {
            return "Created %d/%d/%d".formatted(month,date,year);
        }

        return "Created %d/%d/%d".formatted(date,month,year);
    }
}
