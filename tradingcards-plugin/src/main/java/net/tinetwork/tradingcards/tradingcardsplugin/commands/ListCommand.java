package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class ListCommand extends BaseCommand {
    private final TradingCards plugin;

    public ListCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }
    @Subcommand("list")
    @CommandPermission(Permissions.LIST)
    @Description("Lists all cards by rarities")
    public class ListSubCommand extends BaseCommand {

        @Default
        @CommandCompletion("@rarities")
        public void onList(final CommandSender sender, @Single @Optional final String rarity) {
            onListPlayer(sender, sender.getName(), rarity);
        }

        @Subcommand("player")
        @CommandPermission(Permissions.LIST_PLAYER)
        @CommandCompletion("@players @rarities")
        @Description("Lists all cards by a player.")
        public void onListPlayer(final CommandSender sender, @Single final String playerName, @Single @Optional final String rarity) {
            Player target = Bukkit.getPlayerExact(playerName);
            if (target == null) {
                ChatUtil.sendPrefixedMessage(sender, CardsCommand.PLAYER_NOT_ONLINE);
                return;
            }
            if (rarity == null) {
                final String sectionFormat = String.format(plugin.getMessagesConfig().sectionFormatPlayer(), target.getName());
                ChatUtil.sendMessage(sender, String.format(sectionFormat, target.getName()));
                for (Rarity rarityKey : plugin.getRarityManager().getRarities()) {
                    listRarity(sender, target, rarityKey.getName());
                }
                return;
            }

            if (!plugin.getRarityManager().containsRarity(rarity)) {
                ChatUtil.sendMessage(sender, plugin.getMessagesConfig().noRarity());
                return;
            }

            listRarity(sender, target, rarity);
        }

        private boolean canBuyPack(final String name) {
            Pack pack = plugin.getPackManager().getPack(name);
            return plugin.getGeneralConfig().vaultEnabled() && pack.getBuyPrice() > 0.0D;
        }

        @Subcommand("pack")
        @CommandPermission(Permissions.LIST_PACK)
        @Description("Lists all packs.")
        public void onListPack(final CommandSender sender) {
            int lineNumber = 0;
            ChatUtil.sendMessage(sender, plugin.getMessagesConfig().packSection());

            for (String packName : plugin.getPackManager().getCachedPacksItemStacks().keySet()) {
                Pack pack = plugin.getPackManager().getPack(packName);
                ++lineNumber;
                if (canBuyPack(packName)) {
                    ChatUtil.sendMessage(sender, "&6" + lineNumber + ") &e" + pack.getDisplayName() + " &7(&aPrice: " + pack.getBuyPrice() + "&7)");
                } else {
                    ChatUtil.sendMessage(sender, "&6" + lineNumber + ") &e" + pack.getDisplayName());
                }
                final String packEntries = StringUtils.join(pack.getPackEntryList(), " ");
                ChatUtil.sendMessage(sender, "  &7- &f&o" + packEntries);
            }
        }

        private TradingCard getCard(final String id, final String rarity) {
            return plugin.getCardManager().getCard(id, rarity, false);
        }

        private @NotNull String generateRarityCardList(final Player target, final String rarityId) {
            final StringBuilder stringBuilder = new StringBuilder();
            String prefix = "";
            final List<String> rarityCardListName = plugin.getCardManager().getRarityCardListNames(rarityId);
            if (rarityCardListName == null || rarityCardListName.isEmpty())
                return "";

            for (final String cardId : plugin.getCardManager().getRarityCardListNames(rarityId)) {
                plugin.debug(ListSubCommand.class, "rarityId=" + rarityId + ",cardId=" + cardId);
                TradingCard card = getCard(cardId, rarityId);
                plugin.debug(ListSubCommand.class, card.toString());

                final String color = plugin.getGeneralConfig().colorListHaveCard();
                final String shinyColor = plugin.getGeneralConfig().colorListHaveCardShiny();

                stringBuilder.append(prefix);
                if (plugin.getDeckManager().hasShinyCard(target, cardId, rarityId)) {
                    stringBuilder.append(shinyColor)
                            .append(card.getDisplayName().replace("_", " "));
                } else if (plugin.getDeckManager().hasCard(target, cardId, rarityId)) {
                    stringBuilder.append(color)
                            .append(card.getDisplayName().replace("_", " "));
                } else {
                    stringBuilder.append("&7")
                            .append(card.getDisplayName().replace("_", " "));
                }
                prefix = "&f, ";
            }

            return stringBuilder.toString();
        }

        private void listRarity(final CommandSender sender, final Player target, final String rarityId) {
            plugin.debug(ListSubCommand.class, rarityId);
            final Rarity rarityObject = plugin.getRarityManager().getRarity(rarityId);

            final String sectionFormat = plugin.getMessagesConfig().sectionFormat();
            final String sectionFormatComplete = plugin.getMessagesConfig().sectionFormatComplete();

            int cardCounter = countPlayerCardsInRarity(target, rarityId);
            int sizeOfRarityCardList = getSizeOfRarityCardList(rarityId);
            //send title
            if (cardCounter == sizeOfRarityCardList) {
                ChatUtil.sendMessage(sender, String.format(sectionFormatComplete, rarityObject.getDisplayName(), plugin.getGeneralConfig().colorRarityCompleted()));
            } else {
                ChatUtil.sendMessage(sender, String.format(sectionFormat, rarityObject.getDisplayName(), cardCounter, sizeOfRarityCardList));
            }

            //send actual message
            final String rarityCardList = generateRarityCardList(target, rarityId);
            ChatUtil.sendMessage(sender, rarityCardList);
        }

        private int getSizeOfRarityCardList(final String rarityId) {
            final List<TradingCard> rarityCardList = plugin.getCardManager().getRarityCardList(rarityId);
            if (rarityCardList == null || rarityCardList.isEmpty())
                return 0;
            return rarityCardList.size();
        }

        private int countPlayerCardsInRarity(final Player player, final String rarity) {
            final List<String> rarityCardList = plugin.getCardManager().getRarityCardListNames(rarity);
            int cardCounter = 0;
            if (rarityCardList == null || rarityCardList.isEmpty())
                return cardCounter;

            for (String cardId : rarityCardList) {
                if (plugin.getDeckManager().hasCard(player, cardId, rarity)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }

        private int countShinyPlayerCardsInRarity(final Player player, final String rarity) {
            final List<String> rarityCardList = plugin.getCardManager().getRarityCardListNames(rarity);
            int cardCounter = 0;
            for (String cardId : rarityCardList) {
                if (plugin.getDeckManager().hasShinyCard(player, cardId, rarity)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }
    }
}
