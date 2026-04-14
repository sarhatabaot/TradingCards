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
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TypeEditService {
    private static final String DISPLAY_NAME_KEY = "display_name";
    private static final String TYPE_KEY = "type";

    private final TradingCards plugin;

    public TypeEditService(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    public void openEditor(final @NotNull Player player, final @NotNull String typeId) {
        player.showDialog(buildEditor(plugin.getDropTypeManager().getType(typeId)));
    }

    public boolean applyEdits(final @NotNull CommandSender sender, final @NotNull String typeId, final @NotNull String displayName, final @NotNull String type) {
        final List<String> defaultTypes = plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList();
        if (!defaultTypes.contains(type)) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.TYPE_MUST_BE.formatted(defaultTypes));
            return false;
        }

        plugin.getStorage().editCustomType(typeId, displayName, type);
        plugin.getDropTypeManager().getCache().refresh(typeId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &btype &7for &b%s".formatted(typeId));
        reopenEditor(sender, typeId);
        return true;
    }

    private @NotNull Dialog buildEditor(final @NotNull DropType dropType) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit type: " + dropType.getId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Display: ").append(ChatUtil.component(dropType.getDisplayName())).append(Component.text(" | Type: " + dropType.getType())), 320),
                                DialogBody.plainMessage(Component.text("Allowed types: " + plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList()), 320)
                        ))
                        .inputs(List.of(
                                DialogInput.text(DISPLAY_NAME_KEY, Component.text("Display Name")).initial(dropType.getDisplayName()).maxLength(128).width(320).build(),
                                DialogInput.text(TYPE_KEY, Component.text("Type")).initial(dropType.getType()).maxLength(32).width(320).build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(
                                Component.text("Save"),
                                Component.text("Apply these type edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyEdits(player, dropType.getId(), textValue(response.getText(DISPLAY_NAME_KEY)), textValue(response.getText(TYPE_KEY)));
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                )));
    }

    private void reopenEditor(final @NotNull CommandSender sender, final @NotNull String typeId) {
        if (sender instanceof Player player) {
            player.showDialog(buildEditor(plugin.getDropTypeManager().getType(typeId)));
        }
    }

    private @NotNull String textValue(final String input) {
        return input == null ? "" : input.trim();
    }
}
