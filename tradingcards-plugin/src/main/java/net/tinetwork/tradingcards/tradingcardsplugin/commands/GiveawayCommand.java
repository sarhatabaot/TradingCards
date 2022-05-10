package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class GiveawayCommand extends BaseCommand {
    private final TradingCards plugin;

    public GiveawayCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("giveaway")
    public class GiveawaySubCommand extends BaseCommand {
        @Subcommand("rarity")
        @CommandPermission(Permissions.GIVEAWAY_RARITY)
        @Description("Give away a random card by rarity to the server.")
        @CommandCompletion("@rarities")
        public void onRarity(final CommandSender sender, final String rarityId) {
            if (!plugin.getRarityManager().containsRarity(rarityId)) {
                ChatUtil.sendMessage(sender, plugin.getMessagesConfig().noRarity());
                return;
            }

            Bukkit.broadcastMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveaway()
                    .replaceAll(PlaceholderUtil.PLAYER.asRegex(), sender.getName())
                    .replaceAll(PlaceholderUtil.RARITY.asRegex(), plugin.getRarityManager().getRarity(rarityId).getDisplayName())));
            for (final Player player : Bukkit.getOnlinePlayers()) {
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCardByRarity(rarityId).build(false));
            }
        }

        @Subcommand("series")
        @CommandPermission(Permissions.GIVEAWAY_RARITY)
        @Description("Give away a random card by rarity to the server.")
        @CommandCompletion("@series")
        public void onSeries(final CommandSender sender, final String seriesId) {
            if(!plugin.getSeriesManager().containsSeries(seriesId)) {
                ChatUtil.sendMessage(sender, plugin.getMessagesConfig().noSeries());
                return;
            }

            Bukkit.broadcastMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveaway()
                    .replaceAll(PlaceholderUtil.PLAYER.asRegex(), sender.getName())
                    .replaceAll(PlaceholderUtil.RARITY.asRegex(), plugin.getSeriesManager().getSeries(seriesId).getDisplayName())));
            for (final Player player : Bukkit.getOnlinePlayers()) {
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCardBySeries(seriesId).build(false));
            }
        }


        @Subcommand("entity")
        @CommandPermission(Permissions.GIVEAWAY_ENTITY)
        @Description("Give away a random card by entity to the server.")
        public void onMob(final CommandSender sender, final String entity) {
            if (plugin.isMob(entity)) {
                if (sender instanceof ConsoleCommandSender) {
                    CardUtil.giveawayNatural(EntityType.valueOf(entity.toUpperCase()), null);
                } else {
                    CardUtil.giveawayNatural(EntityType.valueOf(entity.toUpperCase()), (Player) sender);
                }
            }
        }
    }
}
