package net.tinetwork.tradingcards.tradingcardsplugin.commands.edit;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.tinetwork.tradingcards.api.model.Upgrade;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class UpgradeEditService {
    private static final String REQUIRED_KEY = "required";
    private static final String RESULT_KEY = "result";

    private final TradingCards plugin;

    public UpgradeEditService(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    public void openEditor(final @NotNull Player player, final @NotNull String upgradeId) {
        player.showDialog(buildEditor(plugin.getUpgradeManager().getUpgrade(upgradeId)));
    }

    public boolean applyEdits(final @NotNull CommandSender sender, final @NotNull String upgradeId, final @NotNull String requiredInput, final @NotNull String resultInput) {
        final PackEntry required = parseEntry(sender, requiredInput, "required");
        if (required == null) {
            return false;
        }
        final PackEntry result = parseEntry(sender, resultInput, "result");
        if (result == null) {
            return false;
        }

        plugin.getStorage().editUpgrade(upgradeId, required, result);
        plugin.getUpgradeManager().getCache().refresh(upgradeId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &bupgrade &7for &b%s".formatted(upgradeId));
        reopenEditor(sender, upgradeId);
        return true;
    }

    private @NotNull Dialog buildEditor(final @NotNull Upgrade upgrade) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit upgrade: " + upgrade.id()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Required: " + upgrade.required()), 340),
                                DialogBody.plainMessage(Component.text("Result: " + upgrade.result()), 340),
                                DialogBody.plainMessage(Component.text("Format: rarity:amount:series"), 340)
                        ))
                        .inputs(List.of(
                                DialogInput.text(REQUIRED_KEY, Component.text("Required")).initial(upgrade.required().toString()).maxLength(128).width(340).build(),
                                DialogInput.text(RESULT_KEY, Component.text("Result")).initial(upgrade.result().toString()).maxLength(128).width(340).build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(
                                Component.text("Save"),
                                Component.text("Apply these upgrade edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyEdits(player, upgrade.id(), textValue(response.getText(REQUIRED_KEY)), textValue(response.getText(RESULT_KEY)));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                )));
    }

    private PackEntry parseEntry(final @NotNull CommandSender sender, final @NotNull String input, final @NotNull String fieldName) {
        try {
            final PackEntry entry = PackEntry.fromString(input);
            if (!plugin.getRarityManager().containsRarity(entry.getRarityId())) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_RARITY.formatted(entry.getRarityId()));
                return null;
            }
            final String seriesId = entry.seriesId();
            if (seriesId != null && !plugin.getSeriesManager().containsSeries(seriesId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_SERIES.formatted(seriesId));
                return null;
            }
            return entry;
        } catch (RuntimeException ignored) {
            ChatUtil.sendPrefixedMessage(sender, "&4Invalid %s entry. Expected format: &brarity:amount:series".formatted(fieldName));
            return null;
        }
    }

    private void reopenEditor(final @NotNull CommandSender sender, final @NotNull String upgradeId) {
        if (sender instanceof Player player) {
            player.showDialog(buildEditor(plugin.getUpgradeManager().getUpgrade(upgradeId)));
        }
    }

    private @NotNull String textValue(final String input) {
        return input == null ? "" : input.trim();
    }
}
