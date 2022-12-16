package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import de.tr7zw.nbtapi.NBTEntity;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingRarityManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.AllCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeCardKey;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.denylist.PlayerDenylist;
import net.tinetwork.tradingcards.tradingcardsplugin.denylist.WorldDenylist;
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

public class DropListener extends SimpleListener {
    private final PlayerDenylist playerBlacklist;
    private final WorldDenylist worldBlacklist;
    private final AllCardManager cardManager;

    public DropListener(final TradingCards plugin) {
        super(plugin);
        this.playerBlacklist = plugin.getPlayerBlacklist();
        this.worldBlacklist = plugin.getWorldBlacklist();
        this.cardManager = plugin.getCardManager();
    }


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

        ItemStack playerCard = cardManager.getActiveCard(killedPlayer.getName(), rarityKey, plugin.getGeneralConfig().playerSeries()).build(false);
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

        NBTEntity nbtEntity = new NBTEntity(killedEntity);
        if(nbtEntity.getPersistentDataContainer().hasTag(NbtUtils.TC_COMPOUND) && nbtEntity.getPersistentDataContainer().getCompound(NbtUtils.TC_COMPOUND).hasTag(NbtUtils.TC_SPAWNER_MOB)) {
            debug("Entity %s is marked as a spawner entity, not dropping card.".formatted(killedEntity.getType()));
            return;
        }

        //Get card rarity
        debug(InternalDebug.DropListener.ENTITY_TYPE.formatted(killedEntity.getType()));
        debug(InternalDebug.DropListener.MOB_TYPE.formatted(CardUtil.getMobType(killedEntity.getType())));

        String rarityName = cardManager.getRandomRarityId(CardUtil.getMobType(killedEntity.getType()), false);
        if (rarityName.equalsIgnoreCase(TradingRarityManager.EMPTY_RARITY.getId()))
            return;

        //Get the card
        TradingCard randomCard = plugin.getCardManager().getRandomActiveCardByRarity(rarityName);
        if (randomCard instanceof EmptyCard) {
            return;
        }

        boolean isShiny = randomCard.hasShiny() && CardUtil.calculateIfShiny(false);
        debug(InternalDebug.DropListener.ADDED_CARD.formatted(CompositeCardKey.fromCard(randomCard)));
        //Add the card to the killedEntity drops
        e.getDrops().add(randomCard.build(isShiny));
    }

    //Gets the rarity key for the appropriate player card.
    private @Nullable String getRarityKey(Player player) {
        List<Rarity> rarities = plugin.getRarityManager().getRarities();
        if (rarities == null)
            return null;


        for (final Rarity rarity : rarities) {
            if (!(cardManager.getCard(player.getName(), rarity.getId(), plugin.getGeneralConfig().playerSeries()) instanceof EmptyCard)) {
                debug(rarity.getId());
                return rarity.getId();
            }
        }

        debug(InternalDebug.DropListener.NULL_RARITY_KEY);
        return null;
    }
}
