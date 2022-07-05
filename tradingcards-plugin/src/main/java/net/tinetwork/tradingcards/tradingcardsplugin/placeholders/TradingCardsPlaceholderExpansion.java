package net.tinetwork.tradingcards.tradingcardsplugin.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sarhatabaot
 */
public class TradingCardsPlaceholderExpansion extends PlaceholderExpansion {
    private final TradingCards plugin;

    public TradingCardsPlaceholderExpansion(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tc";
    }

    @Override
    public @NotNull String getAuthor() {
        return "sarhatabaot";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getName() {
        return "TradingCards";
    }

    @Override
    public @Nullable String onRequest(final OfflinePlayer player, @NotNull final String params) {
        //normal check first
        if(params.startsWith("type_")) {
            return new PlaceholderQuery(params){
                @Override
                protected String onPlaceholderValue() {
                    if("display-name".equalsIgnoreCase(type)) {
                        return plugin.getDropTypeManager().getType(type).getDisplayName();
                    }

                    if("type".equalsIgnoreCase(type)) {
                        return plugin.getDropTypeManager().getType(type).getType();
                    }
                    return null;
                }

                @Override
                public boolean containsType(final String id) {
                    return plugin.getDropTypeManager().containsType(id);
                }
            }.getPlaceholderValue();
        }

        if(params.startsWith("card_")) {
            return new PlaceholderQuery(params) {
                @Override
                protected String onPlaceholderValue() {
                    final String rarityId = id.split("\\.")[0];
                    final String cardId = id.split("\\.")[1];
                    final String seriesId = id.split("\\.")[2];
                    return switch (type) {
                        case "display-name" -> plugin.getCardManager().getCard(cardId, rarityId,seriesId).getDisplayName();
                        case "buy-price" -> String.valueOf(plugin.getCardManager().getCard(cardId, rarityId,seriesId).getBuyPrice());
                        case "sell-price" -> String.valueOf(plugin.getCardManager().getCard(cardId, rarityId,seriesId).getSellPrice());
                        case "info" -> plugin.getCardManager().getCard(cardId, rarityId,seriesId).getInfo();
                        case "about" -> plugin.getCardManager().getCard(cardId, rarityId,seriesId).getAbout();
                        case "type" -> plugin.getCardManager().getCard(cardId, rarityId,seriesId).getType().getId();
                        default -> null;
                    };
                }

                @Override
                public boolean containsType(final String id) {
                    final String rarityId = id.split("\\.")[0];
                    final String cardId = id.split("\\.")[1];
                    final String seriesId = id.split("\\.")[2];
                    return plugin.getCardManager().containsCard(cardId,rarityId,seriesId);
                }
            }.getPlaceholderValue();
        }

        if(params.startsWith("pack_")) {
            return new PlaceholderQuery(params){
                @Override
                public String onPlaceholderValue() {
                    return switch (type) {
                        case "display-name" -> plugin.getPackManager().getPack(id).getDisplayName();
                        case "buy-price" ->String.valueOf(plugin.getPackManager().getPack(id).getBuyPrice());
                        case "permission" -> plugin.getPackManager().getPack(id).getPermission();
                        default -> null;
                    };
                }

                @Override
                public boolean containsType(final String id) {
                    return plugin.getPackManager().containsPack(id);
                }
            }.getPlaceholderValue();
        }

        if(params.startsWith("rarity_")) {
            return new PlaceholderQuery(params){
                @Override
                protected String onPlaceholderValue() {
                    return switch (type) {
                        case "default-color" -> plugin.getRarityManager().getRarity(id).getDefaultColor();
                        case "display-name" -> plugin.getRarityManager().getRarity(id).getDisplayName();
                        case "buy-price" -> String.valueOf(plugin.getRarityManager().getRarity(id).getBuyPrice());
                        case "sell-price" -> String.valueOf(plugin.getRarityManager().getRarity(id).getSellPrice());
                        default -> null;
                    };
                }

                @Override
                public boolean containsType(final String id) {
                    return plugin.getRarityManager().containsRarity(id);
                }
            }.getPlaceholderValue();
        }

        if(params.startsWith("series_")) {
            return new PlaceholderQuery(params){
                @Override
                protected String onPlaceholderValue() {
                    if("mode".equalsIgnoreCase(type)) {
                        return plugin.getSeriesManager().getSeries(id).getMode().name();
                    }
                    if("display-name".equalsIgnoreCase(type)) {
                        return plugin.getSeriesManager().getSeries(id).getDisplayName();
                    }

                    return null;
                }

                @Override
                public boolean containsType(final String id) {
                    return plugin.getSeriesManager().containsSeries(id);
                }
            }.getPlaceholderValue();
        }
        if("version".equalsIgnoreCase(params)) {
            return plugin.getDescription().getVersion();
        }
        if("prefix".equalsIgnoreCase(params)){
            return plugin.getMessagesConfig().prefix();
        }
        return null;
    }


    public abstract static class PlaceholderQuery {
        protected final String id;
        protected final String type;

        protected PlaceholderQuery(@NotNull String params) {
            this.id = params.split("_")[1];
            this.type = params.split("_")[2];
        }
        public String getPlaceholderValue() {
            if(!containsType(id))
                return null;

            return onPlaceholderValue();
        }
        protected abstract String onPlaceholderValue();
        public abstract boolean containsType(String id);
    }


}
