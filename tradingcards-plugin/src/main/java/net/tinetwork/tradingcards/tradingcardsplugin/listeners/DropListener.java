package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.PlayerBlacklist;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.WorldBlacklist;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil.cardKey;

public class DropListener extends SimpleListener {
    private final PlayerBlacklist playerBlacklist;
    private final WorldBlacklist worldBlacklist;
    private final TradingCardManager cardManager;

    public DropListener(final TradingCards plugin) {
        super(plugin);
        this.playerBlacklist = plugin.getPlayerBlacklist();
        this.worldBlacklist = plugin.getWorldBlacklist();
        this.cardManager = plugin.getCardManager();
    }


    //When a player is killed, he can drop a card
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        boolean canPlayerDropCards = plugin.getGeneralConfig().playerDropsCard();
        int playerCardDropRarity = plugin.getGeneralConfig().playerDropsCardRarity();

        if (!canPlayerDropCards)
            return;

        final Player killedPlayer = e.getEntity();
        final Player killer = killedPlayer.getKiller();

        if (killer == null)
            return;

        if (plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1 > playerCardDropRarity)
            return;

        String rarityKey = getRarityKey(killedPlayer);
        if (rarityKey == null)
            return;

        ItemStack playerCard = cardManager.getActiveCard(killedPlayer.getName(), rarityKey, false).build();
        e.getDrops().add(playerCard);
        debug(e.getDrops().toString());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        final LivingEntity killedEntity = e.getEntity();
        final Player killer = killedEntity.getKiller();
        final World world = killedEntity.getWorld();

        //Do Validations
        if (killer == null) return;
        if (!this.playerBlacklist.isAllowed(killer)) return;
        if (!this.worldBlacklist.isAllowed(world)) return;
        //Get card rarity
        debug("EntityType=" + killedEntity.getType());
        debug("MobType=" + CardUtil.getMobType(killedEntity.getType()));

        String rarityName = cardManager.getRandomRarity(CardUtil.getMobType(killedEntity.getType()), false);
        if (rarityName.equals("None"))
            return;

        //Get the card
        TradingCard randomCard = plugin.getCardManager().getRandomActiveCard(rarityName, false);
        if (randomCard instanceof EmptyCard) {
            return;
        }

        debug("Added card "+cardKey(randomCard.getRarity().getName(),randomCard.getCardName()));
        //Add the card to the killedEntity drops
        e.getDrops().add(randomCard.build());
    }

    //Gets the rarity key for the appropriate player card.
    private String getRarityKey(Player player) {
        List<Rarity> rarities = plugin.getRaritiesConfig().rarities();
        if (rarities == null)
            return null;


        for (final Rarity rarity : rarities) {
            if (!(cardManager.getCard(player.getName(), rarity.getName(), false) instanceof EmptyCard)) {
                debug(rarity.getName());
                return rarity.getName();
            }
        }

        debug("rarityKey is null");
        return null;
    }
}
