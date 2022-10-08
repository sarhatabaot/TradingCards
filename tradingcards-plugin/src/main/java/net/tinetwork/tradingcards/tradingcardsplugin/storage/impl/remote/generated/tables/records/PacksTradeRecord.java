/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records;


import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.PacksTrade;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PacksTradeRecord extends UpdatableRecordImpl<PacksTradeRecord> implements Record6<Integer, Integer, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>{prefix}packs_trade.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>{prefix}packs_trade.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>{prefix}packs_trade.line_number</code>.
     */
    public void setLineNumber(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>{prefix}packs_trade.line_number</code>.
     */
    public Integer getLineNumber() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>{prefix}packs_trade.pack_id</code>.
     */
    public void setPackId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>{prefix}packs_trade.pack_id</code>.
     */
    public String getPackId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>{prefix}packs_trade.rarity_id</code>.
     */
    public void setRarityId(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>{prefix}packs_trade.rarity_id</code>.
     */
    public String getRarityId() {
        return (String) get(3);
    }

    /**
     * Setter for <code>{prefix}packs_trade.card_amount</code>.
     */
    public void setCardAmount(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>{prefix}packs_trade.card_amount</code>.
     */
    public String getCardAmount() {
        return (String) get(4);
    }

    /**
     * Setter for <code>{prefix}packs_trade.series_id</code>.
     */
    public void setSeriesId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>{prefix}packs_trade.series_id</code>.
     */
    public String getSeriesId() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, Integer, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Integer, Integer, String, String, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return PacksTrade.PACKS_TRADE.ID;
    }

    @Override
    public Field<Integer> field2() {
        return PacksTrade.PACKS_TRADE.LINE_NUMBER;
    }

    @Override
    public Field<String> field3() {
        return PacksTrade.PACKS_TRADE.PACK_ID;
    }

    @Override
    public Field<String> field4() {
        return PacksTrade.PACKS_TRADE.RARITY_ID;
    }

    @Override
    public Field<String> field5() {
        return PacksTrade.PACKS_TRADE.CARD_AMOUNT;
    }

    @Override
    public Field<String> field6() {
        return PacksTrade.PACKS_TRADE.SERIES_ID;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getLineNumber();
    }

    @Override
    public String component3() {
        return getPackId();
    }

    @Override
    public String component4() {
        return getRarityId();
    }

    @Override
    public String component5() {
        return getCardAmount();
    }

    @Override
    public String component6() {
        return getSeriesId();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getLineNumber();
    }

    @Override
    public String value3() {
        return getPackId();
    }

    @Override
    public String value4() {
        return getRarityId();
    }

    @Override
    public String value5() {
        return getCardAmount();
    }

    @Override
    public String value6() {
        return getSeriesId();
    }

    @Override
    public PacksTradeRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public PacksTradeRecord value2(Integer value) {
        setLineNumber(value);
        return this;
    }

    @Override
    public PacksTradeRecord value3(String value) {
        setPackId(value);
        return this;
    }

    @Override
    public PacksTradeRecord value4(String value) {
        setRarityId(value);
        return this;
    }

    @Override
    public PacksTradeRecord value5(String value) {
        setCardAmount(value);
        return this;
    }

    @Override
    public PacksTradeRecord value6(String value) {
        setSeriesId(value);
        return this;
    }

    @Override
    public PacksTradeRecord values(Integer value1, Integer value2, String value3, String value4, String value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PacksTradeRecord
     */
    public PacksTradeRecord() {
        super(PacksTrade.PACKS_TRADE);
    }

    /**
     * Create a detached, initialised PacksTradeRecord
     */
    public PacksTradeRecord(Integer id, Integer lineNumber, String packId, String rarityId, String cardAmount, String seriesId) {
        super(PacksTrade.PACKS_TRADE);

        setId(id);
        setLineNumber(lineNumber);
        setPackId(packId);
        setRarityId(rarityId);
        setCardAmount(cardAmount);
        setSeriesId(seriesId);
    }
}
