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
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class SeriesEditService {
    private static final String DISPLAY_NAME_KEY = "display_name";
    private static final String MODE_KEY = "mode";
    private static final String SERIES_COLOR_KEY = "series_color";
    private static final String TYPE_COLOR_KEY = "type_color";
    private static final String INFO_COLOR_KEY = "info_color";
    private static final String ABOUT_COLOR_KEY = "about_color";
    private static final String RARITY_COLOR_KEY = "rarity_color";

    private final TradingCards plugin;

    public SeriesEditService(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
    }

    public void openEditor(final @NotNull Player player, final @NotNull String seriesId) {
        player.showDialog(buildEditor(plugin.getSeriesManager().getSeries(seriesId)));
    }

    public boolean applyEdits(
            final @NotNull CommandSender sender,
            final @NotNull String seriesId,
            final @NotNull String displayName,
            final @NotNull String modeInput,
            final @NotNull String seriesColor,
            final @NotNull String typeColor,
            final @NotNull String infoColor,
            final @NotNull String aboutColor,
            final @NotNull String rarityColor
    ) {
        final Mode mode = Mode.getMode(modeInput);
        if (mode == null) {
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.MODE_INCORRECT.formatted(Arrays.toString(Mode.values())));
            return false;
        }

        plugin.getStorage().editSeries(
                seriesId,
                displayName,
                mode,
                new ColorSeries(seriesColor, typeColor, infoColor, aboutColor, rarityColor)
        );
        plugin.getSeriesManager().getCache().refresh(seriesId);
        ChatUtil.sendPrefixedMessage(sender, "&7Updated &bseries &7for &b%s".formatted(seriesId));
        reopenEditor(sender, seriesId);
        return true;
    }

    private @NotNull Dialog buildEditor(final @NotNull Series series) {
        final ColorSeries colors = series.getColorSeries();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Edit series: " + series.getId()))
                        .body(List.of(
                                DialogBody.plainMessage(Component.text("Display: " + series.getDisplayName() + " | Mode: " + series.getMode()), 340),
                                DialogBody.plainMessage(Component.text("Update display name, mode, and the color tokens below."), 340)
                        ))
                        .inputs(List.of(
                                DialogInput.text(DISPLAY_NAME_KEY, Component.text("Display Name")).initial(series.getDisplayName()).maxLength(128).width(320).build(),
                                DialogInput.text(MODE_KEY, Component.text("Mode")).initial(series.getMode().name()).maxLength(32).width(320).build(),
                                DialogInput.text(SERIES_COLOR_KEY, Component.text("Series Color")).initial(colors.getSeries()).maxLength(32).width(320).build(),
                                DialogInput.text(TYPE_COLOR_KEY, Component.text("Type Color")).initial(colors.getType()).maxLength(32).width(320).build(),
                                DialogInput.text(INFO_COLOR_KEY, Component.text("Info Color")).initial(colors.getInfo()).maxLength(32).width(320).build(),
                                DialogInput.text(ABOUT_COLOR_KEY, Component.text("About Color")).initial(colors.getAbout()).maxLength(32).width(320).build(),
                                DialogInput.text(RARITY_COLOR_KEY, Component.text("Rarity Color")).initial(colors.getRarity()).maxLength(32).width(320).build()
                        ))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.confirmation(
                        ActionButton.create(
                                Component.text("Save"),
                                Component.text("Apply these series edits"),
                                120,
                                DialogAction.customClick((response, audience) -> {
                                    if (audience instanceof Player player) {
                                        applyEdits(
                                                player,
                                                series.getId(),
                                                textValue(response.getText(DISPLAY_NAME_KEY)),
                                                textValue(response.getText(MODE_KEY)),
                                                textValue(response.getText(SERIES_COLOR_KEY)),
                                                textValue(response.getText(TYPE_COLOR_KEY)),
                                                textValue(response.getText(INFO_COLOR_KEY)),
                                                textValue(response.getText(ABOUT_COLOR_KEY)),
                                                textValue(response.getText(RARITY_COLOR_KEY))
                                        );
                                    }
                                }, ClickCallback.Options.builder().uses(1).build())
                        ),
                        ActionButton.create(Component.text("Cancel"), Component.text("Discard these changes"), 120, null)
                )));
    }

    private void reopenEditor(final @NotNull CommandSender sender, final @NotNull String seriesId) {
        if (sender instanceof Player player) {
            player.showDialog(buildEditor(plugin.getSeriesManager().getSeries(seriesId)));
        }
    }

    private @NotNull String textValue(final String input) {
        return input == null ? "" : input.trim();
    }
}
