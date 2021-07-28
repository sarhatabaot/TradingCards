package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tinetwork.tradingcards.api.addons.TradingCardsAddon;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.PlayerBlacklist;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@CommandAlias("cards")
public class CardsCommand extends BaseCommand {
    private final TradingCards plugin;
    private final TradingCardManager cardManager;
    private final TradingDeckManager deckManager;
    private final PlayerBlacklist playerBlacklist;

    public CardsCommand(final TradingCards plugin, final PlayerBlacklist playerBlacklist) {
        this.plugin = plugin;
        this.playerBlacklist = playerBlacklist;
        this.cardManager = plugin.getCardManager();
        this.deckManager = plugin.getDeckManager();
    }

    @CatchUnknown
    @HelpCommand
    public void onHelp(final CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("version|ver")
    @CommandPermission("cards.version")
    @Description("Show the plugin version.")
    public void onVersion(final CommandSender sender) {
        final String format = "%s %s API-%s";
        ChatUtil.sendMessage(sender, String.format(format, plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAPIVersion()));
    }

    @Subcommand("reload")
    @CommandPermission("cards.reload")
    @Description("Reloads all the configs & restart the timer.")
    public void onReload(final CommandSender sender) {
        final String format = "%s %s";
        ChatUtil.sendMessage(sender, String.format(format, plugin.getMessagesConfig().prefix, plugin.getMessagesConfig().reload));
        plugin.reloadAllConfig();
        plugin.reloadManagers();
        if (plugin.getMainConfig().scheduleCards) {
            plugin.startTimer();
        }
    }


    @Subcommand("resolve")
    @CommandPermission("cards.resolve")
    @Description("Shows a player's uuid")
    public void onResolve(final CommandSender sender, final Player player) {
        ChatUtil.sendMessage(sender, plugin.getMessagesConfig().resolveMsg.replace("%name%", player.getName()).replaceAll("%uuid%", player.getUniqueId().toString()));
    }

    @Subcommand("toggle")
    @CommandPermission("cards.toggle")
    @Description("Toggles card drops from mobs.")
    public void onToggle(final Player player) {
        if (playerBlacklist.isAllowed(player)) {
            playerBlacklist.remove(player);
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleDisabled));
        } else {
            playerBlacklist.add(player);
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleEnabled));
        }
    }

    @Subcommand("give")
    @CommandPermission("cards.give")
    public class GiveCommands extends BaseCommand {
        @Subcommand("card")
        @CommandPermission("cards.give.card")
        @CommandCompletion("@rarities @cards")
        @Description("Gives a card.")
        public void onGiveCard(final Player player, final String rarity, final String name) {
            if (cardManager.getCards().containsKey(rarity + "." + name)) {
                player.getInventory().addItem(cardManager.getCard(name, rarity, false).build());
                return;
            }
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().noCard));
        }

        @Subcommand("card shiny")
        @CommandPermission("cards.give.card.shiny")
        @CommandCompletion("@rarities @cards")
        @Description("Gives a shiny card.")
        public void onGiveShinyCard(final Player player, final String rarity, final String name) {
            if (cardManager.getCards().containsKey(rarity + "." + name)) {
                player.getInventory().addItem(cardManager.getCard(name, rarity, true).build());
                return;
            }
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().noCard));
        }


        @Subcommand("boosterpack|pack")
        @Description("Gives a pack to a player.")
        @CommandCompletion("@players @packs")
        @CommandPermission("cards.give.pack")
        public void onGiveBoosterPack(final CommandSender sender, final Player player, final String boosterpack) {
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().boosterPackMsg));
            CardUtil.dropItem(player, plugin.getPackManager().getPackItem(boosterpack));
        }

        @Subcommand("random")
        @Description("Gives a random card to a player.")
        @CommandPermission("cards.give.random")
        public void onGiveRandomCard(final CommandSender sender, final Player player, final EntityType entityType) {
            try {
                String rare = cardManager.getRandomRarity(entityType, true);
                plugin.debug("onCommand.rare: " + rare);
                ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().giveRandomCardMsg.replaceAll("%player%", player.getName()));
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCard(rare, false).build());
            } catch (IllegalArgumentException exception) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().noEntity);
            }
        }
    }


    @Subcommand("debug")
    @CommandPermission("cards.admin.debug")
    public class DebugCommands extends BaseCommand {
        @Subcommand("showcache all")
        @CommandPermission("cards.admin.debug.showcache")
        @Description("Shows the card cache")
        public void showCacheAll(final CommandSender sender) {
            sender.sendMessage(StringUtils.join(cardManager.getCards().keySet(), ","));
        }

        @Subcommand("showcache active")
        @CommandPermission("cards.admin.debug.showcache")
        @Description("Shows the card cache")
        public void showCacheActive(final CommandSender sender) {
            sender.sendMessage(StringUtils.join(cardManager.getActiveCards().keySet(), ","));
        }

        @Subcommand("modules")
        @CommandPermission("cards.admin.debug.modules")
        @Description("Shows all enabled hooks and addons.")
        public void onModules(final CommandSender sender) {
            final StringBuilder builder = new StringBuilder("Enabled Modules/Addons:");
            builder.append("\n");
            builder.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Modules:");
            for (String depend : plugin.getDescription().getSoftDepend()) {
                if (Bukkit.getPluginManager().getPlugin(depend) == null)
                    builder.append(ChatColor.GRAY);
                else {
                    builder.append(ChatColor.GREEN);
                }
                builder.append(depend).append(" ");
            }
            builder.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Addons:");
            for (Plugin bukkitPlugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin instanceof TradingCardsAddon)
                    builder.append(ChatColor.GREEN).append(bukkitPlugin.getName()).append(" ");
            }

            sender.sendMessage(builder.toString());
        }


        @Subcommand("rarities")
        @CommandPermission("cards.admin.debug.rarities")
        @Description("Shows available rarities.")
        public void onRarities(final CommandSender sender) {
            sender.sendMessage(StringUtils.join(plugin.getMainConfig().getRarities(), ", "));
        }

        @Subcommand("exists")
        @CommandPermission("cards.admin.debug.exists")
        @Description("Shows if a card exists or not.")
        public void onExists(final CommandSender sender, final String card, final String rarity) {
            if (cardManager.getCards().containsKey(rarity + "." + card)) {
                sender.sendMessage(String.format("Card %s.%s exists", rarity, card));
                return;
            }
            sender.sendMessage(String.format("Card %s.%s does not exist", rarity, card));
        }
    }


    @Subcommand("list")
    @CommandPermission("cards.list")
    @Description("Lists all cards by rarities")
    public class ListSubCommand extends BaseCommand {
        @Default
        @CommandCompletion("@rarities")
        public void onList(final CommandSender sender, @Optional final String rarity) {
            onListPlayer(sender, (Player) sender, rarity);
        }

        @Subcommand("player")
        @CommandPermission("cards.list.player")
        @CommandCompletion("@players @rarities")
        @Description("Lists all cards by a player.")
        public void onListPlayer(final CommandSender sender, final Player target, @Optional final String rarity) {
            if (rarity == null || plugin.isRarityAndFormat(rarity).equals("None")) {
                final String sectionFormat = String.format("&e&l------- &7(&6&l%s's Collection&7)&e&l -------", target.getName());
                ChatUtil.sendMessage(sender, String.format(sectionFormat, target.getName()));
                for (String raritySection : plugin.getMainConfig().getRarities()) {
                    listRarity(sender, target, raritySection);
                }
                return;
            }
            listRarity(sender, target, rarity);
        }

        private boolean canBuyPack(final String name) {
            return plugin.getMainConfig().vaultEnabled && plugin.getConfig().getDouble("BoosterPacks." + name + ".Price", 0.0D) > 0.0D;
        }

        private boolean hasExtra(final String name) {
            return plugin.getConfig().contains("BoosterPacks." + name + ".ExtraCardRarity") && plugin.getConfig().contains("BoosterPacks." + name + ".NumExtraCards");

        }

        @Subcommand("pack")
        @CommandPermission("cards.list.pack")
        @Description("Lists all packs.")
        public void onListPack(final CommandSender sender) {
            ConfigurationSection boosterPacks = plugin.getConfig().getConfigurationSection("BoosterPacks");
            Set<String> rarityKeys = boosterPacks.getKeys(false);
            int k = 0;
            ChatUtil.sendMessage(sender, "&6--- Booster Packs --- ");

            for (String pack : rarityKeys) {
                ++k;
                if (canBuyPack(pack)) {
                    ChatUtil.sendPrefixedMessage(sender, "&6" + k + ") &e" + pack + " &7(&aPrice: " + plugin.getConfig().getDouble("BoosterPacks." + pack + ".Price") + "&7)");
                } else {
                    ChatUtil.sendPrefixedMessage(sender, "&6" + k + ") &e" + pack);
                }

                if (hasExtra(pack)) {
                    ChatUtil.sendMessage(sender, "  &7- &f&o" + plugin.getConfig().getInt("BoosterPacks." + pack + ".NumNormalCards") + " " + plugin.getConfig().getString("BoosterPacks." + pack + ".NormalCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + pack + ".NumExtraCards") + " " + plugin.getConfig().getString("BoosterPacks." + pack + ".ExtraCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + pack + ".NumSpecialCards") + " " + plugin.getConfig().getString("BoosterPacks." + pack + ".SpecialCardRarity"));
                } else {
                    ChatUtil.sendMessage(sender, "  &7- &f&o" + plugin.getConfig().getInt("BoosterPacks." + pack + ".NumNormalCards") + " " + plugin.getConfig().getString("BoosterPacks." + pack + ".NormalCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + pack + ".NumSpecialCards") + " " + plugin.getConfig().getString("BoosterPacks." + pack + ".SpecialCardRarity"));
                }

            }
        }


        private void listRarity(final CommandSender sender, final Player target, final String rarity) {
            final StringBuilder stringBuilder = new StringBuilder();
            final String sectionFormat = "&6--- %s &7(&c%d&f/&a%d&7)&6 ---";
            final String sectionFormatComplete = "&6--- %s &7(%sComplete&7)&6 ---";

            int cardCounter = 0;

            for (String cardName : plugin.getCardManager().getRarityCardList(rarity)) {
                if (cardCounter > 32) {
                    if (deckManager.hasCard(target, cardName, rarity)) {
                        ++cardCounter;
                    }
                    stringBuilder.append(cardName).append("&7and more!");
                } else {
                    plugin.debug(rarity + ", " + cardName);

                    String colour = plugin.getMainConfig().listHaveCardColour;
                    if (deckManager.hasShiny(target, cardName, rarity)) {
                        ++cardCounter;
                        colour = plugin.getMainConfig().listHaveShinyCardColour;
                        stringBuilder.append(colour).append(cardName.replace("_", " ")).append("&f, ");
                    } else if (deckManager.hasCard(target, cardName, rarity) && !deckManager.hasShiny(target, cardName, rarity)) {
                        ++cardCounter;
                        stringBuilder.append(colour).append(cardName.replace("_", " ")).append("&f, ");
                    } else {
                        stringBuilder.append("&7").append(cardName.replace("_", " ")).append("&f, ");
                    }
                }
            }
            //send title
            if (cardCounter == plugin.getCardManager().getRarityCardList(rarity).size()) {
                ChatUtil.sendMessage(sender, String.format(sectionFormatComplete, plugin.isRarityAndFormat(rarity), plugin.getConfig().getString("Colours.ListRarityComplete")));
            } else {
                ChatUtil.sendMessage(sender, String.format(sectionFormat, plugin.isRarityAndFormat(rarity), cardCounter, plugin.getCardManager().getRarityCardList(rarity).size()));
            }

            ChatUtil.sendMessage(sender, stringBuilder.toString());
        }
    }

    private String getFormattedRarity(final String rarity) {
        for (final String rarityKey : plugin.getMainConfig().getRarities()) {
            if (rarityKey.equalsIgnoreCase(rarity.replace("_", " "))) {
                return rarityKey;
            }
        }
        return "";
    }

    @Subcommand("giveaway")
    @CommandPermission("cards.giveaway")
    @Description("Give away a random card by rarity to the server.")
    @CommandCompletion("@rarities")
    public void onGiveaway(final CommandSender sender, final String rarity) {
        if (plugin.isMob(rarity)) {
            if (sender instanceof ConsoleCommandSender) {
                CardUtil.giveawayNatural(EntityType.valueOf(rarity.toUpperCase()), null);
            } else {
                CardUtil.giveawayNatural(EntityType.valueOf(rarity.toUpperCase()), (Player) sender);
            }
            return;
        }
        if (getFormattedRarity(rarity).equals("")) {
            ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().noRarity);
            return;
        }


        Bukkit.broadcastMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveaway.replace("%player%", sender.getName()).replaceAll("%rarity%", getFormattedRarity(rarity))));
        for (final Player p5 : Bukkit.getOnlinePlayers()) {
            CardUtil.dropItem(p5, cardManager.getRandomCard(rarity, false).build());
        }
    }

    @Subcommand("worth")
    @CommandPermission("cards.worth")
    @Description("Shows a card's worth.")
    public void onWorth(final Player player) {
        if (!hasVault(player)) {
            return;
        }
        if (!CardUtil.isCard(player.getInventory().getItemInMainHand())) {
            ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().notACard);
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        final String keyToUse = itemInHand.getItemMeta().getDisplayName();
        plugin.debug(keyToUse);
        plugin.debug(ChatColor.stripColor(keyToUse));

        String[] splitName = ChatColor.stripColor(keyToUse).split(" ");
        String cardName2 = splitName.length > 1 ? splitName[1] : splitName[0];
        plugin.debug("card=" + cardName2);


        List<String> lore = itemInHand.getItemMeta().getLore();
        String rarity = ChatColor.stripColor(lore.get(lore.size() - 1));
        plugin.debug("rarity=" + rarity);


        double buyPrice = cardManager.getCard(rarity, cardName2, false).getBuyPrice();
        double sellPrice = cardManager.getCard(rarity, cardName2, false).getSellPrice();
        String buyMessage = (buyPrice > 0.0D) ? plugin.getMessagesConfig().canBuy.replace("%buyAmount%", String.valueOf(buyPrice)) : plugin.getMessagesConfig().canNotBuy;
        String sellMessage = (buyPrice > 0.0D) ? plugin.getMessagesConfig().canSell.replace("%sellAmount%", String.valueOf(sellPrice)) : plugin.getMessagesConfig().canNotSell;
        plugin.debug("buy=" + buyPrice + "|sell=" + sellPrice);
        ChatUtil.sendPrefixedMessage(player, buyMessage);
        ChatUtil.sendPrefixedMessage(player, sellMessage);
    }


    private boolean hasVault(final Player player) {
        if (!plugin.isHasVault()) {
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().noVault));
            return false;
        }
        return true;
    }

    @Subcommand("sell")
    @CommandPermission("cards.sell")
    public class SellSubCommand extends BaseCommand {
        @Default
        @Description("Sells the card in your main hand.")
        public void onSell(final Player player) {
            if (!hasVault(player))
                return;

            if (player.getInventory().getItemInMainHand().getType() != Material.valueOf(plugin.getMainConfig().cardMaterial)) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().notACard);
                return;
            }


            final ItemStack itemInHand = player.getInventory().getItemInMainHand();
            final int itemInHandSlot = player.getInventory().getHeldItemSlot();
            final String[] splitName = ChatColor.stripColor(itemInHand.getItemMeta().getDisplayName()).split(" ");
            final String card = (splitName.length > 1) ? splitName[1] : splitName[0];

            plugin.debug(card);


            List<String> lore = itemInHand.getItemMeta().getLore();
            Validate.notNull(lore, "Lore cannot be null.");
            String rarity = ChatColor.stripColor(lore.get(lore.size() - 1));
            plugin.debug(rarity);

            if (cardManager.getCard(rarity, card, false).getSellPrice() == 0.0D) {
                if (card.contains(plugin.getMainConfig().shinyName))
                    ChatUtil.sendPrefixedMessage(player, "Cannot sell shiny card.");
                ChatUtil.sendPrefixedMessage(player, "Cannot sell this card.");
                return;
            }

            final double sellPrice = cardManager.getCard(rarity, card, false).getSellPrice();
            if (sellPrice == 0) {
                ChatUtil.sendPrefixedMessage(player, "Cannot sell this card.");
                return;
            }

            PlayerInventory inventory = player.getInventory();
            double sellAmount = sellPrice * itemInHand.getAmount();
            EconomyResponse economyResponse = plugin.getEcon().depositPlayer(player, sellAmount);
            if (economyResponse.transactionSuccess()) {
                ChatUtil.sendPrefixedMessage(player, String.format("You have sold %dx%s for %.2f", itemInHand.getAmount(), (rarity + " " + card), sellAmount));
                inventory.setItem(itemInHandSlot, null);
            }
        }
    }


    @Subcommand("buy")
    @CommandPermission("cards.buy")
    public class BuySubCommand extends BaseCommand {

        @Subcommand("pack|boosterpack")
        @CommandPermission("cards.buy.pack")
        @CommandCompletion("packs")
        @Description("Buy a pack.")
        public void onBuyPack(final Player player, final String name) {
            if (!hasVault(player))
                return;
            if (!plugin.getConfig().contains("BoosterPacks." + name)) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().packDoesntExist);
                return;
            }


            double buyPrice2 = plugin.getConfig().getDouble("BoosterPacks." + name + ".Price", 0.0D);

            if (buyPrice2 <= 0.0D) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().cannotBeBought);
                return;
            }

            EconomyResponse economyResponse = plugin.getEcon().withdrawPlayer(player, buyPrice2);
            if (economyResponse.transactionSuccess()) {
                if (plugin.getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {
                    plugin.getEcon().bankDeposit(plugin.getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
                }
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().boughtCard.replace("%amount%", String.valueOf(buyPrice2)));
                CardUtil.dropItem(player, plugin.getPackManager().getPackItem(name));
                return;
            }

            ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().notEnoughMoney);
        }


        @Subcommand("card")
        @CommandPermission("cards.buy.card")
        @Description("Buy a card.")
        @CommandCompletion("@rarities @cards")
        public void onBuyCard(final Player player, @NotNull final String rarity, @NotNull final String card) {
            if (!hasVault(player))
                return;

            if (cardManager.getCard(card, rarity, false).getCardName().equals("nullCard")) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().cardDoesntExist);
                return;
            }


            double buyPrice2 = cardManager.getCard(card, rarity, false).getBuyPrice();

            EconomyResponse economyResponse = plugin.getEcon().withdrawPlayer(player, buyPrice2);
            if (economyResponse.transactionSuccess()) {
                if (plugin.getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {
                    plugin.getEcon().bankDeposit(plugin.getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
                }
                CardUtil.dropItem(player, cardManager.getCard(card, rarity, false).build());
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().boughtCard.replace("%amount%", String.valueOf(buyPrice2)));
                return;
            }
            ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().notEnoughMoney);
        }
    }
}





