package net.tinetwork.tradingcards.tradingcardsplugin.listeners;


import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.PlayerBlacklist;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.WorldBlacklist;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class DropListener extends SimpleListener {
    private final PlayerBlacklist playerBlacklist;
    private final WorldBlacklist worldBlacklist;
    private final TradingCardManager cardManager;

    public DropListener(final TradingCards plugin, TradingCardManager cardManager) {
        super(plugin);
        this.playerBlacklist = plugin.getPlayerBlacklist();
        this.worldBlacklist = plugin.getWorldBlacklist();
        this.cardManager = cardManager;
    }


    //When a player is killed, he can drop a card
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        boolean canPlayerDropCards = plugin.getMainConfig().playerDropCard;
        boolean automaticallyAddPlayerAsCards = plugin.getMainConfig().autoAddPlayers;
        int playerCardDropRarity = plugin.getMainConfig().playerDropCardRarity;

        if (!canPlayerDropCards || !automaticallyAddPlayerAsCards)
            return;

        final Player killedPlayer = e.getEntity();
        final Player killer = killedPlayer.getKiller();

        if (killer == null)
            return;

        if (plugin.getRandom().nextInt(100) + 1 > playerCardDropRarity)
            return;

        String rarityKey = getRarityKey(killedPlayer);
        if (rarityKey == null)
            return;

        ItemStack playerCard = cardManager.getActiveCard(killedPlayer.getName(), rarityKey, false).build();
        e.getDrops().add(playerCard);
        debug(e.getDrops().toString());
    }

    private String getSeriesFromLore(List<String> lore) {
        for(String line: lore) {
            if (line.contains("Series"))
                return CardUtil.stripAllColor(line).split("Series:")[1].trim();
        }
        return "";
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
        if (isSpawnerMob(killedEntity)) return;
        //Get card rarity
        debug("EntityType="+killedEntity.getType());
        debug("MobType="+CardUtil.getMobTypeOrNone(killedEntity.getType(),false));
        String rarityName = cardManager.getRandomRarity(CardUtil.getMobTypeOrNone(killedEntity.getType(), false));
        if (rarityName.equals("None"))
            return;

        //Generate the card
        ItemStack randomCard = plugin.getCardManager().getRandomActiveCard(rarityName, false).build();

        debug("Successfully generated card.");

        //Add the card to the killedEntity drops
        e.getDrops().add(randomCard);
    }

    private String getRarityKey(Player player) {
        ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("Rarities");
        if (rarities == null)
            return null;

        Set<String> rarityKeys = rarities.getKeys(false);

        for (final String rarity : rarityKeys) {
            if(!cardManager.getCard(player.getName(),rarity,false).getCardName().equals("nullCard")) {
                debug(rarity);
                return rarity;
            }
        }

        plugin.getLogger().info("rarityKey is null");
        return null;
    }

    private boolean isSpawnerMob(LivingEntity killedEntity) {
        String customName = killedEntity.getCustomName();
        boolean spawnerDropBlocked = plugin.getMainConfig().spawnerBlock;
        String spawnerMobName = plugin.getMainConfig().spawnerMobName;

        if (spawnerDropBlocked && customName != null && customName.equals(spawnerMobName)) {
            debug("Mob came from spawner, not dropping card.");
            return true;
        }

        return false;
    }
}
