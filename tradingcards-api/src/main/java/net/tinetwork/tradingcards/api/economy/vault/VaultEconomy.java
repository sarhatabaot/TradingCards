package net.tinetwork.tradingcards.api.economy.vault;

import net.milkbowl.vault.economy.Economy;
import net.tinetwork.tradingcards.api.economy.EconomyWrapper;
import net.tinetwork.tradingcards.api.economy.ResponseWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author sarhatabaot
 */
public class VaultEconomy implements EconomyWrapper {
    private final Economy provider;

    public VaultEconomy(final Economy provider) {
        this.provider = provider;
    }


    @Override
    public ResponseWrapper withdraw(final Player player, final String currencyId, final double amount) {
        return new VaultResponse(provider.withdrawPlayer(player, amount));
    }

    @Override
    public ResponseWrapper deposit(final Player player, final String currencyId, final double amount) {
        return new VaultResponse(provider.depositPlayer(player, amount));
    }

    @Override
    public String getProviderName() {
        return provider.getName();
    }

    @Override
    public ResponseWrapper depositAccount(final String accountId, final String currencyId, final double amount) {
        return new VaultResponse(provider.bankDeposit(accountId,amount));
    }

    @Override
    public String getPrimaryCurrencyId() {
        if(provider.currencyNameSingular() == null)
            return provider.currencyNamePlural();
        return provider.currencyNameSingular();
    }
}
