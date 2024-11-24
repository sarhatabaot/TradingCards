package net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mobarena;


import com.garbagemule.MobArena.events.ArenaKillEvent;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.impl.TradingRarityManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MobArenaListener implements Listener {
    private final TradingCards tradingCards;
    private final MobArenaConfig mobArenaConfig;

    public MobArenaListener(TradingCards tradingCards, MobArenaConfig mobArenaConfig) {
        this.tradingCards = tradingCards;
        this.mobArenaConfig = mobArenaConfig;
    }

    @EventHandler
    public void onArenaKill(ArenaKillEvent event) {
        if (!mobArenaConfig.enabled() || mobArenaConfig.disableInArena() || event.getPlayer() == null) {
            return;
        }


        final Player player = event.getPlayer();
        if (!canDropPlayer(player) || !canDropWorld(player.getWorld())) {
            return;
        }

        DropType dropType = tradingCards.getDropTypeManager().getMobType(event.getVictim().getType());
        String rarity = tradingCards.getCardManager().getRandomRarityId(dropType);
        if (rarity.equalsIgnoreCase(TradingRarityManager.EMPTY_RARITY.getId())) {
            return;
        }

        CardUtil.dropItem(event.getPlayer(), getRandomDrop(rarity));
    }

    private ItemStack getRandomDrop(final String rarity) {
        if (mobArenaConfig.useActiveSeries())
            return tradingCards.getCardManager().getRandomActiveCardByRarity(rarity).build(false);
        return tradingCards.getCardManager().getRandomCardByRarity(rarity).build(false);
    }

    private boolean canDropPlayer(Player player) {
        return tradingCards.getPlayerDenylist().isAllowed(player);
    }

    private boolean canDropWorld(World world) {
        return tradingCards.getWorldDenylist().isAllowed(world);
    }
}
