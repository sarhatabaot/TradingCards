package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.model.Chance;
import net.tinetwork.tradingcards.api.model.EmptyChance;
import net.tinetwork.tradingcards.api.model.MobType;
import net.tinetwork.tradingcards.api.model.Rarity;
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
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

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

    //This stores the cards from a single rarity, over multiple files
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
        var cardConfigs = plugin.getCardsConfig().getCardConfigs();
        for (SimpleCardsConfig simpleCardsConfig : cardConfigs) {
            for (final Rarity rarity : plugin.getRaritiesConfig().rarities()) {
                rarityCardList.put(rarity.getName(), new ArrayList<>());
                activeRarityCardList.put(rarity.getName(), new ArrayList<>());

                var cardNodes = simpleCardsConfig.getCards(rarity.getName()).entrySet();

                for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : cardNodes) {
                    final String cardName = nodeEntry.getValue().key().toString();
                    cards.put(rarity.getName() + "." + cardName, generateCard(simpleCardsConfig, cardName, rarity.getName(), false));
                    rarityCardList.get(rarity.getName()).add(cardName);
                    if (plugin.getGeneralConfig().activeSeries().contains(simpleCardsConfig.series(rarity.getName(), cardName))) {
                        activeRarityCardList.get(rarity.getName()).add(cardName);
                        activeCards.put(rarity + "." + cardName, cards.get(rarity.getName() + "." + cardName));
                    }
                }
            }
        }
    }

    @Override
    public List<String> getRarityCardList(final String rarity) {
        return rarityCardList.get(rarity);
    }

    @Override
    public List<String> getActiveRarityCardList(final String rarity) {
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
        return new NullCard();
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
            return new NullCard();
        }
        List<String> cardNames = getActiveRarityCardList(rarity);
        var cardIndex = plugin.getRandom().nextInt(cardNames.size());
        String randomCardName = cardNames.get(cardIndex);
        return getActiveCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public ItemStack getCardItem(String cardName, String rarity, int num) {
        TradingCard card = (TradingCard) cards.get(rarity + "." + cardName);
        ItemStack cardItem = card.build();
        cardItem.setAmount(num);
        return cardItem;
    }

    public String getRandomRarity(MobType mobType) {
        int randomChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            plugin.debug("rarity=" + rarity);
            Chance chance = plugin.getChancesConfig().getChance(rarity);
            if(chance instanceof EmptyChance)
                return "None";

            int chanceInt = chance.getFromMobType(mobType);

            if (randomChance < chanceInt)
                return rarity;
        }
        return "None";
    }
    public String getRandomRarity(String mobType) {
        plugin.debug("getRandomRarity=" + mobType);
        if (mobType.equalsIgnoreCase("None"))
            return "None";

        int randomChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            plugin.debug("rarity=" + rarity);
            Chance chance = plugin.getChancesConfig().getChance(rarity);
            if(chance instanceof EmptyChance)
                return "None";

            int chanceInt = chance.getFromMobType(mobType);

            if (randomChance < chanceInt)
                return rarity;
        }
        return "None";
    }


    //Returns none or the mobtype
    public String getMobTypeName(MobType mobType, boolean alwaysDrop) {
        int generatedDropChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        if (alwaysDrop)
            return mobType.name();
        switch (mobType) {
            case BOSS:
                if (generatedDropChance > plugin.getChancesConfig().bossChance()) {
                    return mobType.name();
                }
                break;
            case NEUTRAL:
                if (generatedDropChance > plugin.getChancesConfig().neutralChance()) {
                    return mobType.name();
                }
                break;
            case PASSIVE:
                if (generatedDropChance > plugin.getChancesConfig().passiveChance()) {
                    return mobType.name();
                }
                break;
            case HOSTILE:
                if (generatedDropChance > plugin.getChancesConfig().hostileChance()) {
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
        String mobTypeName = getMobTypeName(CardUtil.getMobType(e), alwaysDrop);
        plugin.debug(mobTypeName);
        if (mobTypeName.equalsIgnoreCase("None"))
            return "None";

        int randomChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getActiveRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            plugin.debug("rarity=" + rarity);
            Chance chance = plugin.getChancesConfig().getChance(rarity);
            if(chance instanceof EmptyChance)
                return "None";

            int chanceInt = chance.getFromMobType(mobTypeName);
            if (alwaysDrop || randomChance < chanceInt)
                return rarity;
        }

        return "None";
    }

    @Override
    @NotNull
    public String getRandomRarity(EntityType e, boolean alwaysDrop) {
        String mobTypeName = getMobTypeName(CardUtil.getMobType(e), alwaysDrop);
        plugin.debug(mobTypeName);
        if (mobTypeName.equalsIgnoreCase("None"))
            return "None";

        int randomChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            plugin.debug("rarity=" + rarity);
            Chance chance = plugin.getChancesConfig().getChance(rarity);
            if(chance instanceof EmptyChance)
                return "None";

            int chanceInt = chance.getFromMobType(mobTypeName);
            if (alwaysDrop || randomChance < chanceInt)
                return rarity;
        }

        return "None";
    }

    @NotNull
    public TradingCard generateCard(final SimpleCardsConfig simpleCardsConfig, final String cardName, final String rarityName, boolean forcedShiny) {
        if ("None".equalsIgnoreCase(rarityName))
            return new NullCard();

        TradingCard builder = new TradingCard(cardName);
        boolean isShiny = false;
        if (simpleCardsConfig.hasShinyVersion(rarityName, cardName))
            isShiny = calculateIfShiny(forcedShiny);

        final String series = simpleCardsConfig.series(rarityName, cardName);
        final String about = simpleCardsConfig.about(rarityName, cardName);
        final String type = simpleCardsConfig.type(rarityName, cardName);
        final String info = simpleCardsConfig.info(rarityName, cardName);
        final double buyPrice = simpleCardsConfig.buyPrice(rarityName, cardName);
        final double sellPrice = simpleCardsConfig.sellPrice(rarityName, cardName);
        boolean isPlayerCard = isPlayerCard(cardName);
        builder.isShiny(isShiny)
                .series(series)
                .type(type)
                .info(info)
                .isPlayerCard(isPlayerCard)
                .buyPrice(buyPrice)
                .sellPrice(sellPrice)
                .rarity(rarityName);
        if (!about.isEmpty())
            builder.about(about);
        return builder.get();
    }

    private boolean calculateIfShiny(boolean forcedShiny) {
        if (forcedShiny)
            return true;
        int shinyRandom = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        return shinyRandom <= plugin.getChancesConfig().shinyVersionChance();
    }

    //TODO
    public boolean isPlayerCard(String name) {
        String rarity = plugin.getConfig().getString("General.Auto-Add-Player-Rarity");
        return !getCard(name, rarity, false).getCardName().equals("nullCard") && getCard(name, rarity, false).isPlayerCard();
    }

}
