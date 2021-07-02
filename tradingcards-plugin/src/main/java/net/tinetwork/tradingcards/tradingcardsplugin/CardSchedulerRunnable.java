package net.tinetwork.tradingcards.tradingcardsplugin;

import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;


public class CardSchedulerRunnable extends BukkitRunnable {
    private final TradingCards plugin;
    private final TradingCardManager cardManager;

    public CardSchedulerRunnable(final TradingCards plugin) {
        this.plugin = plugin;
        this.cardManager =  plugin.getCardManager();
    }

    @Override
    public void run() {
        plugin.debug(getClass().getSimpleName() + " task running");
        //check this before the task is registered.
        if (!plugin.getConfig().getBoolean("General.Schedule-Cards"))
            return;

        if (plugin.getConfig().getBoolean("General.Schedule-Cards-Natural")) {
            String mob = plugin.getConfig().getString("General.Schedule-Card-Mob");
            if (plugin.isMob(mob.toUpperCase())) {
                CardUtil.giveawayNatural(EntityType.valueOf(mob.toUpperCase()), null);
                return;
            }
            plugin.getLogger().info("Error! schedule-card-mob is an invalid mob?");
            return;
        }

        String keyToUse = "";

        for (final String key : plugin.getCardManager().getRarityNames()) {
            plugin.debug("Rarity key: " + key);
            if (key.equalsIgnoreCase(plugin.getConfig().getString("General.Schedule-Card-Rarity"))) {
                keyToUse = key;
            }
        }
        plugin.debug("keyToUse: " + keyToUse);
        if (keyToUse.isEmpty())
            return;

        Bukkit.broadcastMessage(plugin.cMsg(plugin.getMessagesConfig().prefix + " " + plugin.getMessagesConfig().scheduledGiveaway));
        for (final Player p : Bukkit.getOnlinePlayers()) {
            String cardName = getRandomCardName(keyToUse);
            CardUtil.dropItem(p, cardManager.getCard(cardName, keyToUse, false).build());
        }

    }
    private String getRandomCardName(final String rarity) {
        var rIndex = plugin.getRandom().nextInt(plugin.getCardManager().getRarityCardList(rarity).size());
        var i = 0;
        var cardName = "";
        for (Iterator<String> var11 = plugin.getCardManager().getRarityCardList(rarity).iterator(); var11.hasNext(); ++i) {
            String theCardName = var11.next();
            if (i == rIndex) {
                return theCardName;
            }
        }
        return cardName;
    }


}