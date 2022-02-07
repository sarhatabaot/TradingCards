package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.chance.Chance;
import net.tinetwork.tradingcards.api.model.chance.EmptyChance;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.SimpleCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil.cardKey;

public class TradingCardManager implements CardManager<TradingCard> {
    private final TradingCards plugin;
    public static final EmptyCard NULL_CARD = new EmptyCard();

    //CardKey,Card<TradingCard>
    private Map<String, Card<TradingCard>> cards;
    //TODO, should instead contain an id instead of the whole object.
    private List<String> activeCards;

    //Contains all cards from a specific series, regardless of rarity
    private Map<String, List<String>> seriesCards;

    //This stores the cards from a single rarity, over multiple files
    private Map<String, List<String>> rarityCardList;
    private Map<String, List<String>> activeRarityCardList;


    public TradingCardManager(final TradingCards plugin) {
        this.plugin = plugin;
        initValues();
    }


    public void initValues() {
        loadAllCards();
        loadActiveCards();
        cacheSeriesCards();
        plugin.getLogger().info(String.format("Loaded %d cards.", cards.size()));
        plugin.getLogger().info(String.format("Loaded %d rarities", rarityCardList.keySet().size()));
        plugin.debug(TradingCardManager.class,StringUtils.join(rarityCardList.keySet(), ","));
        plugin.debug(TradingCardManager.class,StringUtils.join(cards.keySet(), ","));
    }

    public Map<String, List<String>> getSeriesCards() {
        return seriesCards;
    }

    /**
     * Pre-loads all existing cards.
     */
    private void loadAllCards() {
        this.cards = new HashMap<>();
        this.rarityCardList = new HashMap<>();
        var cardConfigs = plugin.getCardsConfig().getCardConfigs();
        for (SimpleCardsConfig simpleCardsConfig : cardConfigs) {
            for (final Rarity rarity : plugin.getRaritiesConfig().rarities()) {
                rarityCardList.put(rarity.getName(), new ArrayList<>());

                var cardNodes = simpleCardsConfig.getCards(rarity.getName()).entrySet();

                for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : cardNodes) {
                    final String cardName = nodeEntry.getValue().key().toString();
                    final String cardKey = cardKey(rarity.getName(), cardName);
                    plugin.debug(TradingCardManager.class,"CardKey="+cardKey);
                    cards.put(cardKey, generateCard(simpleCardsConfig, cardName, rarity.getName()));
                    rarityCardList.get(rarity.getName()).add(cardName);
                }
            }
        }
    }

    //Caches a list of cards in their series.
    private void cacheSeriesCards() {
        this.seriesCards = new HashMap<>();
        for(String series: plugin.getSeriesConfig().series().keySet()) {
            seriesCards.put(series, new ArrayList<>());
            for (Map.Entry<String, Card<TradingCard>> entry : cards.entrySet()) {
                if(entry.getValue().getSeries().getName().equals(series))
                    seriesCards.get(series).add(entry.getValue().getCardName());
            }
        }
    }

    private void loadActiveCards() {
        this.activeCards = new ArrayList<>();
        this.activeRarityCardList = new HashMap<>();
        var cardConfigs = plugin.getCardsConfig().getCardConfigs();
        for (SimpleCardsConfig simpleCardsConfig : cardConfigs) {
            for (final Rarity rarity : plugin.getRaritiesConfig().rarities()) {
                activeRarityCardList.put(rarity.getName(), new ArrayList<>());

                var cardNodes = simpleCardsConfig.getCards(rarity.getName()).entrySet();

                for (Map.Entry<Object, ? extends ConfigurationNode> nodeEntry : cardNodes) {
                    final String cardName = nodeEntry.getValue().key().toString();
                    final String cardKey = cardKey(rarity.getName(), cardName);
                    plugin.debug(TradingCardManager.class,"CardKey=" + cardKey);
                    Card<TradingCard> card = cards.get(cardKey);
                    //A card should only be created if the series exists, checking here for now TODO
                    plugin.debug(TradingCardManager.class,card.toString());
                    if (!plugin.getSeriesConfig().series().containsKey(card.getSeries().getName().toLowerCase())) {
                        plugin.debug(TradingCardManager.class,"This series does not exist, make sure it is in series.yml" + card.getSeries());
                        continue;
                    }
                    //This only loads on startup, that means that it doesn't update. But only on restarts TODO
                    if (card.getSeries().isActive()) {
                        activeRarityCardList.get(rarity.getName()).add(cardName);
                        activeCards.add(cardKey);
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
    public List<String> getActiveCards() {
        return activeCards;
    }

    @Override
    public TradingCard getCard(final String cardName, final String rarity, final boolean forcedShiny) {
        if (cards.containsKey(cardKey(rarity,cardName))) {
            TradingCard card = cards.get(cardKey(rarity, cardName)).get();
            if(card.hasShiny())
                card.isShiny(forcedShiny);
            return card;
        }
        return new EmptyCard();
    }

    @Override
    public TradingCard getActiveCard(final String cardName, final String rarity, final boolean forcedShiny) {
        final String cardKey = cardKey(rarity,cardName);
        if (activeCards.contains(cardKey)) {
            return getCard(cardName,rarity,forcedShiny);
        }
        //fallthrough
        //if it doesn't contain this card for some reason
        return new EmptyCard();
    }

    @Override
    public TradingCard getRandomCard(final String rarity, final boolean forcedShiny) {
        plugin.debug(TradingCardManager.class,"getRandomCard(),rarity=" + rarity);
        var cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarity).size());
        String randomCardName = getRarityCardList(rarity).get(cardIndex);
        return getCard(randomCardName, rarity, forcedShiny);
    }


    public TradingCard getRandomCard(final String rarity, final String series, final boolean forcedShiny) {
        if(series == null)
            return getRandomCard(rarity,forcedShiny);
        //TODO Reallly inefficient
        List<String> raritySeries = new ArrayList<>();
        for(String cardKey: getRarityCardList(rarity)) {
            TradingCard tradingCard = getCard(cardKey,rarity,forcedShiny);
            if(tradingCard.getSeries().getName().equals(series)) {
                raritySeries.add(tradingCard.getCardName());
            }
        }
        var cindex = plugin.getRandom().nextInt(raritySeries.size());
        String randomCardName = raritySeries.get(cindex);
        return getCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public TradingCard getRandomActiveCard(final String rarity, final boolean forcedShiny) {
        if (activeCards.isEmpty()) {
            plugin.debug(TradingCardManager.class,"There are no cards in the active series. Not dropping anything.");
            return new EmptyCard();
        }

        List<String> cardNames = getActiveRarityCardList(rarity);
        var cardIndex = plugin.getRandom().nextInt(cardNames.size());
        String randomCardName = cardNames.get(cardIndex);
        return getActiveCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public ItemStack getCardItem(String cardName, String rarity, int num) {
        TradingCard card = cards.get(cardKey(rarity,cardName)).get();
        ItemStack cardItem = card.build();
        cardItem.setAmount(num);
        return cardItem;
    }

    private int getGeneralMobChance(@NotNull DropType dropType) {
        return switch (dropType.getType()) {
            case "boss" -> plugin.getChancesConfig().bossChance();
            case "hostile" -> plugin.getChancesConfig().hostileChance();
            case "neutral" -> plugin.getChancesConfig().neutralChance();
            case "passive" -> plugin.getChancesConfig().passiveChance();
            default -> 0;
        };
    }

    public String getRandomRarity(DropType dropType, boolean alwaysDrop) {
        int randomDropChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        int mobDropChance = getGeneralMobChance(dropType);
        plugin.debug(TradingCardManager.class,"DropChance=" + randomDropChance +" AlwaysDrop=" + alwaysDrop +" MobType="+ dropType +" MobDropChance="+mobDropChance);
        if (!alwaysDrop && randomDropChance > mobDropChance) {
            return "None";
        }

        int randomRarityChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        plugin.debug(TradingCardManager.class,"RarityChance=" + randomRarityChance);

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            Chance chance = plugin.getChancesConfig().getChance(rarity);
            if (chance instanceof EmptyChance)
                return "None";

            int chanceInt = chance.getFromMobType(dropType);
            if (randomRarityChance < chanceInt)
                return rarity;
        }
        return "None";
    }


    public TradingCard generateCard(final SimpleCardsConfig simpleCardsConfig, final String cardId, final String rarityId) {
        if ("none".equalsIgnoreCase(rarityId)) {
            return NULL_CARD;
        }

        return simpleCardsConfig.getCard(rarityId, cardId).get();
    }


}
