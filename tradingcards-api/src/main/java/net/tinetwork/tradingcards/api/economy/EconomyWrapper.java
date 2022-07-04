package net.tinetwork.tradingcards.api.economy;

import org.bukkit.entity.Player;

/**
 * @author sarhatabaot
 * EconomyWrapper for Vault/Treasury
 */
public interface EconomyWrapper {

    /**
     * Withdraw an amount from a player.
     * @param player player
     * @param currencyId currency id
     * @param amount amount to withdraw
     */
    ResponseWrapper withdraw(final Player player, final String currencyId, final double amount);

    /**
     * Deposit an amount to a player.
     * @param player player
     * @param currencyId currency id
     * @param amount amount to deposit
     */
    ResponseWrapper deposit(final Player player, final String currencyId, final double amount);

    ResponseWrapper depositAccount(final String accountId, final String currencyId, final double amount);

    String getProviderName();

    String getPrimaryCurrencyId();
}
