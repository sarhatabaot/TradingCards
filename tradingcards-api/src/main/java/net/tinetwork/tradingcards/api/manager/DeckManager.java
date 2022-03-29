package net.tinetwork.tradingcards.api.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author sarhatabaot
 */
public interface DeckManager {
    /**
     * @param player
     * @param deckNum
     */
    void openDeck(@NotNull final Player player, final int deckNum);

    /**
     *
     * @param player
     * @param deckNumber
     * @return
     */
    @NotNull
    ItemStack createDeckItem(@NotNull final Player player, final int deckNumber);

    /**
     * @param player
     * @param deckNumber
     * @return
     */
    @NotNull
    ItemStack getNbtItem(@NotNull final Player player, final int deckNumber);

    /**
     * @param material
     * @return
     */
    boolean isDeckMaterial(final Material material);

    /**
     * @param item
     * @return
     */
    boolean isDeck(final ItemStack item);

    /**
     * @param player
     * @param num
     * @return
     */
    boolean hasDeckItem(@NotNull final Player player, final int num);

    /**
     * @param player
     * @param cardId
     * @param rarityId
     * @return
     */
    boolean hasCard(final Player player,final  String cardId,final  String rarityId);

    /**
     * @param player
     * @param cardId
     * @param rarityId
     * @return
     */
    boolean hasShinyCard(final Player player, final  String cardId, final  String rarityId);

    /**
     * @param uuid
     * @param deckNumber
     */
    void createNewDeckInFile(final UUID uuid, final int deckNumber);
}
