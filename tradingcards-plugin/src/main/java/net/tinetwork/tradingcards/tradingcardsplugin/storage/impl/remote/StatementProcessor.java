package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import com.google.common.collect.ImmutableMap;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatementProcessor {
    private final TradingCards plugin;
    private final String tablePrefix;

    public StatementProcessor(final String tablePrefix, final TradingCards plugin) {
        this.tablePrefix = tablePrefix;
        this.plugin = plugin;
    }

    public String applyPrefix(final String statement) {
        return statement.replace("{prefix}", tablePrefix);
    }

    private String getValuesArguments(final String statement) {
        if(statement.contains("WHERE"))
            return StringUtils.substringBetween(statement, "VALUES", "WHERE");
        return StringUtils.substringBetween(statement,"VALUES",";");
    }

    private String values(final String statement, Map<String,String> values) {
        final String orderValues = StringUtils.substringBetween(statement, "decks (", ") VALUES");
        plugin.debug(StatementProcessor.class,orderValues);
        Map<String, Integer> statementOrder = getColumnOrder(orderValues);
        final String valuesArguments = getValuesArguments(statement);
        String[] order = orderValues.split(", ");
        for(Map.Entry<String,String> entry: values.entrySet()){
            final String key = entry.getKey();
            final int index = statementOrder.get(key);
            order[index] = entry.getValue();
        }

        String newValues = valuesArguments;
        for(String newValue: order) {
            newValues = newValues.replaceFirst("\\?", newValue);
        }

        return statement.replace(valuesArguments, newValues);
    }

    @NotNull
    private Map<String, Integer> getColumnOrder(final String orderValues) {
        final Map<String, Integer> orderMap = new HashMap<>();
        final String[] order = orderValues.split(", ");
        for (int i = 0; i < order.length; i++) {
            orderMap.put(order[i], i);
        }
        return orderMap;
    }
    private String set(final String statement, Map<String,String> setValues) {
        final String extractedSetStatement = extractSetStatement(statement);
        plugin.debug(StatementProcessor.class,extractedSetStatement);
        return statement.replace(extractedSetStatement,replaceWhereStatement(extractedSetStatement,setValues));
    }

    private String where(final String statement, Map<String, String> whereValues) {
        final String extractedWhereStatement = extractWhereStatement(statement);
        plugin.debug(StatementProcessor.class,"extractedWhereStatement="+extractedWhereStatement);
        return statement.replace(extractedWhereStatement,replaceWhereStatement(extractedWhereStatement,whereValues));
    }

    public String apply(final String statement,@Nullable Map<String, String> values,@Nullable Map<String, String> whereValues, @Nullable Map<String,String> setValues) {
        plugin.debug(StatementProcessor.class,"base statement="+statement);
        String finalStatement = applyPrefix(statement);
        plugin.debug(StatementProcessor.class,"prefixed statement="+finalStatement);
        if (statement.contains("VALUES") && values != null) {
            finalStatement = values(finalStatement,values);
        }
        if (statement.contains("WHERE") && whereValues != null) {
            finalStatement = where(finalStatement,whereValues);
        }
        if(statement.contains("SET") && setValues != null) {
            finalStatement = set(finalStatement,setValues);
        }
        plugin.debug(StatementProcessor.class,"final statement="+finalStatement);
        return finalStatement;
    }


    public String apply(final String statement,@Nullable Map<String, String> values,@Nullable Map<String, String> whereValues) {
        plugin.debug(StatementProcessor.class,"base statement="+statement);
        String finalStatement = applyPrefix(statement);
        plugin.debug(StatementProcessor.class,"prefixed statement="+finalStatement);
        if (statement.contains("VALUES") && values != null) {
            finalStatement = values(finalStatement,values);
        }
        if (statement.contains("WHERE") && whereValues != null) {
            finalStatement = where(finalStatement,whereValues);
        }
        plugin.debug(StatementProcessor.class,"final statement="+finalStatement);
        return finalStatement;
    }
    private String extractSetStatement(final String statement) {
        if (StringUtils.substringBetween(statement, "SET", "WHERE") == null)
            return StringUtils.substringBetween(statement, "SET",";");
        return StringUtils.substringBetween(statement, "SET", "WHERE");
    }
    private String extractWhereStatement(final String statement) {
        if (StringUtils.substringBetween(statement, "WHERE", "LIMIT") == null)
            return StringUtils.substringBetween(statement, "WHERE",";");
        return StringUtils.substringBetween(statement, "WHERE", "LIMIT");
    }

    public String replaceWhereStatement(final String whereStatement, Map<String, String> argumentValues) {
        String replacedStatement = whereStatement;
        for (Map.Entry<String, String> entry : argumentValues.entrySet()) {
            replacedStatement = replaceWhereArgument(replacedStatement, entry.getKey(), entry.getValue());
        }
        return replacedStatement;
    }

    public String replaceWhereArgument(final String base, final String argument, final String value) {
        String toReplace = argument + "=?";
        String newValue = toReplace.replace("?", value);
        return base.replace(toReplace, newValue);
    }

    public ImmutableMap<String,String> generateValuesMap(final UUID playerUuid, final int deckNumber, final String cardId, final String rarityId, final int amount, final Boolean isShiny) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        if(playerUuid != null) {
            builder.put("uuid",wrap(playerUuid.toString()));
        }
        if(deckNumber != 0) {
            builder.put("deck_number",String.valueOf(deckNumber));
        }
        if(cardId != null) {
            builder.put("card_id",wrap(cardId));
        }
        if(rarityId != null){
            builder.put("rarity_id",wrap(rarityId));
        }
        if(amount != 0){
            builder.put("amount",String.valueOf(amount));
        }
        if(isShiny != null) {
            builder.put("is_shiny",isShiny.toString());
        }

        return builder.build();
    }

    //TODO Should look into which statements need this exactly, and just apply it there. Instead of dynamically adding it.
    public String wrap(final String string) {
        return "'" + string + "'";
    }

    public String unwrap(final String string) {
        return string.replace("'", "");
    }
}
