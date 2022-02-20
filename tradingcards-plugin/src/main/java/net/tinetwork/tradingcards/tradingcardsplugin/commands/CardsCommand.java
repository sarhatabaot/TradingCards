package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.tr7zw.nbtapi.NBTItem;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ExcludeFileFilter;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tinetwork.tradingcards.api.addons.TradingCardsAddon;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.deck.Deck;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.MessagesConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.StorageType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.YamlStorage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import net.tinetwork.tradingcards.tradingcardsplugin.whitelist.PlayerBlacklist;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias("cards")
public class CardsCommand extends BaseCommand {
    private final TradingCards plugin;
    private final TradingCardManager cardManager;
    private final TradingDeckManager deckManager;
    private final PlayerBlacklist playerBlacklist;

    private final MessagesConfig messagesConfig;

    private static final String PLAYER_NOT_ONLINE = "This player is not online. Or doesn't exist.";
    private static final String CANNOT_SELL_CARD = "Cannot sell this card.";

    public CardsCommand(final @NotNull TradingCards plugin, final PlayerBlacklist playerBlacklist) {
        this.plugin = plugin;
        this.playerBlacklist = playerBlacklist;
        this.cardManager = plugin.getCardManager();
        this.deckManager = plugin.getDeckManager();
        this.messagesConfig = plugin.getMessagesConfig();
    }

    private void debug(final String message) {
        plugin.debug(getClass(), message);
    }

    @CatchUnknown
    @HelpCommand
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("version|ver")
    @CommandPermission(Permissions.VERSION)
    @Description("Show the plugin version.")
    public void onVersion(final CommandSender sender) {
        final String format = "%s %s API-%s";
        ChatUtil.sendMessage(sender, String.format(format, plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAPIVersion()));
    }

    @Subcommand("reload")
    @CommandPermission(Permissions.RELOAD)
    @Description("Reloads all the configs.")
    public void onReload(final CommandSender sender) {
        ChatUtil.sendPrefixedMessage(sender, messagesConfig.reload());
        plugin.reloadPlugin();
    }


    @Subcommand("resolve")
    @CommandPermission(Permissions.RESOLVE)
    @Description("Shows a player's uuid")
    public void onResolve(final CommandSender sender, final @NotNull Player player) {
        ChatUtil.sendMessage(sender, plugin.getMessagesConfig().resolveMsg().replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString()));
    }

    @Subcommand("toggle")
    @CommandPermission(Permissions.TOGGLE)
    @Description("Toggles card drops from mobs.")
    public void onToggle(final Player player) {
        if (playerBlacklist.isAllowed(player)) {
            playerBlacklist.add(player);
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleDisabled()));
        } else {
            playerBlacklist.remove(player);
            ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().toggleEnabled()));
        }
    }

    @Subcommand("give")
    @CommandPermission(Permissions.GIVE)
    public class GiveCommands extends BaseCommand {

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
            public void onPlayer(final CommandSender sender, @Single final String playerName, @Single final String rarity, @Single final String cardName, @Single final boolean shiny) {
                TradingCard card = cardManager.getCard(cardName, rarity, shiny);
                if (shiny && !card.hasShiny()) {
                    ChatUtil.sendPrefixedMessage(sender, "This card does not have a shiny version.");
                    return;
                }

                if (card instanceof EmptyCard) {
                    ChatUtil.sendPrefixedMessage(sender, messagesConfig.noCard());
                    return;
                }

                Player target = Bukkit.getPlayerExact(playerName);
                if (isOnline(target)) {
                    ChatUtil.sendPrefixedMessage(sender, PLAYER_NOT_ONLINE);
                    return;
                }


                ChatUtil.sendPrefixedMessage(target, messagesConfig.giveCard()
                        .replace("%player%", target.getName())
                        .replace("%card%", rarity + " " + cardName));

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
                ChatUtil.sendPrefixedMessage(sender, PLAYER_NOT_ONLINE);
                return;
            }

            CardUtil.dropItem(player, plugin.getPackManager().getPackItem(pack));

            ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().givePack().replace("%player%", player.getName()).replace("%pack%", pack));
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
                ChatUtil.sendPrefixedMessage(sender, PLAYER_NOT_ONLINE);
                return;
            }

            try {
                String rare = cardManager.getRandomRarity(CardUtil.getMobType(entityType), true);
                plugin.debug(getClass(), "Rarity: " + rare);
                ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().giveRandomCardMsg().replace("%player%", player.getName()));
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
                ChatUtil.sendPrefixedMessage(sender, PLAYER_NOT_ONLINE);
                return;
            }

            try {
                debug("Rarity: " + rarity);
                ChatUtil.sendPrefixedMessage(sender, plugin.getMessagesConfig().giveRandomCardMsg().replace("%player%", player.getName()));
                CardUtil.dropItem(player, plugin.getCardManager().getRandomCard(rarity).build(false));
            } catch (IllegalArgumentException exception) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().noEntity());
            }
        }
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
                ChatUtil.sendPrefixedMessage(sender, PLAYER_NOT_ONLINE);
                return;
            }
            if (rarity == null) {
                final String sectionFormat = String.format(messagesConfig.sectionFormatPlayer(), target.getName());
                ChatUtil.sendMessage(sender, String.format(sectionFormat, target.getName()));
                for (Rarity rarityKey : plugin.getRarityManager().getRarities()) {
                    listRarity(sender, target, rarityKey.getName());
                }
                return;
            }

            if (!plugin.getRarityManager().containsRarity(rarity)) {
                ChatUtil.sendMessage(sender, messagesConfig.noRarity());
                return;
            }

            listRarity(sender, target, rarity);
        }

        private boolean canBuyPack(final String name) {
            Pack pack = plugin.getPackManager().getPack(name);
            return plugin.getGeneralConfig().vaultEnabled() && pack.getPrice() > 0.0D;
        }

        @Subcommand("pack")
        @CommandPermission(Permissions.LIST_PACK)
        @Description("Lists all packs.")
        public void onListPack(final CommandSender sender) {
            int lineNumber = 0;
            ChatUtil.sendMessage(sender, plugin.getMessagesConfig().packSection());

            for (String packName : plugin.getPackManager().getCachedPacksItemstacks().keySet()) {
                Pack pack = plugin.getPackManager().getPack(packName);
                ++lineNumber;
                if (canBuyPack(packName)) {
                    ChatUtil.sendMessage(sender, "&6" + lineNumber + ") &e" + pack.getDisplayName() + " &7(&aPrice: " + pack.getPrice() + "&7)");
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
            if(rarityCardListName == null || rarityCardListName.isEmpty())
                return "";

            for (final String cardId : plugin.getCardManager().getRarityCardListNames(rarityId)) {
                debug("rarityId=" + rarityId + ",cardId=" + cardId);
                TradingCard card = getCard(cardId, rarityId);
                debug(card.toString());

                final String color = plugin.getGeneralConfig().colorListHaveCard();
                final String shinyColor = plugin.getGeneralConfig().colorListHaveCardShiny();

                stringBuilder.append(prefix);
                if (deckManager.hasShinyCard(target, cardId, rarityId)) {
                    stringBuilder.append(shinyColor)
                            .append(card.getDisplayName().replace("_", " "));
                } else if (deckManager.hasCard(target, cardId, rarityId)) {
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
            debug(rarityId);
            final Rarity rarityObject = plugin.getRarityManager().getRarity(rarityId);

            final String sectionFormat = messagesConfig.sectionFormat();
            final String sectionFormatComplete = messagesConfig.sectionFormatComplete();

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
            if(rarityCardList == null || rarityCardList.isEmpty())
                return 0;
            return rarityCardList.size();
        }

        //Counts the total amount of cards a player has from a rarity
        //TODO, this should be done via the storage impl, since this can be done easily through sql
        private int countPlayerCardsInRarity(final Player player, final String rarity) {
            final List<String> rarityCardList = plugin.getCardManager().getRarityCardListNames(rarity);
            int cardCounter = 0;
            if(rarityCardList == null || rarityCardList.isEmpty())
                return cardCounter;

            for (String cardId : rarityCardList) {
                if (deckManager.hasCard(player, cardId, rarity)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }
        //TODO, this should be done via the storage impl
        private int countShinyPlayerCardsInRarity(final Player player, final String rarity) {
            final List<String> rarityCardList = plugin.getCardManager().getRarityCardListNames(rarity);
            int cardCounter = 0;
            for (String cardId : rarityCardList) {
                if (deckManager.hasShinyCard(player, cardId, rarity)) {
                    cardCounter++;
                }
            }
            return cardCounter;
        }
    }


    private String getFormattedRarity(final String rarity) {
        for (final Rarity rarityKey : plugin.getRarityManager().getRarities()) {
            if (rarityKey.getName().equalsIgnoreCase(rarity.replace("_", " "))) {
                return rarityKey.getName();
            }
        }
        return "";
    }

    @Subcommand("giveaway rarity")
    @CommandPermission(Permissions.GIVEAWAY_RARITY)
    @Description("Give away a random card by rarity to the server.")
    @CommandCompletion("@rarities")
    public void onGiveawayRarity(final CommandSender sender, final String rarity) {
        if (plugin.getRarityManager().getRarity(rarity) == null) {
            ChatUtil.sendPrefixedMessage(sender, messagesConfig.noRarity());
            return;
        }

        Bukkit.broadcastMessage(plugin.getPrefixedMessage(messagesConfig.giveaway().replace("%player%", sender.getName()).replace("%rarity%", getFormattedRarity(rarity))));
        for (final Player p5 : Bukkit.getOnlinePlayers()) {
            CardUtil.dropItem(p5, cardManager.getRandomCard(rarity).build(false));
        }
    }

    @Subcommand("giveaway entity")
    @CommandPermission(Permissions.GIVEAWAY_ENTITY)
    @Description("Give away a random card by entity to the server.")
    public void onGiveawayMob(final CommandSender sender, final String entity) {
        if (plugin.isMob(entity)) {
            if (sender instanceof ConsoleCommandSender) {
                CardUtil.giveawayNatural(EntityType.valueOf(entity.toUpperCase()), null);
            } else {
                CardUtil.giveawayNatural(EntityType.valueOf(entity.toUpperCase()), (Player) sender);
            }
        }
    }

    @Subcommand("worth")
    @CommandPermission(Permissions.WORTH)
    @Description("Shows a card's worth.")
    public void onWorth(final Player player) {
        if (!hasVault(player)) {
            return;
        }
        final NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
        if (!CardUtil.isCard(nbtItem)) {
            ChatUtil.sendPrefixedMessage(player, messagesConfig.notACard());
            return;
        }

        final String cardId = nbtItem.getString(NbtUtils.NBT_CARD_NAME);
        final String rarityId = nbtItem.getString(NbtUtils.NBT_RARITY);
        debug("Card name=" + cardId + ", Card rarity=" + rarityId);

        final TradingCard tradingCard = cardManager.getCard(cardId, rarityId, false);
        final double buyPrice = tradingCard.getBuyPrice();
        final double sellPrice = tradingCard.getSellPrice();

        final String buyMessage = (buyPrice > 0.0D) ? messagesConfig.canBuy().replace("%buyAmount%", String.valueOf(buyPrice)) : messagesConfig.canNotBuy();
        final String sellMessage = (sellPrice > 0.0D) ? messagesConfig.canSell().replace("%sellAmount%", String.valueOf(sellPrice)) : messagesConfig.canNotSell();
        debug("buy=" + buyPrice + "|sell=" + sellPrice);
        ChatUtil.sendPrefixedMessage(player, buyMessage);
        ChatUtil.sendPrefixedMessage(player, sellMessage);
    }


    private boolean hasVault(final CommandSender player) {
        if (!plugin.isHasVault()) {
            ChatUtil.sendPrefixedMessage(player, messagesConfig.noVault());
            return false;
        }
        return true;
    }

    @Subcommand("sell")
    @CommandPermission(Permissions.SELL)
    public class SellSubCommand extends BaseCommand {
        @Default
        @Description("Sells the card in your main hand.")
        public void onSell(final Player player) {
            if (!hasVault(player))
                return;

            final NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
            if (!CardUtil.isCard(nbtItem)) {
                ChatUtil.sendPrefixedMessage(player, messagesConfig.notACard());
                return;
            }

            final ItemStack itemInHand = player.getInventory().getItemInMainHand();
            final int itemInHandSlot = player.getInventory().getHeldItemSlot();
            final String cardId = nbtItem.getString(NbtUtils.NBT_CARD_NAME);
            final String rarityId = nbtItem.getString(NbtUtils.NBT_RARITY);
            debug("Card name=" + cardId + ", Card rarity=" + rarityId);

            final TradingCard tradingCard = cardManager.getCard(cardId, rarityId, false);
            if (tradingCard.isShiny()) {
                ChatUtil.sendPrefixedMessage(player, "Cannot sell shiny card.");
                return;
            }

            if (tradingCard.getSellPrice() <= 0.00D) {
                ChatUtil.sendPrefixedMessage(player, CANNOT_SELL_CARD);
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

    @Subcommand("buy")
    @CommandPermission(Permissions.BUY)
    public class BuySubCommand extends BaseCommand {
        @Subcommand("pack")
        @CommandPermission(Permissions.BUY_PACK)
        @CommandCompletion("@packs")
        @Description("Buy a pack.")
        public void onBuyPack(final Player player, final String name) {
            if (!hasVault(player))
                return;
            if (plugin.getPackManager().getPack(name) == null) {
                ChatUtil.sendPrefixedMessage(player, messagesConfig.packDoesntExist());
                return;
            }

            Pack pack = plugin.getPackManager().getPack(name);

            if (pack.getPrice() <= 0.0D) {
                ChatUtil.sendPrefixedMessage(player, messagesConfig.cannotBeBought());
                return;
            }

            EconomyResponse economyResponse = plugin.getEcon().withdrawPlayer(player, pack.getPrice());
            if (economyResponse.transactionSuccess()) {
                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEcon().bankDeposit(plugin.getGeneralConfig().serverAccount(), pack.getPrice());
                }
                ChatUtil.sendPrefixedMessage(player, messagesConfig.boughtCard().replace("%amount%", String.valueOf(pack.getPrice())));
                CardUtil.dropItem(player, plugin.getPackManager().getPackItem(name));
                return;
            }

            ChatUtil.sendPrefixedMessage(player, messagesConfig.notEnoughMoney());
        }


        @Subcommand("card")
        @CommandPermission(Permissions.BUY_CARD)
        @Description("Buy a card.")
        @CommandCompletion("@rarities @cards")
        public void onBuyCard(final Player player, @NotNull final String rarity, @NotNull final String card) {
            if (!hasVault(player))
                return;

            if (cardManager.getCard(card, rarity, false).getCardName().equals("nullCard")) {
                ChatUtil.sendPrefixedMessage(player, messagesConfig.cardDoesntExist());
                return;
            }

            final TradingCard tradingCard = cardManager.getCard(card, rarity, false);
            double buyPrice = tradingCard.getBuyPrice();

            EconomyResponse economyResponse = plugin.getEcon().withdrawPlayer(player, buyPrice);
            if (economyResponse.transactionSuccess()) {
                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEcon().bankDeposit(plugin.getGeneralConfig().serverAccount(), buyPrice);
                }
                CardUtil.dropItem(player, tradingCard.build(false));
                ChatUtil.sendPrefixedMessage(player, messagesConfig.boughtCard().replace("%amount%", String.valueOf(buyPrice)));
                return;
            }
            ChatUtil.sendPrefixedMessage(player, messagesConfig.notEnoughMoney());
        }
    }

    @Subcommand("debug")
    @CommandPermission("cards.admin.debug")
    public class DebugCommands extends BaseCommand {

        public class ZipBukkitRunnable extends BukkitRunnable {
            private final CommandSender sender;

            public ZipBukkitRunnable(final CommandSender sender) {
                this.sender = sender;
            }

            @Override
            public void run() {
                final String pluginFolder = plugin.getDataFolder().getPath();
                final String cardsFolder = pluginFolder + File.separator + "cards";
                final String dataFolder = pluginFolder + File.separator + "data";
                final String listsFolder = pluginFolder + File.separator + "lists";
                final File settingsFolder = new File(pluginFolder + File.separator + "settings");
                ExcludeFileFilter excludeFileFilter = file -> file.getName().contains("storage.yml");
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setExcludeFileFilter(excludeFileFilter);
                try (ZipFile zipFile = new ZipFile(pluginFolder + File.separator + "debug.zip")) {
                    zipFile.addFolder(new File(cardsFolder));
                    zipFile.addFolder(new File(dataFolder));
                    zipFile.addFolder(new File(listsFolder));
                    zipFile.addFolder(settingsFolder, zipParameters);
                    if (zipFile.getProgressMonitor().getResult().equals(ProgressMonitor.Result.SUCCESS)) {
                        sender.sendMessage("Added all settings files to debug.zip.");
                    }
                } catch (IOException e) {
                    Util.logWarningException(e);
                }

            }
        }

        @Subcommand("zip")
        @CommandPermission(Permissions.ADMIN_DEBUG_ZIP)
        @Description("Creates a zip of all settings.")
        public void onZip(final @NotNull CommandSender sender) {
            sender.sendMessage("Backing the settings folder to debug.zip");
            sender.sendMessage("This does not backup storage.yml.");

            new ZipBukkitRunnable(sender).runTask(plugin);
        }

        @Subcommand("showcache all")
        @CommandPermission(Permissions.ADMIN_DEBUG_SHOW_CACHE)
        @Description("Shows the card cache")
        public void showCacheAll(final @NotNull CommandSender sender) {
            sender.sendMessage(StringUtils.join(cardManager.getCards().keySet(), ","));
        }

        @Subcommand("showcache active")
        @CommandPermission(Permissions.ADMIN_DEBUG_SHOW_CACHE)
        @Description("Shows the card cache")
        public void showCacheActive(final @NotNull CommandSender sender) {
            sender.sendMessage(StringUtils.join(cardManager.getActiveCards(), ","));
        }

        @Subcommand("modules")
        @CommandPermission(Permissions.ADMIN_DEBUG_MODULES)
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

        @Subcommand("packs")
        @CommandPermission(Permissions.ADMIN_DEBUG_PACKS)
        @Description("Show all available packs.")
        public void onPack(final CommandSender sender) {
            sender.sendMessage(StringUtils.join(plugin.getPackManager().getPacks(), ","));
        }

        @Subcommand("rarities")
        @CommandPermission(Permissions.ADMIN_DEBUG_RARITIES)
        @Description("Shows available rarities.")
        public void onRarities(final CommandSender sender) {
            StringBuilder sb = new StringBuilder();
            for (Rarity rarity : plugin.getRarityManager().getRarities()) {
                sb.append(rarity.getName()).append(", ");
            }
            sender.sendMessage(sb.toString());
        }

        @Subcommand("exists")
        @CommandPermission(Permissions.ADMIN_DEBUG_EXISTS)
        @Description("Shows if a card exists or not.")
        public void onExists(final CommandSender sender, final String card, final String rarity) {
            if (cardManager.getCards().containsKey(rarity + "." + card)) {
                sender.sendMessage(String.format("Card %s.%s exists", rarity, card));
                return;
            }
            sender.sendMessage(String.format("Card %s.%s does not exist", rarity, card));
        }
    }

    @Subcommand("migrate")
    @CommandPermission(Permissions.ADMIN_MIGRATE)
    @Description("Migrates from yaml to another storage type.")
    public class MigrateSubCommand extends BaseCommand {

        @Default
        public void onMigrateInfo(final CommandSender sender) {
            if (plugin.getStorage().getType() == StorageType.YAML) {
                sender.sendMessage(ChatUtil.color("&4Cannot convert from YAML to YAML."));
                sender.sendMessage(ChatUtil.color("&4Please change your storage type to MYSQL or MARIADB & restart your server."));
                return;
            }

            sender.sendMessage(ChatUtil.color("&cAre you sure you want to migrate? This action is irreversible."));
            sender.sendMessage(ChatUtil.color("&cMake sure you have made a backup of your decks.yml before continuing."));
            sender.sendMessage(ChatUtil.color("&cIf you want to convert from YAML to " + plugin.getStorage().getType().name()));
            sender.sendMessage(ChatUtil.color("&cPlease type /cards migrate confirm"));
        }


        @Subcommand("confirm")
        public void onMigrateConfirm(final @NotNull CommandSender sender) {
            sender.sendMessage(ChatUtil.color("&2Started migration from YAML to " + plugin.getStorage().getType().name()));
            sender.sendMessage(ChatUtil.color("&2This may take a while..."));
            new MigratorBukkitRunnable(sender).runTask(plugin);
        }

        public class MigratorBukkitRunnable extends BukkitRunnable {
            private final CommandSender sender;

            public MigratorBukkitRunnable(final CommandSender sender) {
                this.sender = sender;
            }

            @Override
            public void run() {
                long startTime = System.nanoTime();
                try {
                    YamlStorage yamlStorage = new YamlStorage(plugin);
                    yamlStorage.init(plugin);
                    Map<UUID, List<Deck>> yamlDecks = yamlStorage.getAllDecks();
                    sender.sendMessage("Found " + yamlDecks.size() + " players.");

                    int totalDecks = yamlDecks.values().stream()
                            .mapToInt(Collection::size)
                            .sum();
                    sender.sendMessage("Total " + totalDecks + " decks.");

                    for (Map.Entry<UUID, List<Deck>> entry : yamlDecks.entrySet()) {
                        final UUID playerUuid = entry.getKey();
                        sender.sendMessage(ChatUtil.color("&2Started conversion for " + playerUuid));
                        for (Deck deck : entry.getValue()) {
                            plugin.getStorage().saveDeck(playerUuid, deck.getNumber(), deck);
                        }
                        sender.sendMessage(ChatUtil.color("&2Finished conversion for " + playerUuid + ", converted " + entry.getValue().size() + " decks."));
                    }
                    long endTime = System.nanoTime();
                    sender.sendMessage(ChatUtil.color("&2Finished conversion of " + totalDecks + " decks."));
                    long duration = (endTime - startTime) / 1000000;
                    sender.sendMessage(ChatUtil.color("&aTook a total of " + duration + "ms"));

                } catch (ConfigurateException e) {
                    sender.sendMessage("There was a problem accessing the yaml data. Check your console for more info.");
                    Util.logSevereException(e);
                } catch (Exception e) {
                    sender.sendMessage("There was an error. Check your console for more info.");
                    Util.logSevereException(e);
                }
            }
        }
    }



}





