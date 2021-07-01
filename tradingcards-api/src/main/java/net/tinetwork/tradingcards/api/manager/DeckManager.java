package net.tinetwork.tradingcards.api.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public interface DeckManager {

    void openDeck(Player p, int deckNum);

    @NotNull
    ItemStack createDeckItem(@NotNull final Player p, final int num);

    @NotNull
    ItemStack createDeck(@NotNull final Player player, final int num);

    boolean isDeckMaterial(final Material material);

    boolean isDeck(final ItemStack item);

    boolean hasDeck(@NotNull final Player p, final int num);

    boolean hasCard(Player player, String card, String rarity);

    boolean hasShiny(Player p, String card, String rarity);
}
