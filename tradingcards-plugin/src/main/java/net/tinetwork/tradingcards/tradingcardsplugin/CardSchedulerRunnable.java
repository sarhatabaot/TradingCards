package net.tinetwork.tradingcards.tradingcardsplugin;

import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


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
        if (!plugin.getGeneralConfig().scheduleCards())
            return;

        if (plugin.getGeneralConfig().scheduleCardsNatural()) {
            String mob = plugin.getGeneralConfig().scheduleCardMob();
            if (plugin.isMob(mob.toUpperCase())) {
                CardUtil.giveawayNatural(EntityType.valueOf(mob.toUpperCase()), null);
                return;
            }
            plugin.getLogger().info("Error! schedule-card-mob is an invalid mob?");
            return;
        }

        final String rarity = getRarity();

        plugin.debug("keyToUse: " + rarity);
        if (rarity.isEmpty())
            return;

        Bukkit.broadcastMessage(plugin.cMsg(plugin.getMessagesConfig().prefix() + " " + plugin.getMessagesConfig().scheduledGiveaway()));
        for (final Player p : Bukkit.getOnlinePlayers()) {
            CardUtil.dropItem(p, cardManager.getRandomCard(rarity, false).build());
        }

    }

    private String getRarity() {
        for (final String key : plugin.getCardManager().getRarityNames()) {
            if (key.equalsIgnoreCase(plugin.getGeneralConfig().scheduleCardRarity())) {
               return key;
            }
        }
        return "";
    }


}