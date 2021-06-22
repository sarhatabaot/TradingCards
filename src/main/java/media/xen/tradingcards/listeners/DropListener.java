package media.xen.tradingcards.listeners;


import media.xen.tradingcards.CardManager;
import media.xen.tradingcards.CardUtil;
import media.xen.tradingcards.TradingCards;
import media.xen.tradingcards.whitelist.PlayerBlacklist;
import media.xen.tradingcards.whitelist.WorldBlacklist;
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

    public DropListener(final TradingCards plugin, final PlayerBlacklist playerBlacklist, final WorldBlacklist worldBlacklist) {
        super(plugin);
        this.playerBlacklist = playerBlacklist;
        this.worldBlacklist = worldBlacklist;
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

        ItemStack playerCard = CardManager.getActiveCard(killedPlayer.getName(), rarityKey, false);
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
        ItemStack randomCard = CardUtil.getRandomActiveCard(rarityName, false);

        debug("Successfully generated card.");

        //Add the card to the killedEntity drops
        e.getDrops().add(randomCard);
    }

    private String getRarityKey(Player player) {
        ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("Rarities");
        if (rarities == null)
            return null;

        Set<String> rarityKeys = rarities.getKeys(false);

        for (final String key : rarityKeys) {
            if (plugin.getCardsConfig().getConfig().contains("Cards." + key + "." + player.getName())) {
                plugin.debug(key);
                return key;
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
