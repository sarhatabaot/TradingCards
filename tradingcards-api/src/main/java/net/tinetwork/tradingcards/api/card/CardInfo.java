package net.tinetwork.tradingcards.api.card;

import lombok.Data;


/**
 * Data class for CardInfo
 *
 */
@Data
@Deprecated
// This class is stupid, color and display should only be referenced when creating the itemstack,
// otherwise we don't care about them.
public class CardInfo {
    private final String name;
    private final String colour;
    private final String display;
}
