package net.tinetwork.tradingcards.tradingcardsplugin.collector.gui;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.CollectorBookManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CollectorBookGuiManager {
    private static final String TITLE_PREFIX = "Collector";
    private static final int PAGE_SIZE = 45;
    private static final int PREV_SLOT = 45;
    private static final int BACK_SLOT = 49;
    private static final int NEXT_SLOT = 53;

    private enum ViewType {
        MENU,
        RARITY_LIST,
        SERIES_LIST,
        CARDS_ALL,
        CARDS_RARITY,
        CARDS_SERIES
    }

    private record ViewState(ViewType type, int page, @Nullable String filterId) {}
    private record CategoryEntry(String id, String displayName, int uniqueCards, int totalCopies) {}

    private final TradingCards plugin;
    private final CollectorBookManager collectorBookManager;
    private final Map<UUID, ViewState> viewerState;

    public CollectorBookGuiManager(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
        this.collectorBookManager = plugin.getCollectorBookManager();
        this.viewerState = new HashMap<>();
    }

    public void openMainMenu(final @NotNull Player player) {
        viewerState.put(player.getUniqueId(), new ViewState(ViewType.MENU, 0, null));
        player.openInventory(buildMainMenu());
    }

    public boolean isViewer(final @NotNull UUID playerUuid) {
        return viewerState.containsKey(playerUuid);
    }

    public boolean isCollectorInventoryTitle(final @Nullable String inventoryTitle) {
        if (inventoryTitle == null) {
            return false;
        }
        return ChatColor.stripColor(inventoryTitle).startsWith(TITLE_PREFIX);
    }

    public void removeViewer(final @NotNull UUID playerUuid) {
        viewerState.remove(playerUuid);
    }

    public void handleClick(final @NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        final UUID playerUuid = player.getUniqueId();
        final ViewState viewState = viewerState.get(playerUuid);
        if (viewState == null) {
            return;
        }

        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        switch (viewState.type()) {
            case MENU -> handleMenuClick(player, event.getRawSlot());
            case RARITY_LIST -> handleCategoryClick(player, viewState, event.getRawSlot(), true);
            case SERIES_LIST -> handleCategoryClick(player, viewState, event.getRawSlot(), false);
            case CARDS_ALL, CARDS_RARITY, CARDS_SERIES -> handleCardsClick(player, viewState, event);
        }
    }

    private void handleMenuClick(final @NotNull Player player, final int slot) {
        if (slot == 11) {
            openCategoryList(player, ViewType.RARITY_LIST, 0);
            return;
        }

        if (slot == 13) {
            openCardsList(player, ViewType.CARDS_ALL, null, 0);
            return;
        }

        if (slot == 15) {
            openCategoryList(player, ViewType.SERIES_LIST, 0);
            return;
        }

        if (slot == 22) {
            player.closeInventory();
        }
    }

    private void handleCategoryClick(
            final @NotNull Player player,
            final @NotNull ViewState state,
            final int slot,
            final boolean rarityView
    ) {
        final List<CategoryEntry> categories = rarityView
                ? getRarityCategories(player.getUniqueId())
                : getSeriesCategories(player.getUniqueId());
        final int page = clampPage(state.page(), categories.size());

        if (slot == PREV_SLOT) {
            openCategoryList(player, state.type(), Math.max(0, page - 1));
            return;
        }
        if (slot == NEXT_SLOT) {
            openCategoryList(player, state.type(), page + 1);
            return;
        }
        if (slot == BACK_SLOT) {
            openMainMenu(player);
            return;
        }

        if (slot < 0 || slot >= PAGE_SIZE) {
            return;
        }

        final int index = page * PAGE_SIZE + slot;
        if (index < 0 || index >= categories.size()) {
            return;
        }

        final CategoryEntry category = categories.get(index);
        if (rarityView) {
            openCardsList(player, ViewType.CARDS_RARITY, category.id(), 0);
        } else {
            openCardsList(player, ViewType.CARDS_SERIES, category.id(), 0);
        }
    }

    private void handleCardsClick(final @NotNull Player player, final @NotNull ViewState state, final @NotNull InventoryClickEvent event) {
        final List<StorageEntry> entries = getFilteredEntries(player.getUniqueId(), state.type(), state.filterId());
        final int page = clampPage(state.page(), entries.size());
        final int slot = event.getRawSlot();

        if (slot == PREV_SLOT) {
            openCardsList(player, state.type(), state.filterId(), Math.max(0, page - 1));
            return;
        }
        if (slot == NEXT_SLOT) {
            openCardsList(player, state.type(), state.filterId(), page + 1);
            return;
        }
        if (slot == BACK_SLOT) {
            if (state.type() == ViewType.CARDS_RARITY) {
                openCategoryList(player, ViewType.RARITY_LIST, 0);
            } else if (state.type() == ViewType.CARDS_SERIES) {
                openCategoryList(player, ViewType.SERIES_LIST, 0);
            } else {
                openMainMenu(player);
            }
            return;
        }

        if (slot < 0 || slot >= PAGE_SIZE) {
            return;
        }

        final int index = page * PAGE_SIZE + slot;
        if (index < 0 || index >= entries.size()) {
            return;
        }

        final StorageEntry selectedEntry = entries.get(index);
        final TradingCard card = plugin.getCardManager().getCard(selectedEntry.getCardId(), selectedEntry.getRarityId(), selectedEntry.getSeriesId());
        if (card instanceof EmptyCard) {
            ChatUtil.sendPrefixedMessage(player, "This card no longer exists in config and cannot be withdrawn.");
            return;
        }

        final int requestedAmount = event.isShiftClick() ? 64 : 1;
        final int removedAmount = collectorBookManager.removeCard(
                player.getUniqueId(),
                selectedEntry.getCardId(),
                selectedEntry.getRarityId(),
                selectedEntry.getSeriesId(),
                selectedEntry.isShiny(),
                requestedAmount
        );

        if (removedAmount <= 0) {
            ChatUtil.sendPrefixedMessage(player, "No copies available to withdraw.");
            return;
        }

        final ItemStack withdrawnCard = card.build(selectedEntry.isShiny());
        withdrawnCard.setAmount(removedAmount);
        givePhysicalItem(player, withdrawnCard);
        ChatUtil.sendPrefixedMessage(player,
                "Withdrew %d %s (%s).".formatted(
                        removedAmount,
                        selectedEntry.getCardId(),
                        selectedEntry.isShiny() ? "shiny" : "normal"));

        openCardsList(player, state.type(), state.filterId(), page);
    }

    private void openCategoryList(final @NotNull Player player, final @NotNull ViewType categoryType, final int requestedPage) {
        final List<CategoryEntry> categories = categoryType == ViewType.RARITY_LIST
                ? getRarityCategories(player.getUniqueId())
                : getSeriesCategories(player.getUniqueId());

        final int page = clampPage(requestedPage, categories.size());
        final int startIndex = page * PAGE_SIZE;
        final int endIndex = Math.min(categories.size(), startIndex + PAGE_SIZE);

        final String title = categoryType == ViewType.RARITY_LIST
                ? ChatUtil.color("&8Collector Book - Rarities")
                : ChatUtil.color("&8Collector Book - Series");
        final Inventory inventory = Bukkit.createInventory(player, 54, title);

        for (int i = startIndex; i < endIndex; i++) {
            final CategoryEntry entry = categories.get(i);
            final int slot = i - startIndex;
            inventory.setItem(slot, buildCategoryItem(entry, categoryType == ViewType.RARITY_LIST));
        }

        addNavigation(inventory, page, categories.size());
        viewerState.put(player.getUniqueId(), new ViewState(categoryType, page, null));
        player.openInventory(inventory);
    }

    private void openCardsList(
            final @NotNull Player player,
            final @NotNull ViewType cardViewType,
            final @Nullable String filterId,
            final int requestedPage
    ) {
        final List<StorageEntry> entries = getFilteredEntries(player.getUniqueId(), cardViewType, filterId);
        final int page = clampPage(requestedPage, entries.size());
        final int startIndex = page * PAGE_SIZE;
        final int endIndex = Math.min(entries.size(), startIndex + PAGE_SIZE);

        final String title = switch (cardViewType) {
            case CARDS_RARITY -> ChatUtil.color("&8Collector - Rarity: " + filterId);
            case CARDS_SERIES -> ChatUtil.color("&8Collector - Series: " + filterId);
            default -> ChatUtil.color("&8Collector Book - All Cards");
        };
        final Inventory inventory = Bukkit.createInventory(player, 54, title);

        for (int i = startIndex; i < endIndex; i++) {
            final StorageEntry entry = entries.get(i);
            final int slot = i - startIndex;
            inventory.setItem(slot, buildCardItem(entry));
        }

        addNavigation(inventory, page, entries.size());
        viewerState.put(player.getUniqueId(), new ViewState(cardViewType, page, filterId));
        player.openInventory(inventory);
    }

    private @NotNull Inventory buildMainMenu() {
        final Inventory inventory = Bukkit.createInventory(null, 27, ChatUtil.color("&8Collector Book"));
        inventory.setItem(11, buildMenuItem(Material.AMETHYST_SHARD, "&dBrowse Rarities", "&7View collected cards by rarity."));
        inventory.setItem(13, buildMenuItem(Material.CHEST, "&eBrowse All Cards", "&7View all collected cards."));
        inventory.setItem(15, buildMenuItem(Material.BOOK, "&bBrowse Series", "&7View collected cards by series."));
        inventory.setItem(22, buildMenuItem(Material.BARRIER, "&cClose", "&7Close this menu."));
        return inventory;
    }

    private @NotNull ItemStack buildMenuItem(final @NotNull Material material, final @NotNull String displayName, final @NotNull String loreLine) {
        final ItemStack itemStack = new ItemStack(material);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        itemMeta.setDisplayName(ChatUtil.color(displayName));
        itemMeta.setLore(List.of(ChatUtil.color(loreLine)));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack buildCategoryItem(final @NotNull CategoryEntry entry, final boolean rarityView) {
        final Material material = rarityView ? Material.AMETHYST_CLUSTER : Material.BOOK;
        final ItemStack itemStack = new ItemStack(material);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        itemMeta.setDisplayName(ChatUtil.color("&e" + entry.displayName().replace("_", " ")));
        itemMeta.setLore(List.of(
                ChatUtil.color("&7Id: &f" + entry.id()),
                ChatUtil.color("&7Unique Cards: &f" + entry.uniqueCards()),
                ChatUtil.color("&7Total Copies: &f" + entry.totalCopies()),
                ChatUtil.color("&eClick to open")
        ));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack buildCardItem(final @NotNull StorageEntry entry) {
        final TradingCard card = plugin.getCardManager().getCard(entry.getCardId(), entry.getRarityId(), entry.getSeriesId());
        if (card instanceof EmptyCard) {
            final ItemStack fallback = new ItemStack(Material.BARRIER);
            final ItemMeta fallbackMeta = fallback.getItemMeta();
            if (fallbackMeta != null) {
                fallbackMeta.setDisplayName(ChatUtil.color("&cRemoved Card"));
                fallbackMeta.setLore(List.of(
                        ChatUtil.color("&7Card: &f" + entry.getCardId()),
                        ChatUtil.color("&7Rarity: &f" + entry.getRarityId()),
                        ChatUtil.color("&7Series: &f" + entry.getSeriesId()),
                        ChatUtil.color("&7Owned: &f" + entry.getAmount()),
                        ChatUtil.color("&7Version: &f" + (entry.isShiny() ? "Shiny" : "Normal")),
                        ChatUtil.color("&cCannot withdraw this card.")
                ));
                fallback.setItemMeta(fallbackMeta);
            }
            return fallback;
        }

        final ItemStack cardItem = card.build(entry.isShiny());
        cardItem.setAmount(Math.max(1, Math.min(64, entry.getAmount())));
        final ItemMeta itemMeta = cardItem.getItemMeta();
        if (itemMeta == null) {
            return cardItem;
        }

        final List<String> lore = new ArrayList<>();
        if (itemMeta.getLore() != null) {
            lore.addAll(itemMeta.getLore());
        }
        lore.add(ChatUtil.color("&8Collector"));
        lore.add(ChatUtil.color("&7Owned: &f" + entry.getAmount()));
        lore.add(ChatUtil.color("&7Version: &f" + (entry.isShiny() ? "Shiny" : "Normal")));
        lore.add(ChatUtil.color("&eLeft-Click: &fWithdraw 1"));
        lore.add(ChatUtil.color("&eShift-Left: &fWithdraw up to 64"));
        itemMeta.setLore(lore);
        cardItem.setItemMeta(itemMeta);
        return cardItem;
    }

    private void addNavigation(final @NotNull Inventory inventory, final int page, final int totalEntries) {
        final int maxPage = Math.max(0, (totalEntries - 1) / PAGE_SIZE);
        if (page > 0) {
            inventory.setItem(PREV_SLOT, buildMenuItem(Material.ARROW, "&ePrevious Page", "&7Go to page " + page));
        }
        inventory.setItem(BACK_SLOT, buildMenuItem(Material.BARRIER, "&cBack", "&7Return to previous menu."));
        if (page < maxPage) {
            inventory.setItem(NEXT_SLOT, buildMenuItem(Material.ARROW, "&eNext Page", "&7Go to page " + (page + 2)));
        }
    }

    private int clampPage(final int requestedPage, final int totalEntries) {
        if (totalEntries <= 0) {
            return 0;
        }
        final int maxPage = Math.max(0, (totalEntries - 1) / PAGE_SIZE);
        return Math.max(0, Math.min(requestedPage, maxPage));
    }

    private @NotNull List<CategoryEntry> getRarityCategories(final @NotNull UUID playerUuid) {
        final List<StorageEntry> entries = collectorBookManager.getOwnedEntries(playerUuid);
        final List<CategoryEntry> categories = new ArrayList<>();
        for (Rarity rarity : plugin.getRarityManager().getRarities()) {
            int unique = 0;
            int copies = 0;
            for (StorageEntry entry : entries) {
                if (entry.getAmount() <= 0 || !rarity.getId().equals(entry.getRarityId())) {
                    continue;
                }
                unique++;
                copies += entry.getAmount();
            }
            categories.add(new CategoryEntry(rarity.getId(), rarity.getDisplayName(), unique, copies));
        }
        return categories;
    }

    private @NotNull List<CategoryEntry> getSeriesCategories(final @NotNull UUID playerUuid) {
        final List<StorageEntry> entries = collectorBookManager.getOwnedEntries(playerUuid);
        final List<CategoryEntry> categories = new ArrayList<>();
        final List<Series> seriesList = new ArrayList<>(plugin.getSeriesManager().getAllSeries());
        seriesList.sort(Comparator.comparing(Series::getId));

        for (Series series : seriesList) {
            int unique = 0;
            int copies = 0;
            for (StorageEntry entry : entries) {
                if (entry.getAmount() <= 0 || !series.getId().equals(entry.getSeriesId())) {
                    continue;
                }
                unique++;
                copies += entry.getAmount();
            }
            categories.add(new CategoryEntry(series.getId(), series.getDisplayName(), unique, copies));
        }
        return categories;
    }

    private @NotNull List<StorageEntry> getFilteredEntries(
            final @NotNull UUID playerUuid,
            final @NotNull ViewType viewType,
            final @Nullable String filterId
    ) {
        final List<StorageEntry> entries = collectorBookManager.getOwnedEntries(playerUuid).stream()
                .filter(entry -> entry.getAmount() > 0)
                .toList();

        final List<StorageEntry> filtered = new ArrayList<>();
        for (StorageEntry entry : entries) {
            if (viewType == ViewType.CARDS_RARITY && filterId != null && !filterId.equals(entry.getRarityId())) {
                continue;
            }
            if (viewType == ViewType.CARDS_SERIES && filterId != null && !filterId.equals(entry.getSeriesId())) {
                continue;
            }
            filtered.add(entry);
        }

        filtered.sort(Comparator
                .comparing(StorageEntry::getRarityId)
                .thenComparing(StorageEntry::getSeriesId)
                .thenComparing(StorageEntry::getCardId)
                .thenComparing(StorageEntry::isShiny));
        return filtered;
    }

    private void givePhysicalItem(final @NotNull Player player, final @NotNull ItemStack itemStack) {
        final Map<Integer, ItemStack> leftovers = player.getInventory().addItem(itemStack);
        for (ItemStack leftover : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }
}
