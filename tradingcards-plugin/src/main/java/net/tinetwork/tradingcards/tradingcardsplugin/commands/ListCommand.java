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
import net.tinetwork.tradingcards.api.model.Upgrade;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.CollectorBookManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeCardKey;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class ListCommand extends BaseCommand {
    private final TradingCards plugin;
    private final CollectorBookManager collectorBookManager;

    public ListCommand(final TradingCards plugin) {
        this.plugin = plugin;
        this.collectorBookManager = plugin.getCollectorBookManager();
    }

    @Subcommand("list")
    @CommandPermission(Permissions.User.List.LIST)
    @Description("Lists all cards by rarities")
    public class ListSubCommand extends BaseCommand {

        private record OwnedCardLookup(Set<CompositeCardKey> ownedCards, Set<CompositeCardKey> ownedShinyCards) {
            private boolean hasCard(final CompositeCardKey key) {
                return ownedCards.contains(key);
            }

            private boolean hasShinyCard(final CompositeCardKey key) {
                return ownedShinyCards.contains(key);
            }
        }

        @Default
        @Description("Lists all cards from all rarities")
        public void onList(final CommandSender sender) {
            onListPlayerRarity(sender, sender.getName(), null);
        }

        @Subcommand("rarity")
        @CommandCompletion("@rarities")
        public void onListRarity(final CommandSender sender, @Single final String rarityId) {
            onListPlayerRarity(sender, sender.getName(), rarityId);
        }

        @Subcommand("series")
        @CommandCompletion("@series")
        public void onListSeries(final CommandSender sender, @Single final String seriesId) {
            onListPlayerSeries(sender, sender.getName(), seriesId);
        }

        @Subcommand("player series")
        @CommandCompletion("@players @series")
        public void onListPlayerSeries(final CommandSender sender, @Single final String playerName, @Single @Optional final String seriesId) {
            Player target = Bukkit.getPlayerExact(playerName);
            if (target == null) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                return;
            }
            final OwnedCardLookup lookup = buildOwnedCardLookup(target);
            if (seriesId == null) {
                final String sectionFormat = String.format(plugin.getMessagesConfig().sectionFormatPlayer(), target.getName());
                ChatUtil.sendMessage(sender, String.format(sectionFormat, target.getName()));
                for (Series seriesKey : plugin.getSeriesManager().getAllSeries()) {
                    listSeries(sender, target, seriesKey.getId(), lookup);
                }
                return;
            }

            if (!plugin.getSeriesManager().containsSeries(seriesId)) {
                ChatUtil.sendMessage(sender, "No such series " + seriesId);
                return;
            }

            listSeries(sender, target, seriesId, lookup);
        }

        private void listSeries(final CommandSender sender, final Player target, final String seriesId, final OwnedCardLookup lookup) {
            plugin.debug(ListSubCommand.class, seriesId);
            final Series seriesObject = plugin.getSeriesManager().getSeries(seriesId);

            ChatUtil.sendMessage(sender, getSeriesListTitle(seriesId, seriesObject.getDisplayName(), lookup));
            //send actual message
            final String seriesCardList = generateSeriesCardList(seriesId, lookup);
            ChatUtil.sendMessage(sender, seriesCardList);
        }



        private String getSeriesListTitle(final String seriesId, final String seriesDisplayName, final OwnedCardLookup lookup) {
            final String sectionFormat = plugin.getMessagesConfig().sectionFormat();
            final String sectionFormatComplete = plugin.getMessagesConfig().sectionFormatComplete();
            int cardCounter = countPlayerOwnedCardsInSeries(seriesId, lookup);
            int shinyCardCounter = countPlayerOwnedShinyCardsInSeries(seriesId, lookup);
            int sizeOfRarityCardList;
            try {
                sizeOfRarityCardList = plugin.getCardManager().getSeriesCardCache().getIfPresent(seriesId).size();
            }catch (NullPointerException e) {
                sizeOfRarityCardList = 0;
            }


            if (cardCounter == sizeOfRarityCardList) {
                return sectionFormatComplete
                        .replaceAll(PlaceholderUtil.SHINY_CARDS_OWNED.asRegex(), String.valueOf(shinyCardCounter))
                        .formatted(seriesDisplayName, plugin.getGeneralConfig().colorRarityCompleted());
            }

            return sectionFormat
                    .replaceAll(PlaceholderUtil.CARDS_OWNED.asRegex(), String.valueOf(cardCounter))
                    .replaceAll(PlaceholderUtil.SHINY_CARDS_OWNED.asRegex(), String.valueOf(shinyCardCounter))
                    .replaceAll(PlaceholderUtil.CARDS_TOTAL.asRegex(), String.valueOf(sizeOfRarityCardList))
                    .formatted(seriesDisplayName);
        }


        @Subcommand("player rarity")
        @CommandPermission(Permissions.User.List.LIST_PLAYER)
        @CommandCompletion("@players @rarities")
        @Description("Lists all cards by a player.")
        public void onListPlayerRarity(final CommandSender sender, @Single final String playerName, @Single @Optional final String rarityId) {
            Player target = Bukkit.getPlayerExact(playerName);
            if (target == null) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.CardsCommand.PLAYER_OFFLINE);
                return;
            }
            final OwnedCardLookup lookup = buildOwnedCardLookup(target);

            if (rarityId == null) {
                final String sectionFormat = plugin.getMessagesConfig().sectionFormatPlayer().replaceAll(PlaceholderUtil.PLAYER.asRegex(), target.getName());
                ChatUtil.sendMessage(sender, sectionFormat);
                for (Rarity rarityKey : plugin.getRarityManager().getRarities()) {
                    listRarity(sender, target, rarityKey.getId(), lookup);
                }
                return;
            }

            if (!plugin.getRarityManager().containsRarity(rarityId)) {
                ChatUtil.sendMessage(sender, plugin.getMessagesConfig().noRarity());
                return;
            }

            listRarity(sender, target, rarityId, lookup);
        }

        private boolean canBuyPack(final String name) {
            Pack pack = plugin.getPackManager().getPack(name);
            return plugin.getGeneralConfig().vaultEnabled() && pack.getBuyPrice() > 0.0D;
        }

        @Subcommand("pack")
        @CommandPermission(Permissions.User.List.LIST_PACK)
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

        @Subcommand("upgrades")
        @CommandPermission(Permissions.User.List.LIST_UPGRADE)
        @Description("List all upgrades.")
        public void onListUpgrades(final CommandSender sender) {
            ChatUtil.sendMessage(sender,"Currently Available Upgrades %d".formatted(plugin.getUpgradeManager().getUpgrades().size()));
            ChatUtil.sendMessage(sender, StringUtils.join(plugin.getUpgradeManager().getUpgrades().stream().map(Upgrade::id).toList(), ","));
        }

        private @NotNull String generateSeriesCardList(final String seriesId, final OwnedCardLookup lookup) {
            final StringBuilder stringBuilder = new StringBuilder();
            String prefix = "";
            final List<TradingCard> seriesCardListName = plugin.getStorage().getCardsInSeries(seriesId);
            if (seriesCardListName == null || seriesCardListName.isEmpty())
                return "";

            for (final TradingCard card : seriesCardListName) {
                plugin.debug(ListSubCommand.class, "seriesId=" + seriesId + ",cardId=" + card.getCardId());
                plugin.debug(ListSubCommand.class, card.toString());

                final String color = plugin.getGeneralConfig().colorListHaveCard();
                final String shinyColor = plugin.getGeneralConfig().colorListHaveCardShiny();

                stringBuilder.append(prefix);
                final String cardDisplayName = card.getDisplayName().replace("_", " ");
                final CompositeCardKey cardKey = new CompositeCardKey(card.getRarity().getId(), seriesId, card.getCardId());
                if (lookup.hasShinyCard(cardKey)) {
                    stringBuilder.append(shinyColor);
                } else if (lookup.hasCard(cardKey)) {
                    stringBuilder.append(color);
                } else {
                    stringBuilder.append("&7");
                }
                stringBuilder.append(cardDisplayName);
                prefix = "&f, ";
            }

            return stringBuilder.toString();
        }

        private @NotNull String generateRarityCardList(final String rarityId, final OwnedCardLookup lookup) {
            final StringBuilder stringBuilder = new StringBuilder();
            String prefix = "";
            final List<TradingCard> rarityCardList = plugin.getCardManager().getRarityCardCache().getIfPresent(rarityId);
            if (rarityCardList == null)
                return "";

            for(final TradingCard card: rarityCardList) {
                plugin.debug(ListSubCommand.class, "rarityId=" + rarityId );
                plugin.debug(ListSubCommand.class, card.toString());

                final String color = plugin.getGeneralConfig().colorListHaveCard();
                final String shinyColor = plugin.getGeneralConfig().colorListHaveCardShiny();

                stringBuilder.append(prefix);
                final String cardDisplayName = card.getDisplayName().replace("_", " ");
                final CompositeCardKey cardKey = new CompositeCardKey(rarityId, card.getSeries().getId(), card.getCardId());
                if (lookup.hasShinyCard(cardKey)) {
                    stringBuilder.append(shinyColor);
                } else if (lookup.hasCard(cardKey)) {
                    stringBuilder.append(color);
                } else {
                    stringBuilder.append("&7");
                }
                stringBuilder.append(cardDisplayName);
                prefix = "&f, ";
            }

            return stringBuilder.toString();
        }

        private void listRarity(final CommandSender sender, final Player target, final String rarityId, final OwnedCardLookup lookup) {
            plugin.debug(ListSubCommand.class, rarityId);
            final Rarity rarityObject = plugin.getRarityManager().getRarity(rarityId);

            //send title
            ChatUtil.sendMessage(sender, getRarityListTitle(rarityId, rarityObject.getDisplayName(), lookup));

            //send actual message
            final String rarityCardList = generateRarityCardList(rarityId, lookup);
            ChatUtil.sendMessage(sender, rarityCardList);
        }

        private String getRarityListTitle(final String rarityId, final String rarityDisplayName, final OwnedCardLookup lookup) {
            final String sectionFormat = plugin.getMessagesConfig().sectionFormat();
            final String sectionFormatComplete = plugin.getMessagesConfig().sectionFormatComplete();
            int cardCounter = countPlayerOwnedCardsInRarity(rarityId, lookup);
            int shinyCardCounter = countPlayerOwnedShinyCardsInRarity(rarityId, lookup);
            int sizeOfRarityCardList;

            try {
                sizeOfRarityCardList = plugin.getCardManager().getRarityCardCache().get(rarityId).size();
            } catch (NullPointerException e) {
                sizeOfRarityCardList = 0;
            }

            if (cardCounter == sizeOfRarityCardList) {
                return sectionFormatComplete
                        .replaceAll(PlaceholderUtil.SHINY_CARDS_OWNED.asRegex(), String.valueOf(shinyCardCounter))
                        .formatted(rarityDisplayName, plugin.getGeneralConfig().colorRarityCompleted());
            }

            return sectionFormat
                    .replaceAll(PlaceholderUtil.CARDS_OWNED.asRegex(), String.valueOf(cardCounter))
                    .replaceAll(PlaceholderUtil.SHINY_CARDS_OWNED.asRegex(), String.valueOf(shinyCardCounter))
                    .replaceAll(PlaceholderUtil.CARDS_TOTAL.asRegex(), String.valueOf(sizeOfRarityCardList))
                    .formatted(rarityDisplayName);
        }

        private int countPlayerOwnedCardsInRarity(final String rarityId, final OwnedCardLookup lookup) {
            final List<TradingCard> rarityCardList = plugin.getCardManager().getRarityCardList(rarityId);
            int cardCounter = 0;
            if (rarityCardList == null || rarityCardList.isEmpty())
                return cardCounter;

            for (TradingCard card : rarityCardList) {
                final CompositeCardKey cardKey = new CompositeCardKey(rarityId, card.getSeries().getId(), card.getCardId());
                if (lookup.hasCard(cardKey)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }

        private int countPlayerOwnedShinyCardsInRarity(final String rarityId, final OwnedCardLookup lookup) {
            final List<TradingCard> rarityCardList = plugin.getCardManager().getRarityCardList(rarityId);
            int cardCounter = 0;
            if (rarityCardList == null || rarityCardList.isEmpty())
                return cardCounter;

            for (TradingCard card : rarityCardList) {
                final CompositeCardKey cardKey = new CompositeCardKey(rarityId, card.getSeries().getId(), card.getCardId());
                if (lookup.hasShinyCard(cardKey)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }

        private int countPlayerOwnedCardsInSeries(final String seriesId, final OwnedCardLookup lookup) {
            final List<TradingCard> seriesCardList = plugin.getStorage().getCardsInSeries(seriesId);
            int cardCounter = 0;
            if (seriesCardList == null || seriesCardList.isEmpty())
                return cardCounter;

            for (TradingCard card : seriesCardList) {
                final CompositeCardKey cardKey = new CompositeCardKey(card.getRarity().getId(), seriesId, card.getCardId());
                if (lookup.hasCard(cardKey)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }

        private int countPlayerOwnedShinyCardsInSeries(final String seriesId, final OwnedCardLookup lookup) {
            final List<TradingCard> seriesCardList = plugin.getStorage().getCardsInSeries(seriesId);
            int cardCounter = 0;
            if (seriesCardList == null || seriesCardList.isEmpty())
                return cardCounter;

            for (TradingCard card : seriesCardList) {
                final CompositeCardKey cardKey = new CompositeCardKey(card.getRarity().getId(), seriesId, card.getCardId());
                if (lookup.hasShinyCard(cardKey)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }

        private OwnedCardLookup buildOwnedCardLookup(final Player target) {
            final Set<CompositeCardKey> ownedCards = new HashSet<>();
            final Set<CompositeCardKey> ownedShinyCards = new HashSet<>();

            final List<StorageEntry> ownedEntries = collectorBookManager.getOwnedEntries(target.getUniqueId());
            for (StorageEntry entry : ownedEntries) {
                final CompositeCardKey key = new CompositeCardKey(entry.getRarityId(), entry.getSeriesId(), entry.getCardId());
                if (entry.isShiny()) {
                    ownedShinyCards.add(key);
                } else {
                    ownedCards.add(key);
                }
            }
            return new OwnedCardLookup(ownedCards, ownedShinyCards);
        }
    }
}
