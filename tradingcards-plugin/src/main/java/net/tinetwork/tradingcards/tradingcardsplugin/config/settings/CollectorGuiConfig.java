package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import com.lapzupi.dev.config.Transformation;
import com.lapzupi.dev.config.YamlConfigurateFile;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class CollectorGuiConfig extends YamlConfigurateFile<TradingCards> {
    private static final String DEFAULT_MAIN_TITLE = "<dark_gray>Collector Book";
    private static final String DEFAULT_RARITIES_TITLE = "<dark_gray>Collector Book - Rarities";
    private static final String DEFAULT_SERIES_TITLE = "<dark_gray>Collector Book - Series";
    private static final String DEFAULT_CARDS_ALL_TITLE = "<dark_gray>Collector Book - All Cards";
    private static final String DEFAULT_CARDS_RARITY_TITLE = "<dark_gray>Collector - Rarity: <white>%filter%";
    private static final String DEFAULT_CARDS_SERIES_TITLE = "<dark_gray>Collector - Series: <white>%filter%";
    private static final List<String> DEFAULT_CATEGORY_LORE = List.of(
            "<gray>Id: <white>%id%",
            "<gray>Unique Cards: <white>%unique_cards%",
            "<gray>Total Copies: <white>%total_copies%",
            "<yellow>Click to open"
    );
    private static final List<String> DEFAULT_REMOVED_CARD_LORE = List.of(
            "<gray>Card: <white>%card_id%",
            "<gray>Rarity: <white>%rarity_id%",
            "<gray>Series: <white>%series_id%",
            "<gray>Owned: <white>%owned%",
            "<gray>Version: <white>%version%",
            "<red>Cannot withdraw this card.",
            "<yellow>Right-Click: <white>Deposit 1",
            "<yellow>Shift-Right: <white>Deposit all from inventory"
    );
    private static final List<String> DEFAULT_OWNED_CARD_LORE = List.of(
            "<dark_gray>Collector",
            "<gray>Owned: <white>%owned%",
            "<gray>Version: <white>%version%",
            "<yellow>Left-Click: <white>Withdraw 1",
            "<yellow>Shift-Left: <white>Withdraw up to 64",
            "<yellow>Right-Click: <white>Deposit 1",
            "<yellow>Shift-Right: <white>Deposit all from inventory"
    );

    private MenuConfig menuConfig;
    private BrowserConfig browserConfig;

    public CollectorGuiConfig(final @NotNull TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings" + File.separator, "collector-gui.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        final ConfigurationNode menuNode = rootNode.node("main-menu");
        final int menuSize = sanitizeInventorySize(menuNode.node("size").getInt(27), 27);
        this.menuConfig = new MenuConfig(
                menuNode.node("title").getString(DEFAULT_MAIN_TITLE),
                menuSize,
                parseButton(menuNode.node("buttons", "rarities"), 11, Material.AMETHYST_SHARD, "<light_purple>Browse Rarities", List.of("<gray>View collected cards by rarity.")),
                parseButton(menuNode.node("buttons", "all-cards"), 13, Material.CHEST, "<yellow>Browse All Cards", List.of("<gray>View all collected cards.")),
                parseButton(menuNode.node("buttons", "series"), 15, Material.BOOK, "<aqua>Browse Series", List.of("<gray>View collected cards by series.")),
                parseButton(menuNode.node("buttons", "close"), 22, Material.BARRIER, "<red>Close", List.of("<gray>Close this menu."))
        );

        final ConfigurationNode browserNode = rootNode.node("browser");
        final int browserSize = sanitizeInventorySize(browserNode.node("size").getInt(54), 54);
        final ButtonConfig previousButton = parseButton(browserNode.node("navigation", "previous"), 45, Material.ARROW, "<yellow>Previous Page", List.of("<gray>Go to previous page."));
        final ButtonConfig backButton = parseButton(browserNode.node("navigation", "back"), 49, Material.BARRIER, "<red>Back", List.of("<gray>Return to previous menu."));
        final ButtonConfig nextButton = parseButton(browserNode.node("navigation", "next"), 53, Material.ARROW, "<yellow>Next Page", List.of("<gray>Go to next page."));

        this.browserConfig = new BrowserConfig(
                browserSize,
                browserNode.node("titles", "rarities").getString(DEFAULT_RARITIES_TITLE),
                browserNode.node("titles", "series").getString(DEFAULT_SERIES_TITLE),
                browserNode.node("titles", "cards-all").getString(DEFAULT_CARDS_ALL_TITLE),
                browserNode.node("titles", "cards-rarity").getString(DEFAULT_CARDS_RARITY_TITLE),
                browserNode.node("titles", "cards-series").getString(DEFAULT_CARDS_SERIES_TITLE),
                parseContentSlots(browserNode.node("content-slots"), browserSize),
                new NavigationConfig(previousButton, backButton, nextButton),
                parseMaterial(browserNode.node("category-icons", "rarity-material"), Material.AMETHYST_CLUSTER),
                parseMaterial(browserNode.node("category-icons", "series-material"), Material.BOOK),
                browserNode.node("category-entry", "name").getString("<yellow>%display_name%"),
                copyStringList(browserNode.node("category-entry", "lore").getList(String.class, DEFAULT_CATEGORY_LORE), DEFAULT_CATEGORY_LORE),
                parseMaterial(browserNode.node("removed-card", "material"), Material.BARRIER),
                browserNode.node("removed-card", "name").getString("<red>Removed Card"),
                copyStringList(browserNode.node("removed-card", "lore").getList(String.class, DEFAULT_REMOVED_CARD_LORE), DEFAULT_REMOVED_CARD_LORE),
                copyStringList(browserNode.node("owned-card", "lore").getList(String.class, DEFAULT_OWNED_CARD_LORE), DEFAULT_OWNED_CARD_LORE),
                browserNode.node("messages", "invalid-content-slots").getString("Collector browser has no content slots configured.")
        );
    }

    private @NotNull ButtonConfig parseButton(
            final @NotNull ConfigurationNode buttonNode,
            final int defaultSlot,
            final @NotNull Material defaultMaterial,
            final @NotNull String defaultName,
            final @NotNull List<String> defaultLore
    ) throws ConfigurateException {
        final int slot = buttonNode.node("slot").getInt(defaultSlot);
        final Material material = parseMaterial(buttonNode.node("material"), defaultMaterial);
        final String name = buttonNode.node("name").getString(defaultName);
        final List<String> lore = buttonNode.node("lore").getList(String.class, defaultLore);
        return new ButtonConfig(slot, material, name, List.copyOf(lore == null ? defaultLore : lore));
    }

    private @NotNull Material parseMaterial(final @NotNull ConfigurationNode node, final @NotNull Material defaultMaterial) throws ConfigurateException {
        final Material parsed = node.get(Material.class, defaultMaterial);
        return parsed == null ? defaultMaterial : parsed;
    }

    private @NotNull List<Integer> parseContentSlots(final @NotNull ConfigurationNode slotsNode, final int inventorySize) throws ConfigurateException {
        final List<Integer> slots = slotsNode.getList(Integer.class, defaultContentSlots(inventorySize));
        if (slots == null || slots.isEmpty()) {
            return defaultContentSlots(inventorySize);
        }

        final Set<Integer> uniqueSlots = new LinkedHashSet<>();
        for (Integer slot : slots) {
            if (slot == null || slot < 0 || slot >= inventorySize) {
                continue;
            }
            uniqueSlots.add(slot);
        }

        if (uniqueSlots.isEmpty()) {
            return defaultContentSlots(inventorySize);
        }
        return List.copyOf(uniqueSlots);
    }

    private @NotNull List<Integer> defaultContentSlots(final int inventorySize) {
        final int size = Math.min(45, inventorySize);
        return IntStream.range(0, size).boxed().toList();
    }

    private int sanitizeInventorySize(final int configuredSize, final int fallbackSize) {
        if (configuredSize <= 0) {
            return fallbackSize;
        }
        int size = Math.max(9, Math.min(54, configuredSize));
        if (size % 9 != 0) {
            size += (9 - (size % 9));
            size = Math.min(54, size);
        }
        return size;
    }

    private @NotNull List<String> copyStringList(final List<String> configured, final @NotNull List<String> fallback) {
        final List<String> values = configured == null ? fallback : configured;
        return List.copyOf(values);
    }

    public @NotNull MenuConfig menuConfig() {
        return menuConfig;
    }

    public @NotNull BrowserConfig browserConfig() {
        return browserConfig;
    }

    @Override
    protected void builderOptions(final TypeSerializerCollection.Builder builder) {
        // No custom serializers.
    }

    @Override
    protected Transformation getTransformation() {
        return null;
    }

    public record ButtonConfig(int slot, @NotNull Material material, @NotNull String name, @NotNull List<String> lore) {
    }

    public record NavigationConfig(@NotNull ButtonConfig previous, @NotNull ButtonConfig back, @NotNull ButtonConfig next) {
    }

    public record MenuConfig(
            @NotNull String title,
            int size,
            @NotNull ButtonConfig raritiesButton,
            @NotNull ButtonConfig allCardsButton,
            @NotNull ButtonConfig seriesButton,
            @NotNull ButtonConfig closeButton
    ) {
    }

    public record BrowserConfig(
            int size,
            @NotNull String raritiesTitle,
            @NotNull String seriesTitle,
            @NotNull String cardsAllTitle,
            @NotNull String cardsRarityTitle,
            @NotNull String cardsSeriesTitle,
            @NotNull List<Integer> contentSlots,
            @NotNull NavigationConfig navigation,
            @NotNull Material rarityCategoryMaterial,
            @NotNull Material seriesCategoryMaterial,
            @NotNull String categoryEntryName,
            @NotNull List<String> categoryEntryLore,
            @NotNull Material removedCardMaterial,
            @NotNull String removedCardName,
            @NotNull List<String> removedCardLore,
            @NotNull List<String> ownedCardLore,
            @NotNull String invalidContentSlotsMessage
    ) {
    }
}
