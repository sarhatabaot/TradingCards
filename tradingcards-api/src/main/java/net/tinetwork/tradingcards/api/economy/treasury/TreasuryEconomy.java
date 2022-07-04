package net.tinetwork.tradingcards.api.economy.treasury;

import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.account.Account;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import net.tinetwork.tradingcards.api.economy.EconomyWrapper;
import net.tinetwork.tradingcards.api.economy.ResponseWrapper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        final EconomyTransactionInitiator<?> initiator = EconomyTransactionInitiator.createInitiator(EconomyTransactionInitiator.Type.PLAYER, player.getUniqueId());
        ResponseWrapper responseWrapper = new TreasuryResponse();

        provider.retrievePlayerAccount(player.getUniqueId(), new EconomySubscriber<>() {
            @Override
            public void succeed(@NotNull PlayerAccount account) {
                Currency currency = provider.findCurrency(currencyId)
                        .orElse(provider.getPrimaryCurrency());

                BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
                account.withdrawBalance(bigDecimalAmount, initiator, currency, new EconomySubscriber<>() {
                    @Override
                    public void succeed(@NotNull BigDecimal newBalance) {
                        responseWrapper.setState(true);
                    }

                    @Override
                    public void fail(@NotNull EconomyException exception) {
                        responseWrapper.setState(false);
                    }
                });
            }

            @Override
            public void fail(@NotNull EconomyException exception) {
                responseWrapper.setState(false);
            }
        });
        return responseWrapper;
    }

    @Override
    public ResponseWrapper deposit(final Player player, final String currencyId, final double amount) {
        final EconomyTransactionInitiator<?> initiator = EconomyTransactionInitiator.createInitiator(EconomyTransactionInitiator.Type.PLAYER, player.getUniqueId());
        ResponseWrapper responseWrapper = new TreasuryResponse();

        provider.retrievePlayerAccount(player.getUniqueId(), new EconomySubscriber<>() {
            @Override
            public void succeed(@NotNull PlayerAccount account) {
                Currency currency = provider.findCurrency(currencyId)
                        .orElse(provider.getPrimaryCurrency());

                BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
                account.depositBalance(bigDecimalAmount, initiator, currency, new EconomySubscriber<>() {
                    @Override
                    public void succeed(@NotNull BigDecimal newBalance) {
                        responseWrapper.setState(true);
                    }

                    @Override
                    public void fail(@NotNull EconomyException exception) {
                        responseWrapper.setState(false);
                    }
                });
            }

            @Override
            public void fail(@NotNull EconomyException exception) {
                responseWrapper.setState(false);
            }
        });
        return responseWrapper;
    }

    @Override
    public ResponseWrapper depositAccount(final String accountId, final String currencyId, final double amount) {
        final EconomyTransactionInitiator<?> initiator = EconomyTransactionInitiator.createInitiator(EconomyTransactionInitiator.Type.PLUGIN, "TradingCards");
        ResponseWrapper responseWrapper = new TreasuryResponse();

        provider.retrieveAccount(accountId, new EconomySubscriber<>() {
            @Override
            public void succeed(@NotNull final Account account) {
                Currency currency = provider.findCurrency(currencyId)
                        .orElse(provider.getPrimaryCurrency());

                BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
                account.depositBalance(bigDecimalAmount, initiator, currency, new EconomySubscriber<>() {
                    @Override
                    public void succeed(@NotNull BigDecimal newBalance) {
                        responseWrapper.setState(true);
                    }

                    @Override
                    public void fail(@NotNull EconomyException exception) {
                        responseWrapper.setState(false);
                    }
                });
            }

            @Override
            public void fail(@NotNull final EconomyException exception) {
                responseWrapper.setState(false);
            }
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
