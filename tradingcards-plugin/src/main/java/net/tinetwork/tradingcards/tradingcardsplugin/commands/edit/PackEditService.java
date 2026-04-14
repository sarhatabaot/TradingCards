package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PackEditService {
    private static final String DISPLAY_NAME_KEY = "display_name";
    private static final String PRICE_KEY = "price";
    private static final String PERMISSION_KEY = "permission";
    private static final String CURRENCY_ID_KEY = "currency_id";
    private static final String CONTENTS_KEY = "contents";
    private static final String TRADE_KEY = "trade";

    private final TradingCards plugin;

    public PackEditService(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    public void openScalarEditor(final @NotNull Player player, final @NotNull String packId) {
        final Pack pack = plugin.getStorage().getPack(packId);
        player.showDialog(buildEditorMenu(pack));
    }

    public boolean applyDetailsEdits(
            final @NotNull CommandSender sender,
            final @NotNull String packId,
            final @NotNull String displayName,
            final @NotNull String priceInput,
            final @NotNull String permission,
            final @NotNull String currencyId
    ) {
        final double price = parsePrice(sender, priceInput);
        if (price <= -1.00D) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
            return false;
        }
        plugin.getStorage().editPack(packId, displayName, price, permission, currencyId);
        plugin.getPackManager().getCache().refresh(packId);
        ChatUtil.sendPrefixedMessage(
                sender,
                "&7Updated &b%s &7with &bdisplay-name&7, &bprice&7, &bpermission &7and &bcurrency-id".formatted(packId)
        );
        reopenEditorMenu(sender, packId);
        return true;
    }

    public boolean applyContentsEdits(
            final @NotNull CommandSender sender,
            final @NotNull String packId,
            final @NotNull String contentsInput
    ) {
        final Pack pack = plugin.getStorage().getPack(packId);
        final List<PackEntry> contents = parseEntries(sender, contentsInput, "contents");
        if (contents == null) {
            return false;
        }

        plugin.getStorage().editPack(
                packId,
                pack.getDisplayName(),
                pack.getBuyPrice(),
                pack.getPermission(),
                pack.getCurrencyId(),
                contents,
                pack.getTradeCards()
        );
        plugin.getPackManager().getCache().refresh(packId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &bcontents &7for &b%s".formatted(packId));
        reopenEditorMenu(sender, packId);
        return true;
    }

    public boolean applyTradeEdits(
            final @NotNull CommandSender sender,
            final @NotNull String packId,
            final @NotNull String tradeInput
    ) {
        final Pack pack = plugin.getStorage().getPack(packId);
        final List<PackEntry> tradeCards = parseEntries(sender, tradeInput, "trade");
        if (tradeCards == null) {
            return false;
        }

        plugin.getStorage().editPack(
                packId,
                pack.getDisplayName(),
                pack.getBuyPrice(),
                pack.getPermission(),
                pack.getCurrencyId(),
                pack.getPackEntryList(),
                tradeCards
        );
        plugin.getPackManager().getCache().refresh(packId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &btrade &7for &b%s".formatted(packId));
        reopenEditorMenu(sender, packId);
        return true;
    }

    private double parsePrice(final @NotNull CommandSender sender, final @NotNull String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {
            return -1.00D;
        }
    }

    private @NotNull Dialog buildEditorMenu(final @NotNull Pack pack) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit pack: " + pack.getId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Display: " + pack.getDisplayName()), 320),
                                DialogBody.plainMessage(Component.text("Price: " + pack.getBuyPrice() + " | Currency: " + orEmpty(pack.getCurrencyId())), 320),
                                DialogBody.plainMessage(Component.text("Permission: " + orEmpty(pack.getPermission())), 320),
                                DialogBody.plainMessage(Component.text("Contents: " + pack.getPackEntryList().size() + " entries | Trade: " + pack.getTradeCards().size() + " entries"), 320),
                                DialogBody.plainMessage(Component.text("Choose which part of the pack to edit."), 320),
                                DialogBody.plainMessage(Component.text("Contents and trade use one entry per line: rarity:amount:series"), 320)
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Edit Details"),
                                Component.text("Display name, price, permission and currency"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildDetailsEditor(plugin.getStorage().getPack(pack.getId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Edit Contents"),
                                Component.text("Replace the full contents list"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildContentsEditor(plugin.getStorage().getPack(pack.getId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Edit Trade"),
                                Component.text("Replace the full trade list"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildTradeEditor(plugin.getStorage().getPack(pack.getId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Preview Item"),
                                Component.text("Preview the current pack item"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildPreviewDialog(
                                                "Pack preview: " + pack.getId(),
                                                plugin.getStorage().getPack(pack.getId()),
                                                previewPlayer -> previewPlayer.showDialog(buildEditorMenu(plugin.getStorage().getPack(pack.getId())))
                                        ));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        )
                ), null, 2)));
    }

    private @NotNull Dialog buildDetailsEditor(final @NotNull Pack pack) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit details: " + pack.getId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Update the scalar pack fields below."), 320)
                        ))
                        .inputs(List.of(
                                DialogInput.text(DISPLAY_NAME_KEY, Component.text("Display Name"))
                                        .initial(pack.getDisplayName())
                                        .maxLength(128)
                                        .width(320)
                                        .build(),
                                DialogInput.text(PRICE_KEY, Component.text("Price"))
                                        .initial(String.valueOf(pack.getBuyPrice()))
                                        .maxLength(32)
                                        .width(320)
                                        .build(),
                                DialogInput.text(PERMISSION_KEY, Component.text("Permission"))
                                        .initial(orEmpty(pack.getPermission()))
                                        .maxLength(128)
                                        .width(320)
                                        .build(),
                                DialogInput.text(CURRENCY_ID_KEY, Component.text("Currency Id"))
                                        .initial(orEmpty(pack.getCurrencyId()))
                                        .maxLength(128)
                                        .width(320)
                                        .build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Save"),
                                Component.text("Apply these detail edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyDetailsEdits(
                                                player,
                                                pack.getId(),
                                                textValue(response.getText(DISPLAY_NAME_KEY)),
                                                textValue(response.getText(PRICE_KEY)),
                                                textValue(response.getText(PERMISSION_KEY)),
                                                textValue(response.getText(CURRENCY_ID_KEY))
                                        );
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Preview"),
                                Component.text("Preview the pack item with these values"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        final Pack previewPack = buildDetailsPreviewPack(
                                                player,
                                                pack,
                                                textValue(response.getText(DISPLAY_NAME_KEY)),
                                                textValue(response.getText(PRICE_KEY)),
                                                textValue(response.getText(PERMISSION_KEY)),
                                                textValue(response.getText(CURRENCY_ID_KEY))
                                        );
                                        if (previewPack != null) {
                                            player.showDialog(buildPreviewDialog(
                                                    "Pack preview: " + pack.getId(),
                                                    previewPack,
                                                    previewPlayer -> previewPlayer.showDialog(buildDetailsEditor(plugin.getStorage().getPack(pack.getId())))
                                            ));
                                        }
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Cancel"),
                                Component.text("Discard these changes"),
                                120,
                                null
                        )
                ), null, 2)));
    }

    private @NotNull Dialog buildContentsEditor(final @NotNull Pack pack) {
        return buildListEditor(
                pack,
                "Edit contents: ",
                CONTENTS_KEY,
                "Contents",
                "Replace the full contents list. One entry per line.",
                "Example: common:9:default",
                joinEntries(pack.getPackEntryList()),
                "Save Contents",
                "Apply these contents edits",
                "Preview",
                "Preview the pack item with these values",
                (sender, value) -> buildContentsPreviewPack(sender, pack, value),
                (player, value) -> applyContentsEdits(player, pack.getId(), value)
        );
    }

    private @NotNull Dialog buildTradeEditor(final @NotNull Pack pack) {
        return buildListEditor(
                pack,
                "Edit trade: ",
                TRADE_KEY,
                "Trade",
                "Replace the full trade list. Leave blank for no trade entries.",
                "Example: rare:2:default",
                joinEntries(pack.getTradeCards()),
                "Save Trade",
                "Apply these trade edits",
                "Preview",
                "Preview the pack item with these values",
                (sender, value) -> buildTradePreviewPack(sender, pack, value),
                (player, value) -> applyTradeEdits(player, pack.getId(), value)
        );
    }

    private @NotNull Dialog buildListEditor(
            final @NotNull Pack pack,
            final @NotNull String titlePrefix,
            final @NotNull String inputKey,
            final @NotNull String label,
            final @NotNull String description,
            final @NotNull String example,
            final @NotNull String initialValue,
            final @NotNull String submitText,
            final @NotNull String submitHover,
            final @NotNull String previewText,
            final @NotNull String previewHover,
            final @NotNull PreviewPackBuilder previewBuilder,
            final @NotNull PlayerInputHandler inputHandler
    ) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(titlePrefix + pack.getId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text(description), 340),
                                DialogBody.plainMessage(Component.text(example), 340)
                        ))
                        .inputs(List.of(
                                DialogInput.text(inputKey, Component.text(label))
                                        .initial(initialValue)
                                        .maxLength(4096)
                                        .width(340)
                                        .multiline(TextDialogInput.MultilineOptions.create(20, 160))
                                        .build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text(submitText),
                                Component.text(submitHover),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        inputHandler.apply(player, textValue(response.getText(inputKey)));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text(previewText),
                                Component.text(previewHover),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        final String inputValue = textValue(response.getText(inputKey));
                                        final Pack previewPack = previewBuilder.build(player, inputValue);
                                        if (previewPack != null) {
                                            player.showDialog(buildPreviewDialog(
                                                    "Pack preview: " + pack.getId(),
                                                    previewPack,
                                                    previewPlayer -> previewPlayer.showDialog(
                                                            CONTENTS_KEY.equals(inputKey)
                                                                    ? buildContentsEditor(plugin.getStorage().getPack(pack.getId()))
                                                                    : buildTradeEditor(plugin.getStorage().getPack(pack.getId()))
                                                    )
                                            ));
                                        }
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Cancel"),
                                Component.text("Discard these changes"),
                                120,
                                null
                        )
                ), null, 2)));
    }

    private Pack buildDetailsPreviewPack(
            final @NotNull CommandSender sender,
            final @NotNull Pack pack,
            final @NotNull String displayName,
            final @NotNull String priceInput,
            final @NotNull String permission,
            final @NotNull String currencyId
    ) {
        final double price = parsePrice(sender, priceInput);
        if (price <= -1.00D) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
            return null;
        }

        return new Pack(
                pack.getId(),
                pack.getPackEntryList(),
                displayName,
                price,
                currencyId,
                permission,
                pack.getTradeCards()
        );
    }

    private Pack buildContentsPreviewPack(final @NotNull CommandSender sender, final @NotNull Pack pack, final @NotNull String contentsInput) {
        final List<PackEntry> contents = parseEntries(sender, contentsInput, "contents");
        if (contents == null) {
            return null;
        }
        return new Pack(
                pack.getId(),
                contents,
                pack.getDisplayName(),
                pack.getBuyPrice(),
                pack.getCurrencyId(),
                pack.getPermission(),
                pack.getTradeCards()
        );
    }

    private Pack buildTradePreviewPack(final @NotNull CommandSender sender, final @NotNull Pack pack, final @NotNull String tradeInput) {
        final List<PackEntry> tradeCards = parseEntries(sender, tradeInput, "trade");
        if (tradeCards == null) {
            return null;
        }
        return new Pack(
                pack.getId(),
                pack.getPackEntryList(),
                pack.getDisplayName(),
                pack.getBuyPrice(),
                pack.getCurrencyId(),
                pack.getPermission(),
                tradeCards
        );
    }

    private @NotNull Dialog buildPreviewDialog(
            final @NotNull String title,
            final @NotNull Pack pack,
            final @NotNull PreviewReturn previewReturn
    ) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Previewing the resulting pack item."), 340),
                                DialogBody.plainMessage(Component.text("Display: " + pack.getDisplayName()), 340),
                                DialogBody.plainMessage(Component.text("Price: " + pack.getBuyPrice() + " | Currency: " + orEmpty(pack.getCurrencyId())), 340),
                                DialogBody.plainMessage(Component.text("Contents: " + pack.getPackEntryList().size() + " entries | Trade: " + pack.getTradeCards().size() + " entries"), 340),
                                DialogBody.item(plugin.getPackManager().generatePack(pack)).build()
                        ))
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

    private List<PackEntry> parseEntries(final @NotNull CommandSender sender, final @NotNull String input, final @NotNull String fieldName) {
        final List<PackEntry> entries = new ArrayList<>();
        if (input.isBlank()) {
            return entries;
        }

        final String[] lines = input.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            try {
                final PackEntry entry = PackEntry.fromString(line);
                if (!plugin.getRarityManager().containsRarity(entry.getRarityId())) {
                    ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_RARITY.formatted(entry.getRarityId()));
                    return null;
                }
                final String seriesId = entry.seriesId();
                if (seriesId != null && !plugin.getSeriesManager().containsSeries(seriesId)) {
                    ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_SERIES.formatted(seriesId));
                    return null;
                }
                entries.add(entry);
            } catch (RuntimeException ignored) {
                ChatUtil.sendPrefixedMessage(sender, "&4Invalid %s entry on line %s: &c%s".formatted(fieldName, i + 1, line));
                ChatUtil.sendPrefixedMessage(sender, "&7Expected format: &brarity:amount:series");
                return null;
            }
        }

        return entries;
    }

    private @NotNull String joinEntries(final @NotNull List<PackEntry> entries) {
        return entries.stream().map(PackEntry::toString).reduce((left, right) -> left + "\n" + right).orElse("");
    }

    private @NotNull String textValue(final String input) {
        return input == null ? "" : input.trim();
    }

    private @NotNull String orEmpty(final String input) {
        return input == null ? "" : input;
    }

    private void reopenEditorMenu(final @NotNull CommandSender sender, final @NotNull String packId) {
        if (sender instanceof Player player) {
            player.showDialog(buildEditorMenu(plugin.getStorage().getPack(packId)));
        }
    }

    @FunctionalInterface
    private interface PlayerInputHandler {
        boolean apply(Player player, String value);
    }

    @FunctionalInterface
    private interface PreviewPackBuilder {
        Pack build(CommandSender sender, String value);
    }

    @FunctionalInterface
    private interface PreviewReturn {
        void show(Player player);
    }
}
