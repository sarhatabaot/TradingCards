package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.impl.TradingRarityManager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class GiveCommands extends BaseCommand {
    private final TradingCards plugin;

    public GiveCommands(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("give")
    @CommandPermission(Permissions.Admin.Give.GIVE)
    public class GiveSubCommands extends BaseCommand {

        @Subcommand("card")
        @CommandPermission(Permissions.Admin.Give.GIVE_CARD)
        @Description("Gives a card.")
        public class CardSubCommand extends BaseCommand {
            @Default
            @Subcommand("self")
            @CommandCompletion("@rarities @series @cards @bool")
            @Description("Gives yourself a card.")
            public void onDefault(final Player player, @Single final Rarity rarity, @Single final Series series, @Single final String cardId, @Single final boolean shiny) {
                onPlayer(player, player.getName(), rarity, series, cardId, shiny);
            }

            @Subcommand("player")
            @CommandPermission(Permissions.Admin.Give.GIVE_CARD_PLAYER)
            @CommandCompletion("@players @rarities @series @cards @bool")
            public void onPlayer(final CommandSender sender, @Single final String playerName, @Single final @NotNull Rarity rarity, @Single final @NotNull Series series, @Single final String cardId, @Single final boolean shiny) {
                TradingCard card = plugin.getCardManager().getCard(cardId, rarity.getId(), series.getId()).isShiny(shiny).get();
                if (shiny && !card.hasShiny()) {
                    ChatUtil.sendPrefixedMessage(sender, "This card does not have a shiny version.");
                    return;
                }

                if(shiny && !sender.hasPermission(Permissions.Admin.Give.GIVE_CARD_SHINY)) {
                    ChatUtil.sendPrefixedMessage(sender,"You do not have permission %s".formatted(Permissions.Admin.Give.GIVE_CARD_SHINY));
                    return;
                }

                if (card instanceof EmptyCard) {
                    sender.sendMessage(plugin.getMessagesConfig().noCard());
                    return;
                }

                Player target = Bukkit.getPlayerExact(playerName);
                if (isOnline(target)) {
                    ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                    return;
                }


                target.sendMessage(ChatUtil.color(plugin.getMessagesConfig().giveCard()
                        .replaceAll(PlaceholderUtil.PLAYER.asRegex(), target.getName())
                        .replaceAll(PlaceholderUtil.CARD.asRegex(), rarity.getDisplayName() + " " + card.getDisplayName())));

                target.getInventory().addItem(card.build(shiny));
            }
        }

        @Subcommand("pack")
        @Description("Gives a pack to a player.")
        @CommandCompletion("@players @packs")
        @CommandPermission(Permissions.Admin.Give.GIVE_PACK)
        public void onGiveBoosterPack(final CommandSender sender, @Single final String playerName, @Single final String packId) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (isOnline(player)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                return;
            }

            CardUtil.dropItem(player, plugin.getPackManager().getPackItem(packId));

            sender.sendMessage(plugin.getMessagesConfig().givePack()
                    .replaceAll(PlaceholderUtil.PLAYER.asRegex(), player.getName())
                    .replaceAll(PlaceholderUtil.PACK.asRegex(), packId));
            player.sendMessage(plugin.getMessagesConfig().boosterPackMsg());
        }

        private boolean isOnline(final Player player) {
            return player == null;
        }


        @Subcommand("random")
        public class RandomSubCommand extends BaseCommand {

            @Subcommand("entity")
            @Description("Gives a random card to a player.")
            @CommandPermission(Permissions.Admin.Give.GIVE_RANDOM_ENTITY)
            public void onGiveRandomCard(final CommandSender sender, @Single final String playerName, final EntityType entityType) {
                Player player = Bukkit.getPlayerExact(playerName);
                if (isOnline(player)) {
                    ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                    return;
                }

                try {
                    String rare = plugin.getCardManager().getRandomRarityId(CardUtil.getMobType(entityType));
                    if(rare.equalsIgnoreCase(TradingRarityManager.EMPTY_RARITY.getId()))
                        return;
                    plugin.debug(getClass(), "RarityId: " + rare);
                    ChatUtil.sendMessage(sender,
                            plugin.getMessagesConfig().giveRandomCardMsg().replaceAll(PlaceholderUtil.PLAYER.asRegex(), player.getName()));
                    CardUtil.dropItem(player, plugin.getCardManager().getRandomCardByRarity(rare).build(false));
                } catch (IllegalArgumentException exception) {
                    player.sendMessage(plugin.getMessagesConfig().noEntity());
                }
            }

            @Subcommand("rarity")
            @Description("Gives a random card to a player. Specify rarity.")
            @CommandCompletion("@players @rarities")
            @CommandPermission(Permissions.Admin.Give.GIVE_RANDOM_RARITY)
            public void onGiveRandomCardByRarity(final CommandSender sender, @Single final String playerName, @Single final String rarityId) {
                Player player = Bukkit.getPlayerExact(playerName);
                if (isOnline(player)) {
                    ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                    return;
                }

                if(!plugin.getRarityManager().containsRarity(rarityId)) {
                    ChatUtil.sendMessage(sender,plugin.getMessagesConfig().noRarity());
                    return;
                }

                plugin.debug(GiveCommands.class, "RarityId: " + rarityId);
                sender.sendMessage(plugin.getMessagesConfig().giveRandomCardMsg().replaceAll(PlaceholderUtil.PLAYER.asRegex(), player.getName()));
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCardByRarity(rarityId).build(false));
            }

            @Subcommand("series")
            @Description("Gives a random card to a player. Specify series.")
            @CommandCompletion("@players @series")
            public void onGiveRandomCardBySeries(final CommandSender sender, @Single final String playerName, @Single final String seriesId) {
                Player player = Bukkit.getPlayerExact(playerName);
                if (isOnline(player)) {
                    ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                    return;
                }

                if(!plugin.getSeriesManager().containsSeries(seriesId)) {
                    ChatUtil.sendMessage(sender,plugin.getMessagesConfig().noSeries());
                    return;
                }
                plugin.debug(GiveCommands.class, "SeriesId: " + seriesId);
                sender.sendMessage(plugin.getMessagesConfig().giveRandomCardMsg().replaceAll(PlaceholderUtil.PLAYER.asRegex(), player.getName()));
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCardBySeries(seriesId).build(false));
            }

        }


    }
}
