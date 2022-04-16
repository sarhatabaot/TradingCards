package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class SellCommand extends BaseCommand {
    private final TradingCards plugin;

    public SellCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }
    @Subcommand("sell")
    @CommandPermission(Permissions.SELL)
    public class SellSubCommand extends BaseCommand {
        @Default
        @Description("Sells the card in your main hand.")
        public void onSell(final Player player) {
            if (!CardUtil.hasVault(player))
                return;

            final NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
            if (!CardUtil.isCard(nbtItem)) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().notACard());
                return;
            }

            final ItemStack itemInHand = player.getInventory().getItemInMainHand();
            final int itemInHandSlot = player.getInventory().getHeldItemSlot();
            final String cardId = nbtItem.getString(NbtUtils.NBT_CARD_NAME);
            final String rarityId = nbtItem.getString(NbtUtils.NBT_RARITY);
            plugin.debug(SellSubCommand.class, "Card name=" + cardId + ", Card rarity=" + rarityId);

            final TradingCard tradingCard = plugin.getCardManager().getCard(cardId, rarityId, false);
            if (tradingCard.isShiny()) {
                ChatUtil.sendPrefixedMessage(player, "Cannot sell shiny card.");
                return;
            }

            if (tradingCard.getSellPrice() <= 0.00D) {
                ChatUtil.sendPrefixedMessage(player, CardsCommand.CANNOT_SELL_CARD);
                return;
            }

            PlayerInventory inventory = player.getInventory();
            double sellAmount = tradingCard.getSellPrice() * itemInHand.getAmount();
            EconomyResponse economyResponse = plugin.getEcon().depositPlayer(player, sellAmount);
            if (economyResponse.transactionSuccess()) {
                ChatUtil.sendPrefixedMessage(player, String.format("You have sold %dx%s for %.2f", itemInHand.getAmount(), (rarityId + " " + cardId), sellAmount));
                inventory.setItem(itemInHandSlot, null);
            }
        }
    }
}
