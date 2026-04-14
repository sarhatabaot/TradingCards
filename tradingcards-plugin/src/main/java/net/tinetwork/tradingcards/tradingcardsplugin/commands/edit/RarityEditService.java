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
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class RarityEditService {
    private static final String DISPLAY_NAME_KEY = "display_name";
    private static final String DEFAULT_COLOR_KEY = "default_color";
    private static final String BUY_PRICE_KEY = "buy_price";
    private static final String SELL_PRICE_KEY = "sell_price";
    private static final String CURRENCY_ID_KEY = "currency_id";
    private static final String REWARDS_KEY = "rewards";

    private final TradingCards plugin;

    public RarityEditService(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    public void openEditor(final @NotNull Player player, final @NotNull String rarityId) {
        player.showDialog(buildEditorMenu(plugin.getRarityManager().getRarity(rarityId)));
    }

    public boolean applyDetailsEdits(
            final @NotNull CommandSender sender,
            final @NotNull String rarityId,
            final @NotNull String displayName,
            final @NotNull String defaultColor,
            final @NotNull String buyPriceInput,
            final @NotNull String sellPriceInput,
            final @NotNull String currencyId
    ) {
        final double buyPrice = parsePrice(buyPriceInput);
        final double sellPrice = parsePrice(sellPriceInput);
        if (buyPrice <= -1.00D || sellPrice <= -1.00D) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
            return false;
        }

        final Rarity rarity = plugin.getRarityManager().getRarity(rarityId);
        plugin.getStorage().editRarity(
                rarityId,
                displayName,
                defaultColor,
                buyPrice,
                sellPrice,
                currencyId,
                rarity.getRewards()
        );
        plugin.getRarityManager().getRarityCache().refresh(rarityId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &brarity details &7for &b%s".formatted(rarityId));
        reopenEditor(sender, rarityId);
        return true;
    }

    public boolean applyRewardsEdits(final @NotNull CommandSender sender, final @NotNull String rarityId, final @NotNull String rewardsInput) {
        final Rarity rarity = plugin.getRarityManager().getRarity(rarityId);
        plugin.getStorage().editRarity(
                rarityId,
                rarity.getDisplayName(),
                rarity.getDefaultColor(),
                rarity.getBuyPrice(),
                rarity.getSellPrice(),
                rarity.getCurrencyId(),
                parseLines(rewardsInput)
        );
        plugin.getRarityManager().getRarityCache().refresh(rarityId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &brewards &7for &b%s".formatted(rarityId));
        reopenEditor(sender, rarityId);
        return true;
    }

    private @NotNull Dialog buildEditorMenu(final @NotNull Rarity rarity) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit rarity: " + rarity.getId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Display: ").append(ChatUtil.component(rarity.getDisplayName())), 320),
                                DialogBody.plainMessage(Component.text("Buy: " + rarity.getBuyPrice() + " | Sell: " + rarity.getSellPrice()), 320),
                                DialogBody.plainMessage(Component.text("Color: " + orEmpty(rarity.getDefaultColor()) + " | Currency: " + orEmpty(rarity.getCurrencyId())), 320),
                                DialogBody.plainMessage(Component.text("Rewards: " + rarity.getRewards().size() + " entries"), 320)
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(
                        ActionButton.create(
                                Component.text("Edit Details"),
                                Component.text("Display name, color, prices, currency"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildDetailsEditor(plugin.getRarityManager().getRarity(rarity.getId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(
                                Component.text("Edit Rewards"),
                                Component.text("Replace the full rewards list"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        player.showDialog(buildRewardsEditor(plugin.getRarityManager().getRarity(rarity.getId())));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        )
                ), null, 2)));
    }

    private @NotNull Dialog buildDetailsEditor(final @NotNull Rarity rarity) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit rarity details: " + rarity.getId()))
                        .body(List.of(DialogBody.plainMessage(Component.text("Update the scalar rarity fields below."), 320)))
                        .inputs(List.of(
                                DialogInput.text(DISPLAY_NAME_KEY, Component.text("Display Name")).initial(rarity.getDisplayName()).maxLength(128).width(320).build(),
                                DialogInput.text(DEFAULT_COLOR_KEY, Component.text("Default Color")).initial(orEmpty(rarity.getDefaultColor())).maxLength(64).width(320).build(),
                                DialogInput.text(BUY_PRICE_KEY, Component.text("Buy Price")).initial(String.valueOf(rarity.getBuyPrice())).maxLength(32).width(320).build(),
                                DialogInput.text(SELL_PRICE_KEY, Component.text("Sell Price")).initial(String.valueOf(rarity.getSellPrice())).maxLength(32).width(320).build(),
                                DialogInput.text(CURRENCY_ID_KEY, Component.text("Currency Id")).initial(orEmpty(rarity.getCurrencyId())).maxLength(128).width(320).build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(
                                Component.text("Save"),
                                Component.text("Apply these rarity detail edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyDetailsEdits(
                                                player,
                                                rarity.getId(),
                                                textValue(response.getText(DISPLAY_NAME_KEY)),
                                                textValue(response.getText(DEFAULT_COLOR_KEY)),
                                                textValue(response.getText(BUY_PRICE_KEY)),
                                                textValue(response.getText(SELL_PRICE_KEY)),
                                                textValue(response.getText(CURRENCY_ID_KEY))
                                        );
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                )));
    }

    private @NotNull Dialog buildRewardsEditor(final @NotNull Rarity rarity) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit rarity rewards: " + rarity.getId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Replace the full rewards list. One command per line."), 340),
                                DialogBody.plainMessage(Component.text("Leave blank for no rewards."), 340)
                        ))
                        .inputs(List.of(
                                DialogInput.text(REWARDS_KEY, Component.text("Rewards"))
                                        .initial(joinLines(rarity.getRewards()))
                                        .maxLength(4096)
                                        .width(340)
                                        .multiline(TextDialogInput.MultilineOptions.create(20, 160))
                                        .build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(
                                Component.text("Save Rewards"),
                                Component.text("Apply these reward edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyRewardsEdits(player, rarity.getId(), textValue(response.getText(REWARDS_KEY)));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                )));
    }

    private void reopenEditor(final @NotNull CommandSender sender, final @NotNull String rarityId) {
        if (sender instanceof Player player) {
            player.showDialog(buildEditorMenu(plugin.getRarityManager().getRarity(rarityId)));
        }
    }

    private double parsePrice(final @NotNull String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {
            return -1.00D;
        }
    }

    private @NotNull List<String> parseLines(final @NotNull String input) {
        final List<String> lines = new ArrayList<>();
        if (input.isBlank()) {
            return lines;
        }
        for (String line : input.split("\\R")) {
            final String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return lines;
    }

    private @NotNull String joinLines(final @NotNull List<String> values) {
        return values.stream().reduce((left, right) -> left + "\n" + right).orElse("");
    }

    private @NotNull String textValue(final String input) {
        return input == null ? "" : input.trim();
    }

    private @NotNull String orEmpty(final String input) {
        return input == null ? "" : input;
    }
}
