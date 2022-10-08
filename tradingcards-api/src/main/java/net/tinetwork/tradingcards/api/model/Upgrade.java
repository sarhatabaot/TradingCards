package net.tinetwork.tradingcards.api.model;

import net.tinetwork.tradingcards.api.model.pack.PackEntry;

/**
 * @author sarhatabaot
 */
public record Upgrade(String id, PackEntry required, PackEntry result) {

}
