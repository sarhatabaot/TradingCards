/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated;


import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Cards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.CustomTypes;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Decks;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Packs;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.PacksContent;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.PacksTrade;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Rarities;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Rewards;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.SeriesColors;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Upgrades;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.UpgradesRequired;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.UpgradesResult;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.CardsRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.CustomTypesRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.DecksRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.PacksContentRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.PacksRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.PacksTradeRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.RaritiesRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.RewardsRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.SeriesColorsRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.SeriesRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.UpgradesRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.UpgradesRequiredRecord;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.UpgradesResultRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in the
 * default schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<CardsRecord> CONSTRAINT_FA = Internal.createUniqueKey(Cards.CARDS, DSL.name("CONSTRAINT_FA"), new TableField[] { Cards.CARDS.ID }, true);
    public static final UniqueKey<CustomTypesRecord> CONSTRAINT_D = Internal.createUniqueKey(CustomTypes.CUSTOM_TYPES, DSL.name("CONSTRAINT_D"), new TableField[] { CustomTypes.CUSTOM_TYPES.TYPE_ID }, true);
    public static final UniqueKey<DecksRecord> CONSTRAINT_F = Internal.createUniqueKey(Decks.DECKS, DSL.name("CONSTRAINT_F"), new TableField[] { Decks.DECKS.ID }, true);
    public static final UniqueKey<PacksRecord> CONSTRAINT_FB = Internal.createUniqueKey(Packs.PACKS, DSL.name("CONSTRAINT_FB"), new TableField[] { Packs.PACKS.PACK_ID }, true);
    public static final UniqueKey<PacksContentRecord> CONSTRAINT_E = Internal.createUniqueKey(PacksContent.PACKS_CONTENT, DSL.name("CONSTRAINT_E"), new TableField[] { PacksContent.PACKS_CONTENT.ID }, true);
    public static final UniqueKey<PacksTradeRecord> CONSTRAINT_9 = Internal.createUniqueKey(PacksTrade.PACKS_TRADE, DSL.name("CONSTRAINT_9"), new TableField[] { PacksTrade.PACKS_TRADE.ID }, true);
    public static final UniqueKey<RaritiesRecord> CONSTRAINT_7 = Internal.createUniqueKey(Rarities.RARITIES, DSL.name("CONSTRAINT_7"), new TableField[] { Rarities.RARITIES.RARITY_ID }, true);
    public static final UniqueKey<RewardsRecord> CONSTRAINT_3 = Internal.createUniqueKey(Rewards.REWARDS, DSL.name("CONSTRAINT_3"), new TableField[] { Rewards.REWARDS.RARITY_ID }, true);
    public static final UniqueKey<SeriesRecord> CONSTRAINT_7F = Internal.createUniqueKey(Series.SERIES, DSL.name("CONSTRAINT_7F"), new TableField[] { Series.SERIES.SERIES_ID }, true);
    public static final UniqueKey<SeriesColorsRecord> CONSTRAINT_C = Internal.createUniqueKey(SeriesColors.SERIES_COLORS, DSL.name("CONSTRAINT_C"), new TableField[] { SeriesColors.SERIES_COLORS.SERIES_ID }, true);
    public static final UniqueKey<UpgradesRecord> CONSTRAINT_B = Internal.createUniqueKey(Upgrades.UPGRADES, DSL.name("CONSTRAINT_B"), new TableField[] { Upgrades.UPGRADES.UPGRADE_ID }, true);
    public static final UniqueKey<UpgradesRequiredRecord> CONSTRAINT_5 = Internal.createUniqueKey(UpgradesRequired.UPGRADES_REQUIRED, DSL.name("CONSTRAINT_5"), new TableField[] { UpgradesRequired.UPGRADES_REQUIRED.UPGRADE_ID }, true);
    public static final UniqueKey<UpgradesResultRecord> CONSTRAINT_B5 = Internal.createUniqueKey(UpgradesResult.UPGRADES_RESULT, DSL.name("CONSTRAINT_B5"), new TableField[] { UpgradesResult.UPGRADES_RESULT.UPGRADE_ID }, true);
}