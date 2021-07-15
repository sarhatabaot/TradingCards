package net.tinetwork.tradingcards.api.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public interface DeckManager {
    /**
     *
     * @param p
     * @param deckNum
     */
    void openDeck(Player p, int deckNum);

    /**
     *
     * @param p
     * @param num
     * @return
     */
    @NotNull
    ItemStack createDeckItem(@NotNull final Player p, final int num);

    /**
     *
     * @param player
     * @param num
     * @return
     */
    @NotNull
    ItemStack createDeck(@NotNull final Player player, final int num);

    /**
     *
     * @param material
     * @return
     */
    boolean isDeckMaterial(final Material material);

    /**
     *
     * @param item
     * @return
     */
    boolean isDeck(final ItemStack item);

    /**
     *
     * @param p
     * @param num
     * @return
     */
    boolean hasDeck(@NotNull final Player p, final int num);

    /**
     *
     * @param player
     * @param card
     * @param rarity
     * @return
     */
    boolean hasCard(final Player player,final  String card,final  String rarity);

    /**
     *
     * @param player
     * @param card
     * @param rarity
     * @return
     */
    boolean hasShiny(final Player player,final  String card,final  String rarity);
}
