package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.NullCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.SimpleCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.manager.CardManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TradingCardManager implements CardManager<TradingCard> {
    private final TradingCards plugin;
    private final Map<String, Card<TradingCard>> cards = new HashMap<>();
    private final Map<String, Card<TradingCard>> activeCards = new HashMap<>();

    private static final Map<String, List<String>> rarityCardList = new HashMap<>();
    private static final Map<String, List<String>> activeRarityCardList = new HashMap<>();


    public TradingCardManager(final TradingCards plugin) {
        this.plugin = plugin;
        loadAllCards();
        plugin.getLogger().info(String.format("Loaded %d cards.", cards.size()));
        plugin.getLogger().info(String.format("Loaded %d rarities", rarityCardList.keySet().size()));
        plugin.debug(StringUtils.join(rarityCardList.keySet(), ","));
        plugin.debug(StringUtils.join(cards.keySet(), ","));
    }


    /**
     * Pre-loads all existing cards.
     */
    private void loadAllCards() {
        for (SimpleCardsConfig simpleCardsConfig : plugin.getCardsConfig().getCardConfigs()) {
            for (final String rarity : simpleCardsConfig.getCards().getKeys(false)) {
                rarityCardList.put(rarity, new ArrayList<>());
                for (String name : simpleCardsConfig.getCards().getConfigurationSection(rarity).getKeys(false)) {
                    cards.put(rarity + "." + name, generateCard(simpleCardsConfig, name, rarity, false));
                    rarityCardList.get(rarity).add(name);
                    if (plugin.getMainConfig().activeSeries.contains(simpleCardsConfig.getSeries(rarity, name))) {
                        activeRarityCardList.put(rarity, new ArrayList<>()); //TODO Probably not the most efficient way to do this.
                        activeRarityCardList.get(rarity).add(name);
                        activeCards.put(rarity + "." + name, cards.get(rarity + "." + name));
                    }
                }
            }
        }
    }

    @Override
    public List<String> getRarityCardList(final String rarity) {
        plugin.debug(rarity);
        plugin.debug(StringUtils.join(getRarityNames(), ","));
        return rarityCardList.get(rarity);
    }

    @Override
    public List<String> getActiveRarityCardList(final String rarity) {
        plugin.debug(rarity);
        plugin.debug(StringUtils.join(getRarityNames(), ","));
        return activeRarityCardList.get(rarity);
    }

    @Override
    public Set<String> getRarityNames() {
        return rarityCardList.keySet();
    }

    @Override
    public Set<String> getActiveRarityNames() {
        return activeRarityCardList.keySet();
    }


    @Override
    public Map<String, Card<TradingCard>> getCards() {
        return cards;
    }


    @Override
    public Map<String, Card<TradingCard>> getActiveCards() {
        return activeCards;
    }

    @Override
    public TradingCard getCard(final String cardName, final String rarity, final boolean forcedShiny) {
        if (cards.containsKey(rarity + "." + cardName))
            return (TradingCard) cards.get(rarity + "." + cardName).isShiny(forcedShiny);
        return new NullCard(plugin);
    }

    @Override
    public TradingCard getActiveCard(final String cardName, final String rarity, final boolean forcedShiny) {
        if (activeCards.containsKey(rarity + "." + cardName))
            return (TradingCard) activeCards.get(rarity + "." + cardName);
        //fallthrough
        return getCard(cardName, rarity, forcedShiny);
    }

    @Override
    public TradingCard getRandomCard(final String rarity, final boolean forcedShiny) {
        var cindex = plugin.getRandom().nextInt(getRarityCardList(rarity).size());
        String randomCardName = getRarityCardList(rarity).get(cindex);
        return getCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public TradingCard getRandomActiveCard(final String rarity, final boolean forcedShiny) {
        if (activeCards.keySet().isEmpty()) {
            plugin.getLogger().warning("There are no cards in the active series. Not dropping anything.");
            return new NullCard(plugin);
        }
        var cindex = plugin.getRandom().nextInt(activeCards.keySet().size());
        List<String> cardNames = getActiveRarityCardList(rarity);
        String randomCardName = cardNames.get(cindex);
        return getActiveCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public ItemStack getCardItem(String cardName, String rarity, int num) {
        TradingCard card = (TradingCard) cards.get(rarity + "." + cardName);
        ItemStack cardItem = card.build();
        cardItem.setAmount(num);
        return cardItem;
    }


    public String getRandomRarity(String mobType) {
        plugin.debug("getRandomRarity=" + mobType);
        if (mobType.equalsIgnoreCase("None"))
            return "None";

        int randomChance = plugin.getRandom().nextInt(100000) + 1;

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            plugin.debug("rarity=" + rarity);
            var chance = plugin.getConfig().getInt("Chances." + rarity + "." + mobType, -1);
            if (randomChance < chance)
                return rarity;
        }
        return "None";
    }

    //Returns none or the mobtype
    public String getMobTypeName(CardUtil.MobType mobType, boolean alwaysDrop) {
        int generatedDropChance = plugin.getRandom().nextInt(100) + 1;
        if (alwaysDrop)
            return mobType.name();
        switch (mobType) {
            case BOSS:
                if (generatedDropChance > plugin.getMainConfig().bossChance) {
                    return mobType.name();
                }
                break;
            case NEUTRAL:
                if (generatedDropChance > plugin.getMainConfig().neutralChance) {
                    return mobType.name();
                }
                break;
            case PASSIVE:
                if (generatedDropChance > plugin.getMainConfig().passiveChance) {
                    return mobType.name();
                }
                break;
            case HOSTILE:
                if (generatedDropChance > plugin.getMainConfig().hostileChance) {
                    return mobType.name();
                }
                break;
            default:
                return "none";
        }
        return "none";
    }

    @NotNull
    public String getRandomActiveRarity(EntityType e, boolean alwaysDrop) {
        String mobTypeName = getMobTypeName(CardUtil.getMobType(e),alwaysDrop);
        plugin.debug(mobTypeName);
        if (mobTypeName.equalsIgnoreCase("None"))
            return "None";

        int randomChance = plugin.getRandom().nextInt(100000) + 1;

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getActiveRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            plugin.debug("rarity=" + rarity);
            var chance = plugin.getConfig().getInt("Chances." + rarity + "." + mobTypeName, -1);
            if (alwaysDrop || randomChance < chance)
                return rarity;
        }

        return "None";
    }

    @Override
    @NotNull
    public String getRandomRarity(EntityType e, boolean alwaysDrop) {
        //String mobTypeName = CardUtil.getMobTypeOrNone(e, alwaysDrop);
        String mobTypeName = getMobTypeName(CardUtil.getMobType(e),alwaysDrop);
        plugin.debug(mobTypeName);
        if (mobTypeName.equalsIgnoreCase("None"))
            return "None";

        int randomChance = plugin.getRandom().nextInt(100000) + 1;

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            plugin.debug("rarity=" + rarity);
            var chance = plugin.getConfig().getInt("Chances." + rarity + "." + mobTypeName, -1);
            if (alwaysDrop || randomChance < chance)
                return rarity;
        }

        return "None";
    }

    @NotNull
    public TradingCard generateCard(final SimpleCardsConfig simpleCardsConfig, final String cardName, final String rarityName, boolean forcedShiny) {
        if ("None".equalsIgnoreCase(rarityName))
            return new NullCard(plugin);

        TradingCard builder = new TradingCard(plugin, cardName);
        boolean isShiny = false;
        if (simpleCardsConfig.hasShiny(rarityName, cardName))
            isShiny = calculateIfShiny(forcedShiny);

        final String rarityColor = plugin.getMainConfig().rarityColour;
        final String prefix = plugin.getMainConfig().cardPrefix;

        final String series = simpleCardsConfig.getSeries(rarityName, cardName);
        final String seriesColour = plugin.getMainConfig().seriesColour;
        final String seriesDisplay = plugin.getMainConfig().seriesDisplay;

        final String about = simpleCardsConfig.getAbout(rarityName, cardName);
        final String aboutColour = plugin.getMainConfig().aboutColour;
        final String aboutDisplay = plugin.getMainConfig().aboutDisplay;

        final String type = simpleCardsConfig.getType(rarityName, cardName);
        final String typeColour = plugin.getMainConfig().typeColour;
        final String typeDisplay = plugin.getMainConfig().typeDisplay;

        final String info = simpleCardsConfig.getInfo(rarityName, cardName);
        final String infoColour = plugin.getMainConfig().infoColour;
        final String infoDisplay = plugin.getMainConfig().infoDisplay;

        final String shinyPrefix = plugin.getMainConfig().shinyName;
        final String cost = simpleCardsConfig.getCost(rarityName, cardName);

        boolean isPlayerCard = isPlayerCard(cardName);
        builder.isShiny(isShiny)
                .rarityColour(rarityColor)
                .prefix(prefix)
                .series(series, seriesColour, seriesDisplay)
                .type(type, typeColour, typeDisplay)
                .info(info, infoColour, infoDisplay)
                .shinyPrefix(shinyPrefix)
                .isPlayerCard(isPlayerCard)
                .cost(cost)
                .rarity(rarityName);
        if(!about.isEmpty())
            builder.about(about, aboutColour, aboutDisplay);
        return builder.get();
    }

    private boolean calculateIfShiny(boolean forcedShiny) {
        if (forcedShiny)
            return true;
        int shinyRandom = plugin.getRandom().nextInt(100) + 1;
        return shinyRandom <= plugin.getConfig().getInt("Chances.Shiny-Version-Chance");
    }

    public boolean isPlayerCard(String name) {
        String rarity = plugin.getConfig().getString("General.Auto-Add-Player-Rarity");
        return !getCard(name, rarity, false).getCardName().equals("nullCard") && getCard(name, rarity, false).isPlayerCard();
    }

}
