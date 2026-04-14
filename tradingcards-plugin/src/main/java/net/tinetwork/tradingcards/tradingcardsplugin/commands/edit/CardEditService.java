package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeCardKey;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeRaritySeriesKey;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class CardEditService {
    private static final String DISPLAY_NAME_KEY = "display_name";
    private static final String TYPE_KEY = "type";
    private static final String SERIES_KEY = "series";
    private static final String HAS_SHINY_KEY = "has_shiny";
    private static final String BUY_PRICE_KEY = "buy_price";
    private static final String SELL_PRICE_KEY = "sell_price";
    private static final String CURRENCY_ID_KEY = "currency_id";
    private static final String CUSTOM_MODEL_DATA_KEY = "custom_model_data";
    private static final String INFO_KEY = "info";

    private final TradingCards plugin;

    public CardEditService(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    public void openEditor(final @NotNull Player player, final @NotNull String rarityId, final @NotNull String seriesId, final @NotNull String cardId) {
        player.showDialog(buildEditorMenu(getCard(rarityId, seriesId, cardId)));
    }

    public boolean applyDetailsEdits(
            final @NotNull CommandSender sender,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final @NotNull String cardId,
            final @NotNull String displayName,
            final @NotNull String typeId,
            final @NotNull String targetSeriesId,
            final @NotNull String hasShinyInput
    ) {
        final TradingCard card = getCard(rarityId, seriesId, cardId);
        if (!plugin.getDropTypeManager().containsType(typeId)) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_TYPE.formatted(typeId));
            return false;
        }
        if (!plugin.getSeriesManager().containsSeries(targetSeriesId)) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_SERIES.formatted(targetSeriesId));
            return false;
        }
        if (!seriesId.equals(targetSeriesId) && plugin.getCardManager().containsCard(cardId, rarityId, targetSeriesId)) {
            ChatUtil.sendPrefixedMessage(sender, "&4Card already exists for &c%s".formatted("%s, %s, %s".formatted(cardId, rarityId, targetSeriesId)));
            return false;
        }

        final Boolean hasShiny = parseBoolean(hasShinyInput);
        if (hasShiny == null) {
            ChatUtil.sendPrefixedMessage(sender, "&4Has shiny must be &ctrue &4or &cfalse");
            return false;
        }

        final DropType type = plugin.getDropTypeManager().getType(typeId);
        final Series targetSeries = plugin.getSeriesManager().getSeries(targetSeriesId);
        plugin.getStorage().editCard(
                rarityId,
                cardId,
                seriesId,
                displayName,
                targetSeries,
                type,
                card.getInfo(),
                card.getCustomModelNbt(),
                card.getBuyPrice(),
                card.getSellPrice(),
                hasShiny,
                orEmpty(card.getCurrencyId())
        );
        refreshCardCaches(rarityId, cardId, seriesId, targetSeriesId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &bdetails &7for &b%s".formatted(cardId));
        reopenEditor(sender, rarityId, targetSeriesId, cardId);
        return true;
    }

    public boolean applyEconomyEdits(
            final @NotNull CommandSender sender,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final @NotNull String cardId,
            final @NotNull String buyPriceInput,
            final @NotNull String sellPriceInput,
            final @NotNull String currencyId,
            final @NotNull String customModelDataInput
    ) {
        final TradingCard card = getCard(rarityId, seriesId, cardId);
        final double buyPrice = parsePrice(buyPriceInput);
        final double sellPrice = parsePrice(sellPriceInput);
        if (buyPrice <= -1.00D || sellPrice <= -1.00D) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
            return false;
        }

        final int customModelData = parseCustomModelData(customModelDataInput);
        if (customModelData < 0) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.CUSTOM_MODEL_DATA_INCORRECT);
            return false;
        }

        plugin.getStorage().editCard(
                rarityId,
                cardId,
                seriesId,
                card.getDisplayName(),
                card.getSeries(),
                card.getType(),
                card.getInfo(),
                customModelData,
                buyPrice,
                sellPrice,
                card.hasShiny(),
                currencyId
        );
        refreshCardCaches(rarityId, cardId, seriesId, seriesId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &beconomy &7for &b%s".formatted(cardId));
        reopenEditor(sender, rarityId, seriesId, cardId);
        return true;
    }

    public boolean applyInfoEdits(
            final @NotNull CommandSender sender,
            final @NotNull String rarityId,
            final @NotNull String seriesId,
            final @NotNull String cardId,
            final @NotNull String info
    ) {
        final TradingCard card = getCard(rarityId, seriesId, cardId);
        plugin.getStorage().editCard(
                rarityId,
                cardId,
                seriesId,
                card.getDisplayName(),
                card.getSeries(),
                card.getType(),
                info,
                card.getCustomModelNbt(),
                card.getBuyPrice(),
                card.getSellPrice(),
                card.hasShiny(),
                orEmpty(card.getCurrencyId())
        );
        refreshCardCaches(rarityId, cardId, seriesId, seriesId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &binfo &7for &b%s".formatted(cardId));
        reopenEditor(sender, rarityId, seriesId, cardId);
        return true;
    }

    public void refreshCardCaches(final @NotNull String rarityId, final @NotNull String cardId, final @NotNull String oldSeriesId, final @NotNull String newSeriesId) {
        final CompositeCardKey oldKey = new CompositeCardKey(rarityId, oldSeriesId, cardId);
        final CompositeCardKey newKey = new CompositeCardKey(rarityId, newSeriesId, cardId);

        plugin.getCardManager().getCache().invalidate(oldKey);
        plugin.getCardManager().getCache().invalidate(newKey);
        plugin.getCardManager().getCache().refresh(newKey);

        plugin.getCardManager().getRarityCardCache().invalidate(rarityId);
        plugin.getCardManager().getRarityCardCache().refresh(rarityId);

        refreshSeriesCaches(rarityId, oldSeriesId);
        if (!oldSeriesId.equals(newSeriesId)) {
            refreshSeriesCaches(rarityId, newSeriesId);
        }
    }

    private void refreshSeriesCaches(final @NotNull String rarityId, final @NotNull String seriesId) {
        plugin.getCardManager().getSeriesCardCache().invalidate(seriesId);
        plugin.getCardManager().getSeriesCardCache().refresh(seriesId);

        final CompositeRaritySeriesKey raritySeriesKey = CompositeRaritySeriesKey.of(rarityId, seriesId);
        plugin.getCardManager().getRarityAndSeriesCardCache().invalidate(raritySeriesKey);
        plugin.getCardManager().getRarityAndSeriesCardCache().refresh(raritySeriesKey);
    }

    private @NotNull Dialog buildEditorMenu(final @NotNull TradingCard card) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit card: " + card.getCardId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Rarity: " + card.getRarity().getId() + " | Series: " + card.getSeries().getId()), 340),
                                DialogBody.plainMessage(Component.text("Display: " + card.getDisplayName()), 340),
                                DialogBody.plainMessage(Component.text("Type: " + card.getType().getId() + " | Has shiny: " + card.hasShiny()), 340),
                                DialogBody.plainMessage(Component.text("Buy: " + card.getBuyPrice() + " | Sell: " + card.getSellPrice() + " | Currency: " + orEmpty(card.getCurrencyId())), 340),
                                DialogBody.plainMessage(Component.text("Model data: " + card.getCustomModelNbt()), 340),
                                DialogBody.plainMessage(Component.text("Choose which part of the card to edit."), 340)
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Edit Details"),
                                Component.text("Display name, type, series and shiny"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildDetailsEditor(getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Edit Economy"),
                                Component.text("Prices, currency and model data"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildEconomyEditor(getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Edit Info"),
                                Component.text("Card lore text"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildInfoEditor(getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Preview Item"),
                                Component.text("Preview the current card item"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildPreviewDialog(
                                                "Card preview: " + card.getCardId(),
                                                getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId()),
                                                previewPlayer -> previewPlayer.showDialog(buildEditorMenu(getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId())))
                                        ));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        )
                ), null, 2)));
    }

    private @NotNull Dialog buildDetailsEditor(final @NotNull TradingCard card) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit details: " + card.getCardId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Has shiny accepts true or false."), 340)
                        ))
                        .inputs(List.of(
                                DialogInput.text(DISPLAY_NAME_KEY, Component.text("Display Name")).initial(card.getDisplayName()).maxLength(128).width(340).build(),
                                DialogInput.text(TYPE_KEY, Component.text("Type Id")).initial(card.getType().getId()).maxLength(128).width(340).build(),
                                DialogInput.text(SERIES_KEY, Component.text("Series Id")).initial(card.getSeries().getId()).maxLength(128).width(340).build(),
                                DialogInput.text(HAS_SHINY_KEY, Component.text("Has Shiny")).initial(String.valueOf(card.hasShiny())).maxLength(16).width(340).build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Save Details"),
                                Component.text("Apply these detail edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyDetailsEdits(
                                                player,
                                                card.getRarity().getId(),
                                                card.getSeries().getId(),
                                                card.getCardId(),
                                                textValue(response.getText(DISPLAY_NAME_KEY)),
                                                textValue(response.getText(TYPE_KEY)),
                                                textValue(response.getText(SERIES_KEY)),
                                                textValue(response.getText(HAS_SHINY_KEY))
                                        );
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Preview"),
                                Component.text("Preview the card item with these values"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        final TradingCard previewCard = buildDetailsPreviewCard(
                                                player,
                                                card,
                                                textValue(response.getText(DISPLAY_NAME_KEY)),
                                                textValue(response.getText(TYPE_KEY)),
                                                textValue(response.getText(SERIES_KEY)),
                                                textValue(response.getText(HAS_SHINY_KEY))
                                        );
                                        if (previewCard != null) {
                                            player.showDialog(buildPreviewDialog(
                                                    "Card preview: " + card.getCardId(),
                                                    previewCard,
                                                    previewPlayer -> previewPlayer.showDialog(buildDetailsEditor(getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId())))
                                            ));
                                        }
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                ), null, 2)));
    }

    private @NotNull Dialog buildEconomyEditor(final @NotNull TradingCard card) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit economy: " + card.getCardId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Custom model data can be 0 or greater."), 340)
                        ))
                        .inputs(List.of(
                                DialogInput.text(BUY_PRICE_KEY, Component.text("Buy Price")).initial(String.valueOf(card.getBuyPrice())).maxLength(32).width(340).build(),
                                DialogInput.text(SELL_PRICE_KEY, Component.text("Sell Price")).initial(String.valueOf(card.getSellPrice())).maxLength(32).width(340).build(),
                                DialogInput.text(CURRENCY_ID_KEY, Component.text("Currency Id")).initial(orEmpty(card.getCurrencyId())).maxLength(128).width(340).build(),
                                DialogInput.text(CUSTOM_MODEL_DATA_KEY, Component.text("Custom Model Data")).initial(String.valueOf(card.getCustomModelNbt())).maxLength(32).width(340).build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Save Economy"),
                                Component.text("Apply these economy edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyEconomyEdits(
                                                player,
                                                card.getRarity().getId(),
                                                card.getSeries().getId(),
                                                card.getCardId(),
                                                textValue(response.getText(BUY_PRICE_KEY)),
                                                textValue(response.getText(SELL_PRICE_KEY)),
                                                textValue(response.getText(CURRENCY_ID_KEY)),
                                                textValue(response.getText(CUSTOM_MODEL_DATA_KEY))
                                        );
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Preview"),
                                Component.text("Preview the card item with these values"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        final TradingCard previewCard = buildEconomyPreviewCard(
                                                player,
                                                card,
                                                textValue(response.getText(BUY_PRICE_KEY)),
                                                textValue(response.getText(SELL_PRICE_KEY)),
                                                textValue(response.getText(CURRENCY_ID_KEY)),
                                                textValue(response.getText(CUSTOM_MODEL_DATA_KEY))
                                        );
                                        if (previewCard != null) {
                                            player.showDialog(buildPreviewDialog(
                                                    "Card preview: " + card.getCardId(),
                                                    previewCard,
                                                    previewPlayer -> previewPlayer.showDialog(buildEconomyEditor(getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId())))
                                            ));
                                        }
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                ), null, 2)));
    }

    private @NotNull Dialog buildInfoEditor(final @NotNull TradingCard card) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit info: " + card.getCardId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("This updates the card info text shown in lore."), 340)
                        ))
                        .inputs(List.of(
                                DialogInput.text(INFO_KEY, Component.text("Info"))
                                        .initial(orEmpty(card.getInfo()))
                                        .maxLength(4096)
                                        .width(340)
                                        .multiline(TextDialogInput.MultilineOptions.create(8, 160))
                                        .build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Save Info"),
                                Component.text("Apply these info edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyInfoEdits(
                                                player,
                                                card.getRarity().getId(),
                                                card.getSeries().getId(),
                                                card.getCardId(),
                                                textValue(response.getText(INFO_KEY))
                                        );
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Preview"),
                                Component.text("Preview the card item with these values"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildPreviewDialog(
                                                "Card preview: " + card.getCardId(),
                                                buildInfoPreviewCard(card, textValue(response.getText(INFO_KEY))),
                                                previewPlayer -> previewPlayer.showDialog(buildInfoEditor(getCard(card.getRarity().getId(), card.getSeries().getId(), card.getCardId())))
                                        ));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                ), null, 2)));
    }

    private TradingCard buildDetailsPreviewCard(
            final @NotNull CommandSender sender,
            final @NotNull TradingCard card,
            final @NotNull String displayName,
            final @NotNull String typeId,
            final @NotNull String targetSeriesId,
            final @NotNull String hasShinyInput
    ) {
        if (!plugin.getDropTypeManager().containsType(typeId)) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_TYPE.formatted(typeId));
            return null;
        }
        if (!plugin.getSeriesManager().containsSeries(targetSeriesId)) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_SERIES.formatted(targetSeriesId));
            return null;
        }

        final Boolean hasShiny = parseBoolean(hasShinyInput);
        if (hasShiny == null) {
            ChatUtil.sendPrefixedMessage(sender, "&4Has shiny must be &ctrue &4or &cfalse");
            return null;
        }

        final TradingCard previewCard = new TradingCard(card);
        previewCard.displayName(displayName)
                .type(plugin.getDropTypeManager().getType(typeId))
                .series(plugin.getSeriesManager().getSeries(targetSeriesId))
                .hasShiny(hasShiny);
        return previewCard;
    }

    private TradingCard buildEconomyPreviewCard(
            final @NotNull CommandSender sender,
            final @NotNull TradingCard card,
            final @NotNull String buyPriceInput,
            final @NotNull String sellPriceInput,
            final @NotNull String currencyId,
            final @NotNull String customModelDataInput
    ) {
        final double buyPrice = parsePrice(buyPriceInput);
        final double sellPrice = parsePrice(sellPriceInput);
        if (buyPrice <= -1.00D || sellPrice <= -1.00D) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
            return null;
        }

        final int customModelData = parseCustomModelData(customModelDataInput);
        if (customModelData < 0) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.CUSTOM_MODEL_DATA_INCORRECT);
            return null;
        }

        final TradingCard previewCard = new TradingCard(card);
        previewCard.buyPrice(buyPrice)
                .sellPrice(sellPrice)
                .currencyId(currencyId)
                .customModelNbt(customModelData);
        return previewCard;
    }

    private @NotNull TradingCard buildInfoPreviewCard(final @NotNull TradingCard card, final @NotNull String info) {
        final TradingCard previewCard = new TradingCard(card);
        previewCard.info(info);
        return previewCard;
    }

    private @NotNull Dialog buildPreviewDialog(
            final @NotNull String title,
            final @NotNull TradingCard card,
            final @NotNull PreviewReturn previewReturn
    ) {
        final List<DialogBody> body = new ArrayList<>();
        body.add(DialogBody.plainMessage(Component.text("Previewing the resulting card item."), 340));
        body.add(DialogBody.plainMessage(Component.text("Display: " + card.getDisplayName() + " | Type: " + card.getType().getId()), 340));
        body.add(DialogBody.plainMessage(Component.text("Series: " + card.getSeries().getId() + " | Has shiny: " + card.hasShiny()), 340));
        body.add(DialogBody.plainMessage(Component.text("Normal"), 340));
        body.add(DialogBody.item(safeBuild(card, false)).build());
        if (card.hasShiny()) {
            body.add(DialogBody.plainMessage(Component.text("Shiny"), 340));
            body.add(DialogBody.item(safeBuild(card, true)).build());
        }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Back"),
                                Component.text("Return to the editor"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        previewReturn.show(player);
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        )
                ), null, 1)));
    }

    private @NotNull ItemStack safeBuild(final @NotNull TradingCard card, final boolean shiny) {
        return card.build(shiny);
    }

    private void reopenEditor(final @NotNull CommandSender sender, final @NotNull String rarityId, final @NotNull String seriesId, final @NotNull String cardId) {
        if (sender instanceof Player player) {
            player.showDialog(buildEditorMenu(getCard(rarityId, seriesId, cardId)));
        }
    }

    private @NotNull TradingCard getCard(final @NotNull String rarityId, final @NotNull String seriesId, final @NotNull String cardId) {
        return plugin.getStorage().getCard(cardId, rarityId, seriesId).get();
    }

    private double parsePrice(final @NotNull String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {
            return -1.00D;
        }
    }

    private int parseCustomModelData(final @NotNull String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private Boolean parseBoolean(final @NotNull String input) {
        if ("true".equalsIgnoreCase(input)) {
            return true;
        }
        if ("false".equalsIgnoreCase(input)) {
            return false;
        }
        return null;
    }

    private @NotNull String textValue(final String input) {
        return input == null ? "" : input.trim();
    }

    private @NotNull String orEmpty(final String input) {
        return input == null ? "" : input;
    }

    @FunctionalInterface
    private interface PreviewReturn {
        void show(Player player);
    }
}
