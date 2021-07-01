package net.tinetwork.tradincards.tradincardsplugin.listeners;


import net.tinetwork.tradincards.tradincardsplugin.managers.TradingCardManager;
import net.tinetwork.tradincards.tradincardsplugin.utils.CardUtil;
import net.tinetwork.tradincards.tradincardsplugin.TradingCards;
import net.tinetwork.tradincards.tradincardsplugin.whitelist.PlayerBlacklist;
import net.tinetwork.tradincards.tradincardsplugin.whitelist.WorldBlacklist;
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

    public DropListener(final TradingCards plugin, PlayerBlacklist playerBlacklist, TradingCardManager cardManager) {
        super(plugin);
        this.playerBlacklist = playerBlacklist;
        this.worldBlacklist = new WorldBlacklist(plugin);
        this.cardManager = cardManager;
    }


    //When a player is killed, he can drop a card
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        boolean canPlayerDropCards = plugin.getConfig().getBoolean("General.Player-Drops-Card");
        boolean automaticallyAddPlayerAsCards = plugin.getConfig().getBoolean("General.Auto-Add-Players");
        int playerCardDropRarity = plugin.getConfig().getInt("General.Player-Drops-Card-Rarity");

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
        plugin.debug(e.getDrops().toString());
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
        String rarityName = CardUtil.calculateRarity(e.getEntityType(), false);
        if (rarityName.equals("None"))
            return;

        //Generate the card
        ItemStack randomCard = CardUtil.getRandomActiveCard(rarityName, false).build();

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
                plugin.debug(rarity);
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
            plugin.debug("Mob came from spawner, not dropping card.");
            return true;
        }

        return false;
    }
}
