package net.tinetwork.tradingcards.tradingcardsplugin.listeners;


import de.tr7zw.changeme.nbtapi.NBT;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.DropPoolsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.drop.DropPoolEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.drop.DropPoolEntryType;
import net.tinetwork.tradingcards.tradingcardsplugin.drop.MobDropPool;
import net.tinetwork.tradingcards.tradingcardsplugin.hooks.impl.mythicmobs.MythicMobsUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.impl.TradingRarityManager;
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
    private final DropPoolsConfig dropPoolsConfig;

    public DropListener(final TradingCards plugin) {
        super(plugin);
        this.playerBlacklist = plugin.getPlayerDenylist();
        this.worldBlacklist = plugin.getWorldDenylist();
        this.cardManager = plugin.getCardManager();
        this.dropPoolsConfig = plugin.getDropPoolsConfig();
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
        if (plugin.getGeneralConfig().collectorBookEnabled()) {
            CardUtil.dropItem(killer, playerCard);
        } else {
            e.getDrops().add(playerCard);
        }
        debug(e.getDrops().toString());
    }

    @EventHandler
    public void onEntityDeath(@NotNull EntityDeathEvent entityDeathEvent) {
        final LivingEntity killedEntity = entityDeathEvent.getEntity();
        final Player killer = killedEntity.getKiller();
        final World world = killedEntity.getWorld();

        //Do Validations
        if (killer == null) return;
        if (!this.playerBlacklist.isAllowed(killer)) return;
        if (!this.worldBlacklist.isAllowed(world)) return;
        final boolean isMythicMob = MythicMobsUtil.isMythicMob(killedEntity);

        if (NBT.get(killedEntity,nbt -> nbt.hasTag(NbtUtils.TC_COMPOUND) && nbt.getCompound(NbtUtils.TC_COMPOUND).hasTag(NbtUtils.TC_SPAWNER_MOB))) {
            debug("Entity %s is marked as a spawner entity, not dropping card.".formatted(killedEntity.getType()));
            return;
        }

        //Get card rarity
        debug(InternalDebug.DropListener.ENTITY_TYPE.formatted(killedEntity.getType()));

        final DropType mobType = CardUtil.getMobType(killedEntity.getType());
        debug(InternalDebug.DropListener.MOB_TYPE.formatted(mobType));

        final MobDropPool mobDropPool = this.dropPoolsConfig == null ? null : this.dropPoolsConfig.getMobDropPool(killedEntity);
        if (mobDropPool != null) {
            handlePoolDrop(entityDeathEvent, killer, mobType, mobDropPool);
            return;
        }

        if (isMythicMob) {
            return;
        }

        if (!CardUtil.shouldDrop(mobType)) {
            return;
        }

        String rarityName = cardManager.getRandomRarityId(mobType);
        if (rarityName.equalsIgnoreCase(TradingRarityManager.EMPTY_RARITY.getId())) {
            return;
        }

        //Get the card
        TradingCard randomCard = plugin.getCardManager().getRandomActiveCardByRarity(rarityName);
        if (randomCard instanceof EmptyCard) {
            plugin.debug(DropListener.class, "EmptyCard for some reason, rarity=%s".formatted(rarityName));
            return;
        }

        boolean isShiny = randomCard.hasShiny() && CardUtil.calculateIfShiny(false);
        debug(InternalDebug.DropListener.ADDED_CARD.formatted(CompositeCardKey.fromCard(randomCard)));
        addDroppedCard(entityDeathEvent, killer, randomCard.build(isShiny));
    }

    private void handlePoolDrop(
            final @NotNull EntityDeathEvent entityDeathEvent,
            final @NotNull Player killer,
            final @NotNull DropType mobType,
            final @NotNull MobDropPool mobDropPool
    ) {
        if (mobDropPool.hasCustomDropChance()) {
            final int randomDropChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
            if (!CardUtil.shouldDrop(randomDropChance, mobDropPool.dropChance())) {
                return;
            }
        } else if (!CardUtil.shouldDrop(mobType)) {
            return;
        }

        final int dropAmount = mobDropPool.getDropAmount(plugin.getRandom());
        for (int i = 0; i < dropAmount; i++) {
            final TradingCard randomCard = resolvePoolCard(mobDropPool);
            if (randomCard instanceof EmptyCard) {
                continue;
            }

            final boolean isShiny = randomCard.hasShiny() && CardUtil.calculateIfShiny(false);
            debug(InternalDebug.DropListener.ADDED_CARD.formatted(CompositeCardKey.fromCard(randomCard)));
            addDroppedCard(entityDeathEvent, killer, randomCard.build(isShiny));
        }
    }

    private void addDroppedCard(final @NotNull EntityDeathEvent event, final @NotNull Player killer, final @NotNull ItemStack cardItem) {
        if (plugin.getGeneralConfig().collectorBookEnabled()) {
            CardUtil.dropItem(killer, cardItem);
            return;
        }
        event.getDrops().add(cardItem);
    }

    private @NotNull TradingCard resolvePoolCard(final @NotNull MobDropPool mobDropPool) {
        final DropPoolEntry selectedEntry = mobDropPool.getRandomEntry(plugin.getRandom()).orElse(null);
        if (selectedEntry == null) {
            return AllCardManager.NULL_CARD;
        }

        if (selectedEntry.type() == DropPoolEntryType.CARD) {
            return cardManager.getRandomActiveCardByCardId(selectedEntry.id());
        }

        if (selectedEntry.type() == DropPoolEntryType.RARITY) {
            final String rarityId = plugin.getRarityManager().getRarityIds().stream()
                    .filter(id -> id.equalsIgnoreCase(selectedEntry.id()))
                    .findFirst()
                    .orElse(null);
            if (rarityId == null) {
                return AllCardManager.NULL_CARD;
            }
            return cardManager.getRandomActiveCardByRarity(rarityId);
        }

        return AllCardManager.NULL_CARD;
    }


    //Gets the rarity key for the appropriate player card.
    private @Nullable String getRarityKey(Player player) {
        List<Rarity> rarities = plugin.getRarityManager().getRarities();
        if (rarities == null)
            return null;


        for (final Rarity rarity : rarities) {
            if(cardManager.containsCard(player.getName(),rarity.getId(),plugin.getGeneralConfig().playerSeries())) {
                return rarity.getId();
            }
        }

        debug(InternalDebug.DropListener.NULL_RARITY_KEY);
        return null;
    }
}
