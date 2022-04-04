package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
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
            public void onDefault(final Player player, @Single final String rarity, @Single final String cardName) {
                onPlayer(player, player.getName(), rarity, cardName, false);
            }

            @Subcommand("player")
            @CommandPermission(Permissions.GIVE_CARD_PLAYER)
            @CommandCompletion("@players @rarities @cards")
            public void onPlayer(final CommandSender sender, @Single final String playerName, @Single final String rarityId, @Single final String cardId, @Single final boolean shiny) {
                TradingCard card = plugin.getCardManager().getCard(cardId, rarityId, shiny);
                if (shiny && !card.hasShiny()) {
                    ChatUtil.sendPrefixedMessage(sender, "This card does not have a shiny version.");
                    return;
                }

                if (card instanceof EmptyCard) {
                    ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().noCard());
                    return;
                }

                Player target = Bukkit.getPlayerExact(playerName);
                if (isOnline(target)) {
                    ChatUtil.sendPrefixedMessage(sender, CardsCommand.PLAYER_NOT_ONLINE);
                    return;
                }


                ChatUtil.sendPrefixedMessage(target, plugin.getMessagesConfig().giveCard()
                        .replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), target.getName())
                        .replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.CARD), rarityId + " " + cardId));

                target.getInventory().addItem(card.build(shiny));
            }

            @Subcommand("shiny")
            @CommandPermission(Permissions.GIVE_CARD_SHINY)
            @CommandCompletion("@rarities @cards")
            @Description("Gives a shiny card.")
            public void onShiny(final Player player, @Single final String rarity, @Single final String cardName) {
                onPlayer(player, player.getName(), rarity, cardName, true);
            }
        }

        @Subcommand("pack")
        @Description("Gives a pack to a player.")
        @CommandCompletion("@players @packs")
        @CommandPermission(Permissions.GIVE_PACK)
        public void onGiveBoosterPack(final CommandSender sender, @Single final String playerName, @Single final String pack) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (isOnline(player)) {
                ChatUtil.sendPrefixedMessage(sender, CardsCommand.PLAYER_NOT_ONLINE);
                return;
            }

            CardUtil.dropItem(player, plugin.getPackManager().getPackItem(pack));

            ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().givePack().replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), player.getName()).replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PACK), pack));
            ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().boosterPackMsg());
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
                ChatUtil.sendPrefixedMessage(sender, CardsCommand.PLAYER_NOT_ONLINE);
                return;
            }

            try {
                String rare = plugin.getCardManager().getRandomRarity(CardUtil.getMobType(entityType), true);
                plugin.debug(getClass(), "Rarity: " + rare);
                ChatUtil.sendPrefixedMessage(sender,
                        plugin.getMessagesConfig().giveRandomCardMsg().replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), player.getName()));
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCard(rare).build(false));
            } catch (IllegalArgumentException exception) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().noEntity());
            }
        }

        @Subcommand("random rarity")
        @Description("Gives a random card to a player. Specify rarity.")
        @CommandCompletion("@players @rarities")
        @CommandPermission(Permissions.GIVE_RANDOM_RARITY)
        public void onGiveRandomCard(final CommandSender sender, @Single final String playerName, @Single final String rarity) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (isOnline(player)) {
                ChatUtil.sendPrefixedMessage(sender, CardsCommand.PLAYER_NOT_ONLINE);
                return;
            }


            plugin.debug(GiveCommands.class, "Rarity: " + rarity);
            ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().giveRandomCardMsg().replaceAll(PlaceholderUtil.matchAllAsRegEx(PlaceholderUtil.PLAYER), player.getName()));
            CardUtil.dropItem(player, plugin.getCardManager().getRandomCard(rarity).build(false));
        }
    }
}
