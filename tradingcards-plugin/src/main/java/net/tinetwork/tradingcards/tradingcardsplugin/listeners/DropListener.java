package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        ItemStack playerCard = cardManager.getActiveCard(killedPlayer.getName(), rarityKey).build(false);
        e.getDrops().add(playerCard);
        debug(e.getDrops().toString());
    }

    @EventHandler
    public void onEntityDeath(@NotNull EntityDeathEvent e) {
        final LivingEntity killedEntity = e.getEntity();
        final Player killer = killedEntity.getKiller();
        final World world = killedEntity.getWorld();

        //Do Validations
        if (killer == null) return;
        if (!this.playerBlacklist.isAllowed(killer)) return;
        if (!this.worldBlacklist.isAllowed(world)) return;
        //Get card rarity
        debug(InternalDebug.DropListener.ENTITY_TYPE.formatted(killedEntity.getType()));
        debug(InternalDebug.DropListener.MOB_TYPE.formatted(CardUtil.getMobType(killedEntity.getType())));

        String rarityName = cardManager.getRandomRarity(CardUtil.getMobType(killedEntity.getType()), false);
        if (rarityName.equalsIgnoreCase("None"))
            return;

        //Get the card
        TradingCard randomCard = plugin.getCardManager().getRandomActiveCardByRarity(rarityName);
        if (randomCard instanceof EmptyCard) {
            return;
        }

        boolean isShiny = randomCard.hasShiny() && CardUtil.calculateIfShiny(false);
        debug(InternalDebug.DropListener.ADDED_CARD.formatted(cardKey(randomCard.getRarity().getId(),randomCard.getCardId())));
        //Add the card to the killedEntity drops
        e.getDrops().add(randomCard.build(isShiny));
    }

    //Gets the rarity key for the appropriate player card.
    private @Nullable String getRarityKey(Player player) {
        List<Rarity> rarities = plugin.getRarityManager().getRarities();
        if (rarities == null)
            return null;


        for (final Rarity rarity : rarities) {
            if (!(cardManager.getCard(player.getName(), rarity.getId(), false) instanceof EmptyCard)) {
                debug(rarity.getId());
                return rarity.getId();
            }
        }

        debug(InternalDebug.DropListener.NULL_RARITY_KEY);
        return null;
    }
}
