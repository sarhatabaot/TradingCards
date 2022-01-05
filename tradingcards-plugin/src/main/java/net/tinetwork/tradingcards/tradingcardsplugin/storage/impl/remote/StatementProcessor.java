package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatementProcessor {
    private final String tablePrefix;

    public StatementProcessor(final String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String applyPrefix(final String statement) {
        return statement.replace("{prefix}", tablePrefix);
    }


    private String values(final String statement, Map<String,String> values) {
        final String orderValues = StringUtils.substringBetween(statement, "' (", ") VALUES");
        Map<String, Integer> statementOrder = getColumnOrder(orderValues);

        final String valuesArguments = StringUtils.substringBetween(statement, "VALUES", "WHERE");
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

    private Map<String, Integer> getColumnOrder(final String orderValues) {
        final Map<String, Integer> orderMap = new HashMap<>();
        final String[] order = orderValues.split(", ");
        for (int i = 0; i < order.length - 1; i++) {
            orderMap.put(order[i], i);
        }
        return orderMap;
    }


    private String where(final String statement, Map<String, String> whereValues) {
        final String extractedWhereStatement = extractWhereStatement(statement);
        return replaceWhereStatement(extractedWhereStatement, whereValues);
    }

    public String apply(final String statement,@Nullable Map<String, String> values,@Nullable Map<String, String> whereValues) {
        String finalStatement = applyPrefix(statement);
        if (statement.contains("VALUES") && values != null) {
            finalStatement = values(statement,values);
        }
        if (statement.contains("WHERE") && whereValues != null) {
            finalStatement = where(statement,whereValues);
        }

        return finalStatement;
    }

    private String extractWhereStatement(final String statement) {
        if (StringUtils.substringBetween(statement, "WHERE", "LIMIT") == null)
            return StringUtils.substringBetween(statement, "WHERE");
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


    public ImmutableMap<String,String> generateValuesMap(final UUID playerUuid, final int deckNumber, final String cardId, final String rarityId, final int amount, final Boolean isShiny, final Integer slot) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        if(playerUuid != null) {
            builder.put("uuid",playerUuid.toString());
        }
        if(deckNumber != 0) {
            builder.put("deck_number",String.valueOf(deckNumber));
        }
        if(cardId != null) {
            builder.put("card_id",cardId);
        }
        if(rarityId != null){
            builder.put("rarity_id",rarityId);
        }
        if(amount != 0){
            builder.put("amount",String.valueOf(amount));
        }
        if(isShiny != null) {
            builder.put("is_shiny",isShiny.toString());
        }

        if(slot != null){
            builder.put("slot",slot.toString());
        }

        return builder.build();
    }


}
