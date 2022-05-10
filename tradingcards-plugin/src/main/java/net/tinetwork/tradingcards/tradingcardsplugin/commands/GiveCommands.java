package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
    @CommandPermission(Permissions.GIVE)
    public class GiveSubCommands extends BaseCommand {

        @Subcommand("card")
        @CommandPermission(Permissions.GIVE_CARD)
        @Description("Gives a card.")
        public class CardSubCommand extends BaseCommand {
            @Default
            @Description("Gives yourself a card.")
            public void onDefault(final Player player, @Single final String rarityId, @Single final String cardId, @Single final String seriesId) {
                onPlayer(player, player.getName(), rarityId, cardId, seriesId, false);
            }

            @Subcommand("player")
            @CommandPermission(Permissions.GIVE_CARD_PLAYER)
            @CommandCompletion("@players @rarities @cards @series")
            public void onPlayer(final CommandSender sender, @Single final String playerName, @Single final String rarityId, @Single final String cardId, @Single final String seriesId, @Single final boolean shiny) {
                TradingCard card = plugin.getCardManager().getCard(cardId, rarityId, seriesId, shiny);
                if (shiny && !card.hasShiny()) {
                    ChatUtil.sendPrefixedMessage(sender, "This card does not have a shiny version.");
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


                target.sendMessage(plugin.getMessagesConfig().giveCard()
                        .replaceAll(PlaceholderUtil.PLAYER.asRegex(), target.getName())
                        .replaceAll(PlaceholderUtil.CARD.asRegex(), rarityId + " " + cardId));

                target.getInventory().addItem(card.build(shiny));
            }

            @Subcommand("shiny")
            @CommandPermission(Permissions.GIVE_CARD_SHINY)
            @CommandCompletion("@rarities @cards")
            @Description("Gives a shiny card.")
            public void onShiny(final Player player, @Single final String rarityId, @Single final String cardId,@Single final String seriesId) {
                onPlayer(player, player.getName(), rarityId, cardId, seriesId,true);
            }
        }

        @Subcommand("pack")
        @Description("Gives a pack to a player.")
        @CommandCompletion("@players @packs")
        @CommandPermission(Permissions.GIVE_PACK)
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

        @Subcommand("random entity")
        @Description("Gives a random card to a player.")
        @CommandPermission(Permissions.GIVE_RANDOM_ENTITY)
        public void onGiveRandomCard(final CommandSender sender, @Single final String playerName, final EntityType entityType) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (isOnline(player)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                return;
            }

            try {
                String rare = plugin.getCardManager().getRandomRarity(CardUtil.getMobType(entityType), true);
                plugin.debug(getClass(), "RarityId: " + rare);
                ChatUtil.sendMessage(sender,
                        plugin.getMessagesConfig().giveRandomCardMsg().replaceAll(PlaceholderUtil.PLAYER.asRegex(), player.getName()));
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCardByRarity(rare).build(false));
            } catch (IllegalArgumentException exception) {
                player.sendMessage(plugin.getMessagesConfig().noEntity());
            }
        }

        @Subcommand("random rarity")
        @Description("Gives a random card to a player. Specify rarity.")
        @CommandCompletion("@players @rarities")
        @CommandPermission(Permissions.GIVE_RANDOM_RARITY)
        public void onGiveRandomCard(final CommandSender sender, @Single final String playerName, @Single final String rarityId) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (isOnline(player)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                return;
            }


            plugin.debug(GiveCommands.class, "RarityId: " + rarityId);
            sender.sendMessage(plugin.getMessagesConfig().giveRandomCardMsg().replaceAll(PlaceholderUtil.PLAYER.asRegex(), player.getName()));
            CardUtil.dropItem(player, plugin.getCardManager().getRandomCardByRarity(rarityId).build(false));
        }
    }
}
