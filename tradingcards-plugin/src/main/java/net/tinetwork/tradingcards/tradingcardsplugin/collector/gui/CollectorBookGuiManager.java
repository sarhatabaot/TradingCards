package net.tinetwork.tradingcards.tradingcardsplugin.collector.gui;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.click.GuiClick;
import dev.triumphteam.gui.click.action.SimpleGuiClickAction;
import dev.triumphteam.gui.element.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CollectorBookGuiManager {

    private enum ViewType {
        MENU, RARITY_LIST, SERIES_LIST, CARDS_ALL, CARDS_RARITY, CARDS_SERIES
    }

    private record CategoryEntry(String id, String displayName, int uniqueCards, int totalCopies) {}
    private record CategoryStats(int uniqueCards, int totalCopies) {}

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final TradingCards plugin;
    private final CollectorBookManager collectorBookManager;
    private final CollectorGuiConfig config;

    public CollectorBookGuiManager(final @NotNull TradingCards plugin, final @NotNull CollectorGuiConfig config) {
        this.plugin = plugin;
        this.collectorBookManager = plugin.getCollectorBookManager();
        this.config = config;
    }

    public void openMainMenu(final @NotNull Player player) {
        buildMainMenu().open(player);
    }

    private @NotNull Gui buildMainMenu() {
        final CollectorGuiConfig.MenuConfig menu = config.menuConfig();
        return Gui.of(menu.size() / 9)
                .title(MINI_MESSAGE.deserialize(menu.title()))
                .statelessComponent(container -> {
                    container.setItem(menu.raritiesButton().slot(),
                            buildButtonItem(menu.raritiesButton(), (p, ctx) ->
                                    buildCategoryList(p, ViewType.RARITY_LIST, 0).open(p)));
                    container.setItem(menu.allCardsButton().slot(),
                            buildButtonItem(menu.allCardsButton(), (p, ctx) ->
                                    buildCardsList(p, ViewType.CARDS_ALL, null, 0).open(p)));
                    container.setItem(menu.seriesButton().slot(),
                            buildButtonItem(menu.seriesButton(), (p, ctx) ->
                                    buildCategoryList(p, ViewType.SERIES_LIST, 0).open(p)));
                    container.setItem(menu.closeButton().slot(),
                            buildButtonItem(menu.closeButton(), (p, ctx) ->
                                    p.closeInventory()));
                })
                .build();
    }

    private @NotNull Gui buildCategoryList(
            final @NotNull Player player,
            final @NotNull ViewType categoryType,
            final int initialPage
    ) {
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final CollectorGuiConfig.NavigationConfig nav = browser.navigation();
        final List<Integer> contentSlots = browser.contentSlots();
        final int pageSize = contentSlots.size();
        final boolean isRarity = categoryType == ViewType.RARITY_LIST;

        if (pageSize == 0) {
            return buildInvalidBrowserGui(browser, browser.invalidContentSlotsMessage());
        }

        final List<CategoryEntry> categories = isRarity
                ? getRarityCategories(player)
                : getSeriesCategories(player);

        final String title = isRarity ? browser.raritiesTitle() : browser.seriesTitle();

        return Gui.of(browser.size() / 9)
                .title(MINI_MESSAGE.deserialize(title))
                .statelessComponent(container -> {
                    final int currentPage = clampPage(initialPage, categories.size(), pageSize);
                    final int startIndex = currentPage * pageSize;
                    final int maxPage = Math.max(0, (categories.size() - 1) / pageSize);

                    for (int i = startIndex; i < Math.min(categories.size(), startIndex + pageSize); i++) {
                        final CategoryEntry entry = categories.get(i);
                        final int slotIndex = i - startIndex;
                        container.setItem(contentSlots.get(slotIndex), buildCategoryItem(browser, isRarity, entry));
                    }

                    if (currentPage > 0) {
                        container.setItem(nav.previous().slot(),
                                buildButtonItem(nav.previous(), (p, ctx) ->
                                        buildCategoryList(p, categoryType, currentPage - 1).open(p)));
                    }
                    if (currentPage < maxPage) {
                        container.setItem(nav.next().slot(),
                                buildButtonItem(nav.next(), (p, ctx) ->
                                        buildCategoryList(p, categoryType, currentPage + 1).open(p)));
                    }
                    container.setItem(nav.back().slot(),
                            buildButtonItem(nav.back(), (p, ctx) -> buildMainMenu().open(p)));
                })
                .build();
    }

    private @NotNull Gui buildCardsList(
            final @NotNull Player player,
            final @NotNull ViewType viewType,
            final @Nullable String filterId,
            final int initialPage
    ) {
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final CollectorGuiConfig.NavigationConfig nav = browser.navigation();
        final List<Integer> contentSlots = browser.contentSlots();
        final int pageSize = contentSlots.size();

        if (pageSize <= 0) {
            return buildInvalidBrowserGui(browser, browser.invalidContentSlotsMessage());
        }

        final List<StorageEntry> entries = getFilteredEntries(player, viewType, filterId);

        final String title = switch (viewType) {
            case CARDS_RARITY -> browser.cardsRarityTitle().replace("%filter%", filterId != null ? filterId : "");
            case CARDS_SERIES -> browser.cardsSeriesTitle().replace("%filter%", filterId != null ? filterId : "");
            default -> browser.cardsAllTitle();
        };

        return Gui.of(browser.size() / 9)
                .title(MINI_MESSAGE.deserialize(title))
                .statelessComponent(container -> {
                    final int currentPage = clampPage(initialPage, entries.size(), pageSize);
                    final int startIndex = currentPage * pageSize;
                    final int maxPage = Math.max(0, (entries.size() - 1) / pageSize);

                    for (int i = startIndex; i < Math.min(entries.size(), startIndex + pageSize); i++) {
                        final StorageEntry entry = entries.get(i);
                        final int slotIndex = i - startIndex;
                        container.setItem(contentSlots.get(slotIndex), buildCardItem(entry, viewType, filterId, currentPage));
                    }

                    if (currentPage > 0) {
                        container.setItem(nav.previous().slot(),
                                buildButtonItem(nav.previous(), (p, ctx) ->
                                        buildCardsList(p, viewType, filterId, currentPage - 1).open(p)));
                    }
                    if (currentPage < maxPage) {
                        container.setItem(nav.next().slot(),
                                buildButtonItem(nav.next(), (p, ctx) ->
                                        buildCardsList(p, viewType, filterId, currentPage + 1).open(p)));
                    }
                    container.setItem(nav.back().slot(),
                            buildButtonItem(nav.back(), (p, ctx) -> {
                                if (viewType == ViewType.CARDS_RARITY) {
                                    buildCategoryList(p, ViewType.RARITY_LIST, 0).open(p);
                                } else if (viewType == ViewType.CARDS_SERIES) {
                                    buildCategoryList(p, ViewType.SERIES_LIST, 0).open(p);
                                } else {
                                    buildMainMenu().open(p);
                                }
                            }));
                })
                .build();
    }

    private GuiItem<Player, ItemStack> buildButtonItem(
            final @NotNull CollectorGuiConfig.ButtonConfig button,
            final @NotNull SimpleGuiClickAction<Player> action
    ) {
        return ItemBuilder.from(button.material())
                .name(MINI_MESSAGE.deserialize(button.name()))
                .lore(button.lore().stream().<Component>map(MINI_MESSAGE::deserialize).toList())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem(action);
    }

    private GuiItem<Player, ItemStack> buildCategoryItem(
            final @NotNull CollectorGuiConfig.BrowserConfig browser,
            final boolean isRarity,
            final @NotNull CategoryEntry entry
    ) {
        return ItemBuilder.from(isRarity ? browser.rarityCategoryMaterial() : browser.seriesCategoryMaterial())
                .name(MINI_MESSAGE.deserialize(replaceCategoryPlaceholders(browser.categoryEntryName(), entry)))
                .lore(browser.categoryEntryLore().stream()
                        .map(line -> replaceCategoryPlaceholders(line, entry))
                        .<Component>map(MINI_MESSAGE::deserialize)
                        .toList())
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem((player, ctx) ->
                        buildCardsList(player, isRarity ? ViewType.CARDS_RARITY : ViewType.CARDS_SERIES, entry.id(), 0).open(player));
    }

    private GuiItem<Player, ItemStack> buildCardItem(
            final @NotNull StorageEntry entry,
            final @NotNull ViewType viewType,
            final @Nullable String filterId,
            final int currentPage
    ) {
        final CollectorGuiConfig.BrowserConfig browser = config.browserConfig();
        final TradingCard card = plugin.getCardManager().getCard(entry.getCardId(), entry.getRarityId(), entry.getSeriesId());
        final ItemStack displayItem = buildDisplayItem(browser, entry, card);

        return ItemBuilder.from(displayItem).asGuiItem((p, ctx) -> {
            final GuiClick click = ctx.guiClick();
            if (click == GuiClick.RIGHT || click == GuiClick.SHIFT_RIGHT) {
                final int requested = click == GuiClick.SHIFT_RIGHT ? Integer.MAX_VALUE : 1;
                final int deposited = removeFromInventory(p, entry, requested);
                if (deposited <= 0) {
                    ChatUtil.sendPrefixedMessage(p, "No matching copies found in your inventory to deposit.");
                    return;
                }
                collectorBookManager.addCard(p.getUniqueId(),
                        entry.getCardId(), entry.getRarityId(), entry.getSeriesId(), entry.isShiny(), deposited);
                ChatUtil.sendPrefixedMessage(p, "Deposited %d %s (%s).".formatted(
                        deposited, entry.getCardId(), entry.isShiny() ? "shiny" : "normal"));
                buildCardsList(p, viewType, filterId, currentPage).open(p);
                return;
            }

            if (click != GuiClick.LEFT && click != GuiClick.SHIFT_LEFT) return;

            if (card instanceof EmptyCard) {
                ChatUtil.sendPrefixedMessage(p, "This card no longer exists in config and cannot be withdrawn.");
                return;
            }

            final int requested = click == GuiClick.SHIFT_LEFT ? 64 : 1;
            final int removed = collectorBookManager.removeCard(p.getUniqueId(),
                    entry.getCardId(), entry.getRarityId(), entry.getSeriesId(), entry.isShiny(), requested);
            if (removed <= 0) {
                ChatUtil.sendPrefixedMessage(p, "No copies available to withdraw.");
                return;
            }

            final ItemStack withdrawnCard = card.build(entry.isShiny());
            withdrawnCard.setAmount(removed);
            givePhysicalItem(p, withdrawnCard);
            ChatUtil.sendPrefixedMessage(p, "Withdrew %d %s (%s).".formatted(
                    removed, entry.getCardId(), entry.isShiny() ? "shiny" : "normal"));
            buildCardsList(p, viewType, filterId, currentPage).open(p);
        });
    }

    private @NotNull ItemStack buildDisplayItem(
            final @NotNull CollectorGuiConfig.BrowserConfig browser,
            final @NotNull StorageEntry entry,
            final @NotNull TradingCard card
    ) {
        if (card instanceof EmptyCard) {
            return buildRemovedCardDisplayItem(browser, entry);
        }
        return buildOwnedCardDisplayItem(entry, card);
    }

    private @NotNull ItemStack buildRemovedCardDisplayItem(
            final @NotNull CollectorGuiConfig.BrowserConfig browser,
            final @NotNull StorageEntry entry
    ) {
        final ItemStack displayItem = new ItemStack(browser.removedCardMaterial());
        final ItemMeta meta = displayItem.getItemMeta();
        if (meta == null) {
            return displayItem;
        }

        meta.setDisplayName(toLegacyString(browser.removedCardName()));
        meta.setLore(browser.removedCardLore().stream()
                .map(line -> replaceEntryPlaceholders(line, entry))
                .map(this::toLegacyString)
                .toList());
        displayItem.setItemMeta(meta);
        return displayItem;
    }

    private @NotNull ItemStack buildOwnedCardDisplayItem(
            final @NotNull StorageEntry entry,
            final @NotNull TradingCard card
    ) {
        final ItemStack displayItem = card.build(entry.isShiny());
        displayItem.setAmount(Math.clamp(entry.getAmount(), 1, 64));

        final ItemMeta meta = displayItem.getItemMeta();
        if (meta == null) {
            return displayItem;
        }

        final List<String> lore = new ArrayList<>(meta.getLore() != null ? meta.getLore() : List.of());
        lore.addAll(browserOwnedCardLore(entry));
        meta.setLore(lore);
        displayItem.setItemMeta(meta);
        return displayItem;
    }

    private @NotNull List<CategoryEntry> getRarityCategories(final @NotNull Player player) {
        final Map<String, CategoryStats> statsByRarity = getCategoryStatsBy(entries -> entries.getRarityId(), player);
        final List<CategoryEntry> categories = new ArrayList<>();
        for (final Rarity rarity : plugin.getRarityManager().getRarities()) {
            final CategoryStats stats = statsByRarity.getOrDefault(rarity.getId(), new CategoryStats(0, 0));
            categories.add(new CategoryEntry(rarity.getId(), rarity.getDisplayName(), stats.uniqueCards(), stats.totalCopies()));
        }
        return categories;
    }

    private @NotNull List<CategoryEntry> getSeriesCategories(final @NotNull Player player) {
        final Map<String, CategoryStats> statsBySeries = getCategoryStatsBy(entries -> entries.getSeriesId(), player);
        final List<CategoryEntry> categories = new ArrayList<>();
        final List<Series> seriesList = new ArrayList<>(plugin.getSeriesManager().getAllSeries());
        seriesList.sort(Comparator.comparing(Series::getId));
        for (final Series series : seriesList) {
            final CategoryStats stats = statsBySeries.getOrDefault(series.getId(), new CategoryStats(0, 0));
            categories.add(new CategoryEntry(series.getId(), series.getDisplayName(), stats.uniqueCards(), stats.totalCopies()));
        }
        return categories;
    }

    private @NotNull Map<String, CategoryStats> getCategoryStatsBy(
            final @NotNull java.util.function.Function<StorageEntry, String> keyExtractor,
            final @NotNull Player player
    ) {
        final Map<String, CategoryStats> stats = new java.util.HashMap<>();
        for (final StorageEntry entry : collectorBookManager.getOwnedEntries(player.getUniqueId())) {
            if (entry.getAmount() <= 0) {
                continue;
            }

            final String key = keyExtractor.apply(entry);
            final CategoryStats current = stats.getOrDefault(key, new CategoryStats(0, 0));
            stats.put(key, new CategoryStats(current.uniqueCards() + 1, current.totalCopies() + entry.getAmount()));
        }
        return stats;
    }

    private @NotNull List<StorageEntry> getFilteredEntries(
            final @NotNull Player player,
            final @NotNull ViewType viewType,
            final @Nullable String filterId
    ) {
        return collectorBookManager.getOwnedEntries(player.getUniqueId())
                .stream()
                .filter(e -> e.getAmount() > 0)
                .filter(e -> matchesFilter(e, viewType, filterId))
                .sorted(Comparator
                        .comparing(StorageEntry::getRarityId)
                        .thenComparing(StorageEntry::getSeriesId)
                        .thenComparing(StorageEntry::getCardId)
                        .thenComparing(StorageEntry::isShiny))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    private @NotNull Gui buildInvalidBrowserGui(
            final @NotNull CollectorGuiConfig.BrowserConfig browser,
            final @NotNull String message
    ) {
        return Gui.of(browser.size() / 9)
                .title(LEGACY.deserialize(browser.cardsAllTitle()))
                .statelessComponent(container ->
                        container.setItem(browser.navigation().back().slot(),
                                buildButtonItem(browser.navigation().back(), (player, ctx) -> {
                                    ChatUtil.sendPrefixedMessage(player, message);
                                    buildMainMenu().open(player);
                                })))
                .build();
    }

    private boolean matchesFilter(
            final @NotNull StorageEntry entry,
            final @NotNull ViewType viewType,
            final @Nullable String filterId
    ) {
        return switch (viewType) {
            case CARDS_RARITY -> matchesRarity(entry, filterId);
            case CARDS_SERIES -> matchesSeries(entry, filterId);
            default -> true;
        };
    }

    private boolean matchesRarity(final @NotNull StorageEntry entry, final @Nullable String filterId) {
        return filterId == null || filterId.equals(entry.getRarityId());
    }

    private boolean matchesSeries(final @NotNull StorageEntry entry, final @Nullable String filterId) {
        return filterId == null || filterId.equals(entry.getSeriesId());
    }

    private @NotNull List<String> browserOwnedCardLore(final @NotNull StorageEntry entry) {
        return config.browserConfig().ownedCardLore().stream()
                .map(line -> replaceEntryPlaceholders(line, entry))
                .map(this::toLegacyString)
                .toList();
    }

    private void givePhysicalItem(final @NotNull Player player, final @NotNull ItemStack itemStack) {
        final Map<Integer, ItemStack> leftovers = player.getInventory().addItem(itemStack);
        for (final ItemStack leftover : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private int removeFromInventory(
            final @NotNull Player player,
            final @NotNull StorageEntry entry,
            final int requestedAmount
    ) {
        int remaining = requestedAmount;
        int removed = 0;
        for (int slot = 0; slot < player.getInventory().getStorageContents().length && remaining > 0; slot++) {
            final ItemStack item = player.getInventory().getItem(slot);
            if (!isMatchingCard(item, entry)) continue;
            final int toRemove = Math.min(remaining, item.getAmount());
            removed += toRemove;
            remaining -= toRemove;
            if (toRemove >= item.getAmount()) player.getInventory().setItem(slot, null);
            else item.setAmount(item.getAmount() - toRemove);
        }
        return removed;
    }

    private boolean isMatchingCard(final @Nullable ItemStack item, final @NotNull StorageEntry entry) {
        if (item == null || item.getType().isAir() || !NbtUtils.Card.isCard(item)) return false;
        final String cardId = NbtUtils.Card.getCardId(item);
        final String rarityId = NbtUtils.Card.getRarityId(item);
        final String seriesId = NbtUtils.Card.getSeriesId(item);
        if (cardId == null || rarityId == null || seriesId == null) return false;
        return cardId.equals(entry.getCardId())
                && rarityId.equals(entry.getRarityId())
                && seriesId.equals(entry.getSeriesId())
                && NbtUtils.Card.isShiny(item) == entry.isShiny();
    }

    private @NotNull String getVersionName(final @NotNull StorageEntry entry) {
        return entry.isShiny() ? "Shiny" : "Normal";
    }

    private @NotNull String replaceCategoryPlaceholders(
            final @NotNull String input,
            final @NotNull CategoryEntry entry
    ) {
        return input
                .replace("%id%", entry.id())
                .replace("%display_name%", entry.displayName().replace("_", " "))
                .replace("%unique_cards%", Integer.toString(entry.uniqueCards()))
                .replace("%total_copies%", Integer.toString(entry.totalCopies()));
    }

    private @NotNull String replaceEntryPlaceholders(
            final @NotNull String input,
            final @NotNull StorageEntry entry
    ) {
        return input
                .replace("%card_id%", entry.getCardId())
                .replace("%rarity_id%", entry.getRarityId())
                .replace("%series_id%", entry.getSeriesId())
                .replace("%owned%", Integer.toString(entry.getAmount()))
                .replace("%version%", getVersionName(entry));
    }

    private @NotNull String toLegacyString(final @NotNull String miniMessageText) {
        return LEGACY.serialize(MINI_MESSAGE.deserialize(miniMessageText));
    }

    private int clampPage(final int requested, final int total, final int pageSize) {
        if (total <= 0 || pageSize <= 0) return 0;
        return Math.clamp(requested, 0, (total - 1) / pageSize);
    }
}
