package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.github.sarhatabaot.tradingcards.extensions.PlayerExtensionsKt;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.model.Upgrade;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class UpgradeCommand extends BaseCommand {
    private final TradingCards plugin;

    public UpgradeCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("upgrade")
    public class UpgradeSubCommand extends BaseCommand {

        @Default
        public void onDefault(final Player player, @Optional Integer amount, @Optional Upgrade optionalUpgrade) {
            Upgrade upgrade = getUpgradeOptional(player, optionalUpgrade);

            if (amount == null)
                amount = 1;

            if (upgrade == null || amount == 0) {
                player.sendMessage("No possible upgrades.");
                return;
            }

            if (!CardUtil.hasCardsInInventory(player, upgrade.required())) {
                player.sendMessage("Not enough cards of rarity %s, series %s".formatted(upgrade.required().rarityId(), upgrade.required().seriesId()));
                return;
            }

            Map<ItemStack, Integer> removedCards = new HashMap<>();
            for (int i = 0; i < amount; i++) {
                removedCards = Stream.concat(removedCards.entrySet().stream(), CardUtil.removeCardsMatchingEntry(player, upgrade.required()).entrySet().stream())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Integer::sum));
            }
            final int totalCards = removedCards.values().stream().mapToInt(Integer::intValue).sum();
            player.sendMessage("Upgraded %d %s cards to %d %s cards:"
                    .formatted(totalCards, upgrade.required().rarityId(),
                            upgrade.result().amount() * amount, upgrade.result().rarityId()));

            PlayerExtensionsKt.sendTradedCardsMessage(player,removedCards);

            for (int i = 0; i < amount; i++) {
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCardByRarity(upgrade.result().rarityId()).build(false));
            }
        }

        private Upgrade getUpgradeOptional(final Player player, final Upgrade optionalUpgrade) {
            if (optionalUpgrade == null)
                return matchUpgrade(player);
            return optionalUpgrade;
        }

        @Subcommand("max")
        public void onMax(final Player player) {
            Upgrade upgrade = matchUpgrade(player);
            if (upgrade == null) {
                player.sendMessage("No possible upgrades.");
                return;
            }

            int maxPossibleUpgrades = calcMaxPossibleUpgrades(player, upgrade.required());
            onDefault(player, maxPossibleUpgrades, upgrade);
        }

        //Attempt to match to a possible upgrade

        /**
         * Attempt to match to a possible upgrade
         * Will return an upgrade if it matches, even if the player doesn't have enough cards
         *
         * @param player Player
         * @return Upgrade that matches.
         */
        private @Nullable Upgrade matchUpgrade(final @NotNull Player player) {
            final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            if (itemInMainHand.getType() == Material.AIR)
                return null;

            final NBTItem nbtItem = new NBTItem(itemInMainHand);
            final String seriesId = NbtUtils.Card.getSeriesId(nbtItem);
            final String rarityId = NbtUtils.Card.getRarityId(nbtItem);

            for (Upgrade upgrade : plugin.getStorage().getUpgrades()) {
                PackEntry required = upgrade.required();

                if (required.seriesId().equals(seriesId) && required.rarityId().equals(rarityId)) {
                    return upgrade;
                }
            }

            return null;
        }

        //Find the amount of matching items stacks
        //Calc the correct amount
        private int calcMaxPossibleUpgrades(final Player player, PackEntry packEntry) {
            if (CardUtil.hasCardsInInventory(player, packEntry))
                return CardUtil.countCardsInInventory(player, packEntry) / packEntry.amount();
            return 0;
        }
    }

}
