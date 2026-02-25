package net.tinetwork.tradingcards.tradingcardsplugin.collector.gui;

import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.deck.StorageEntry;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.collector.CollectorBookManager;
import net.tinetwork.tradingcards.tradingcardsplugin.config.settings.CollectorGuiConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
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

//todo, use triumph gui v4.x.x
public class CollectorBookGuiManager {
    // Unique identifier for collector inventories (stored in persistent data or used for tracking)
    public static final String COLLECTOR_GUI_IDENTIFIER = "TradingCards:CollectorBook";

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
    private final CollectorGuiConfig config;
    private final Map<UUID, ViewState> viewerState;

    public CollectorBookGuiManager(final @NotNull TradingCards plugin, final @NotNull CollectorGuiConfig config) {
        this.plugin = plugin;
        this.collectorBookManager = plugin.getCollectorBookManager();
        this.config = config;
        this.viewerState = new HashMap<>();
    }

    public void openMainMenu(final @NotNull Player player) {
        viewerState.put(player.getUniqueId(), new ViewState(ViewType.MENU, 0, null));
        player.openInventory(buildMainMenu());
    }

    public boolean isViewer(final @NotNull UUID playerUuid) {
        return viewerState.containsKey(playerUuid);
    }

    /**
     * Checks if an inventory title belongs to a collector GUI.
     * Uses a best-effort approach that works with fully custom titles.
     */
    public boolean isCollectorInventoryTitle(final @Nullable String inventoryTitle) {
        if (inventoryTitle == null) {
            return false;
        }
        // Check against all configured titles (with color codes stripped for comparison)
        final String stripped = ChatUtil.stripColor(inventoryTitle);
        final CollectorGuiConfig.MenuConfig menu = config.menuConfig();
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();

        // Check main menu title
        if (stripped.equals(ChatUtil.stripColor(menu.title()))) {
            return true;
        }
        // Check browser titles (without filter placeholders)
        if (stripped.startsWith(ChatUtil.stripColor(browser.raritiesTitle())) ||
            stripped.startsWith(ChatUtil.stripColor(browser.seriesTitle())) ||
            stripped.startsWith(ChatUtil.stripColor(browser.cardsAllTitle()))) {
            return true;
        }
        // Check filtered titles (prefix match only since filter varies)
        final String rarityPrefix = ChatUtil.stripColor(browser.cardsRarityTitle()).replace("%filter%", "");
        final String seriesPrefix = ChatUtil.stripColor(browser.cardsSeriesTitle()).replace("%filter%", "");
        if (stripped.startsWith(rarityPrefix) || stripped.startsWith(seriesPrefix)) {
            return true;
        }
        return false;
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
        final CollectorGuiConfig.MenuConfig menu = config.menuConfig();

        if (slot == menu.raritiesButton().slot()) {
            openCategoryList(player, ViewType.RARITY_LIST, 0);
            return;
        }

        if (slot == menu.allCardsButton().slot()) {
            openCardsList(player, ViewType.CARDS_ALL, null, 0);
            return;
        }

        if (slot == menu.seriesButton().slot()) {
            openCategoryList(player, ViewType.SERIES_LIST, 0);
            return;
        }

        if (slot == menu.closeButton().slot()) {
            player.closeInventory();
        }
    }

    private void handleCategoryClick(
            final @NotNull Player player,
            final @NotNull ViewState state,
            final int slot,
            final boolean rarityView
    ) {
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final CollectorGuiConfig.NavigationConfig nav = browser.navigation();
        final List<Integer> contentSlots = browser.contentSlots();
        final int pageSize = contentSlots.size();

        final List<CategoryEntry> categories = rarityView
                ? getRarityCategories(player.getUniqueId())
                : getSeriesCategories(player.getUniqueId());
        final int page = clampPage(state.page(), categories.size(), pageSize);

        if (slot == nav.previous().slot() && page > 0) {
            openCategoryList(player, state.type(), page - 1);
            return;
        }
        if (slot == nav.next().slot()) {
            openCategoryList(player, state.type(), page + 1);
            return;
        }
        if (slot == nav.back().slot()) {
            openMainMenu(player);
            return;
        }

        // Check if slot is in content slots
        final int contentIndex = contentSlots.indexOf(slot);
        if (contentIndex < 0) {
            return;
        }

        final int index = page * pageSize + contentIndex;
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
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final CollectorGuiConfig.NavigationConfig nav = browser.navigation();
        final List<Integer> contentSlots = browser.contentSlots();
        final int pageSize = contentSlots.size();

        final List<StorageEntry> entries = getFilteredEntries(player.getUniqueId(), state.type(), state.filterId());
        final int page = clampPage(state.page(), entries.size(), pageSize);
        final int slot = event.getRawSlot();

        if (slot == nav.previous().slot() && page > 0) {
            openCardsList(player, state.type(), state.filterId(), page - 1);
            return;
        }
        if (slot == nav.next().slot()) {
            openCardsList(player, state.type(), state.filterId(), page + 1);
            return;
        }
        if (slot == nav.back().slot()) {
            if (state.type() == ViewType.CARDS_RARITY) {
                openCategoryList(player, ViewType.RARITY_LIST, 0);
            } else if (state.type() == ViewType.CARDS_SERIES) {
                openCategoryList(player, ViewType.SERIES_LIST, 0);
            } else {
                openMainMenu(player);
            }
            return;
        }

        // Check if slot is in content slots
        final int contentIndex = contentSlots.indexOf(slot);
        if (contentIndex < 0) {
            return;
        }

        final int index = page * pageSize + contentIndex;
        if (index < 0 || index >= entries.size()) {
            return;
        }

        final StorageEntry selectedEntry = entries.get(index);
        if (event.isRightClick()) {
            final int requestedAmount = event.isShiftClick() ? Integer.MAX_VALUE : 1;
            final int depositedAmount = removeFromInventory(player, selectedEntry, requestedAmount);
            if (depositedAmount <= 0) {
                ChatUtil.sendPrefixedMessage(player, "No matching copies found in your inventory to deposit.");
                return;
            }

            collectorBookManager.addCard(
                    player.getUniqueId(),
                    selectedEntry.getCardId(),
                    selectedEntry.getRarityId(),
                    selectedEntry.getSeriesId(),
                    selectedEntry.isShiny(),
                    depositedAmount
            );
            ChatUtil.sendPrefixedMessage(player,
                    "Deposited %d %s (%s).".formatted(
                            depositedAmount,
                            selectedEntry.getCardId(),
                            selectedEntry.isShiny() ? "shiny" : "normal"));
            openCardsList(player, state.type(), state.filterId(), page);
            return;
        }

        if (!event.isLeftClick()) {
            return;
        }

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
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final List<Integer> contentSlots = browser.contentSlots();
        final int pageSize = contentSlots.size();

        final List<CategoryEntry> categories = categoryType == ViewType.RARITY_LIST
                ? getRarityCategories(player.getUniqueId())
                : getSeriesCategories(player.getUniqueId());

        final int page = clampPage(requestedPage, categories.size(), pageSize);
        final int startIndex = page * pageSize;
        final int endIndex = Math.min(categories.size(), startIndex + pageSize);

        final String title = categoryType == ViewType.RARITY_LIST
                ? ChatUtil.color(browser.raritiesTitle())
                : ChatUtil.color(browser.seriesTitle());
        final Inventory inventory = Bukkit.createInventory(player, browser.size(), title);

        for (int i = startIndex; i < endIndex; i++) {
            final CategoryEntry entry = categories.get(i);
            final int slotIndex = i - startIndex;
            if (slotIndex < contentSlots.size()) {
                inventory.setItem(contentSlots.get(slotIndex), buildCategoryItem(entry, categoryType == ViewType.RARITY_LIST));
            }
        }

        addNavigation(inventory, page, categories.size(), pageSize);
        viewerState.put(player.getUniqueId(), new ViewState(categoryType, page, null));
        player.openInventory(inventory);
    }

    private void openCardsList(
            final @NotNull Player player,
            final @NotNull ViewType cardViewType,
            final @Nullable String filterId,
            final int requestedPage
    ) {
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final List<Integer> contentSlots = browser.contentSlots();
        final int pageSize = contentSlots.size();

        final List<StorageEntry> entries = getFilteredEntries(player.getUniqueId(), cardViewType, filterId);
        final int page = clampPage(requestedPage, entries.size(), pageSize);
        final int startIndex = page * pageSize;
        final int endIndex = Math.min(entries.size(), startIndex + pageSize);

        final String title = switch (cardViewType) {
            case CARDS_RARITY -> ChatUtil.color(browser.cardsRarityTitle().replace("%filter%", filterId != null ? filterId : ""));
            case CARDS_SERIES -> ChatUtil.color(browser.cardsSeriesTitle().replace("%filter%", filterId != null ? filterId : ""));
            default -> ChatUtil.color(browser.cardsAllTitle());
        };
        final Inventory inventory = Bukkit.createInventory(player, browser.size(), title);

        for (int i = startIndex; i < endIndex; i++) {
            final StorageEntry entry = entries.get(i);
            final int slotIndex = i - startIndex;
            if (slotIndex < contentSlots.size()) {
                inventory.setItem(contentSlots.get(slotIndex), buildCardItem(entry));
            }
        }

        addNavigation(inventory, page, entries.size(), pageSize);
        viewerState.put(player.getUniqueId(), new ViewState(cardViewType, page, filterId));
        player.openInventory(inventory);
    }

    private @NotNull Inventory buildMainMenu() {
        final CollectorGuiConfig.MenuConfig menu = config.menuConfig();
        final Inventory inventory = Bukkit.createInventory(null, menu.size(), ChatUtil.color(menu.title()));

        // Set buttons at configured slots
        inventory.setItem(menu.raritiesButton().slot(), buildButtonItem(menu.raritiesButton()));
        inventory.setItem(menu.allCardsButton().slot(), buildButtonItem(menu.allCardsButton()));
        inventory.setItem(menu.seriesButton().slot(), buildButtonItem(menu.seriesButton()));
        inventory.setItem(menu.closeButton().slot(), buildButtonItem(menu.closeButton()));

        return inventory;
    }

    private @NotNull ItemStack buildButtonItem(final @NotNull CollectorGuiConfig.ButtonConfig button) {
        final ItemStack itemStack = new ItemStack(button.material());
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        itemMeta.setDisplayName(ChatUtil.color(button.name()));
        final List<String> coloredLore = new ArrayList<>();
        for (String line : button.lore()) {
            coloredLore.add(ChatUtil.color(line));
        }
        itemMeta.setLore(coloredLore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack buildCategoryItem(final @NotNull CategoryEntry entry, final boolean rarityView) {
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final Material material = rarityView ? browser.rarityCategoryMaterial() : browser.seriesCategoryMaterial();
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
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final TradingCard card = plugin.getCardManager().getCard(entry.getCardId(), entry.getRarityId(), entry.getSeriesId());

        if (card instanceof EmptyCard) {
            final ItemStack fallback = new ItemStack(browser.removedCardMaterial());
            final ItemMeta fallbackMeta = fallback.getItemMeta();
            if (fallbackMeta != null) {
                fallbackMeta.setDisplayName(ChatUtil.color(browser.removedCardName()));
                fallbackMeta.setLore(List.of(
                        ChatUtil.color("&7Card: &f" + entry.getCardId()),
                        ChatUtil.color("&7Rarity: &f" + entry.getRarityId()),
                        ChatUtil.color("&7Series: &f" + entry.getSeriesId()),
                        ChatUtil.color("&7Owned: &f" + entry.getAmount()),
                        ChatUtil.color("&7Version: &f" + (entry.isShiny() ? "Shiny" : "Normal")),
                        ChatUtil.color("&cCannot withdraw this card."),
                        ChatUtil.color("&eRight-Click: &fDeposit 1"),
                        ChatUtil.color("&eShift-Right: &fDeposit all from inventory")
                ));
                fallback.setItemMeta(fallbackMeta);
            }
            return fallback;
        }

        // Use card.build() so displayed cards match real items
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
        lore.add(ChatUtil.color("&eRight-Click: &fDeposit 1"));
        lore.add(ChatUtil.color("&eShift-Right: &fDeposit all from inventory"));
        itemMeta.setLore(lore);
        cardItem.setItemMeta(itemMeta);
        return cardItem;
    }

    private void addNavigation(final @NotNull Inventory inventory, final int page, final int totalEntries, final int pageSize) {
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final CollectorGuiConfig.NavigationConfig nav = browser.navigation();
        final int maxPage = Math.max(0, (totalEntries - 1) / pageSize);

        if (page > 0) {
            final CollectorGuiConfig.ButtonConfig prevButton = nav.previous();
            final List<String> prevLore = new ArrayList<>(prevButton.lore());
            prevLore.add("&7Go to page " + page);
            inventory.setItem(prevButton.slot(), buildButtonItem(new CollectorGuiConfig.ButtonConfig(
                    prevButton.slot(), prevButton.material(), prevButton.name(), prevLore)));
        }

        inventory.setItem(nav.back().slot(), buildButtonItem(nav.back()));

        if (page < maxPage) {
            final CollectorGuiConfig.ButtonConfig nextButton = nav.next();
            final List<String> nextLore = new ArrayList<>(nextButton.lore());
            nextLore.add("&7Go to page " + (page + 2));
            inventory.setItem(nextButton.slot(), buildButtonItem(new CollectorGuiConfig.ButtonConfig(
                    nextButton.slot(), nextButton.material(), nextButton.name(), nextLore)));
        }
    }

    private int clampPage(final int requestedPage, final int totalEntries, final int pageSize) {
        if (totalEntries <= 0) {
            return 0;
        }
        final int maxPage = Math.max(0, (totalEntries - 1) / pageSize);
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

    private int removeFromInventory(
            final @NotNull Player player,
            final @NotNull StorageEntry selectedEntry,
            final int requestedAmount
    ) {
        int remaining = requestedAmount;
        int removed = 0;
        for (int slot = 0; slot < player.getInventory().getStorageContents().length && remaining > 0; slot++) {
            final ItemStack itemStack = player.getInventory().getItem(slot);
            if (!isMatchingCard(itemStack, selectedEntry)) {
                continue;
            }

            final int toRemove = Math.min(remaining, itemStack.getAmount());
            removed += toRemove;
            remaining -= toRemove;
            if (toRemove >= itemStack.getAmount()) {
                player.getInventory().setItem(slot, null);
            } else {
                itemStack.setAmount(itemStack.getAmount() - toRemove);
            }
        }
        return removed;
    }

    private boolean isMatchingCard(final @Nullable ItemStack itemStack, final @NotNull StorageEntry selectedEntry) {
        if (itemStack == null || itemStack.getType().isAir() || !NbtUtils.Card.isCard(itemStack)) {
            return false;
        }

        final String cardId = NbtUtils.Card.getCardId(itemStack);
        final String rarityId = NbtUtils.Card.getRarityId(itemStack);
        final String seriesId = NbtUtils.Card.getSeriesId(itemStack);
        if (cardId == null || rarityId == null || seriesId == null) {
            return false;
        }

        return cardId.equals(selectedEntry.getCardId())
                && rarityId.equals(selectedEntry.getRarityId())
                && seriesId.equals(selectedEntry.getSeriesId())
                && NbtUtils.Card.isShiny(itemStack) == selectedEntry.isShiny();
    }
}
