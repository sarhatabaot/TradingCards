package net.tinetwork.tradingcards.api.economy.treasury;

import me.lokka30.treasury.api.common.Cause;
import me.lokka30.treasury.api.common.NamespacedKey;
import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.currency.Currency;
import net.tinetwork.tradingcards.api.economy.EconomyWrapper;
import net.tinetwork.tradingcards.api.economy.ResponseWrapper;
import org.bukkit.entity.Player;

import java.math.BigDecimal;


/**
 * @author sarhatabaot
 */
public class TreasuryEconomy implements EconomyWrapper {
    private final EconomyProvider provider;

    public TreasuryEconomy(final EconomyProvider provider) {
        this.provider = provider;
    }

    @Override
    public ResponseWrapper withdraw(final Player player, final String currencyId, final double amount) {
        ResponseWrapper responseWrapper = new TreasuryResponse();

        provider
                .accountAccessor()
                .player()
                .withUniqueId(player.getUniqueId())
                .get()
                .thenCompose(account -> {
                    Currency currency = provider.findCurrency(currencyId).orElse(provider.getPrimaryCurrency());
                    final Cause<NamespacedKey> cause = Cause.plugin(NamespacedKey.fromString("TradingCards:EconomyWithdraw"));
                    BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
                    return account.withdrawBalance(bigDecimalAmount, cause, currency);
                })
                .thenRun(() -> responseWrapper.setState(true))
                .exceptionally(ex -> {
                    responseWrapper.setState(false);
                    return null;
                })
        ;

        return responseWrapper;
    }

    @Override
    public ResponseWrapper deposit(final Player player, final String currencyId, final double amount) {
        ResponseWrapper responseWrapper = new TreasuryResponse();

        provider
                .accountAccessor()
                .player()
                .withUniqueId(player.getUniqueId())
                .get()
                .thenCompose(account -> {
                    Currency currency = provider.findCurrency(currencyId).orElse(provider.getPrimaryCurrency());
                    final Cause<NamespacedKey> cause = Cause.plugin(NamespacedKey.fromString("TradingCards:EconomyDeposit"));
                    BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
                    return account.depositBalance(bigDecimalAmount, cause, currency);
                })
                .thenRun(() -> responseWrapper.setState(true))
                .exceptionally(ex -> {
                    responseWrapper.setState(false);
                    return null;
                })
        ;

        return responseWrapper;
    }

    @Override
    public ResponseWrapper depositAccount(final String accountId, final String currencyId, final double amount) {
        ResponseWrapper responseWrapper = new TreasuryResponse();
        provider
                .accountAccessor()
                .nonPlayer()
                .withName(accountId)
                .get()
                .thenCompose(account -> {
                    Currency currency = provider.findCurrency(currencyId).orElse(provider.getPrimaryCurrency());
                    final Cause<NamespacedKey> cause = Cause.plugin(NamespacedKey.fromString("TradingCards:EconomyDeposit"));
                    BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
                    return account.depositBalance(bigDecimalAmount, cause, currency);
                })
                .thenRun(() -> responseWrapper.setState(true))
                .exceptionally(ex -> {
                    responseWrapper.setState(false);
                    return null;
                });

        return responseWrapper;
    }

    @Override
    public String getProviderName() {
        return "TreasuryAPI";
    }

    @Override
    public String getPrimaryCurrencyId() {
        return provider.getPrimaryCurrencyId();
    }

}
