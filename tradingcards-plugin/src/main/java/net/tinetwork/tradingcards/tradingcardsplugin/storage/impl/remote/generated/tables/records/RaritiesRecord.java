/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records;


import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Rarities;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaritiesRecord extends UpdatableRecordImpl<RaritiesRecord> implements Record7<String, String, String, Double, Double, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>{prefix}rarities.RARITY_ID</code>.
     */
    public void setRarityId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>{prefix}rarities.RARITY_ID</code>.
     */
    public String getRarityId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>{prefix}rarities.DISPLAY_NAME</code>.
     */
    public void setDisplayName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>{prefix}rarities.DISPLAY_NAME</code>.
     */
    public String getDisplayName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>{prefix}rarities.DEFAULT_COLOR</code>.
     */
    public void setDefaultColor(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>{prefix}rarities.DEFAULT_COLOR</code>.
     */
    public String getDefaultColor() {
        return (String) get(2);
    }

    /**
     * Setter for <code>{prefix}rarities.BUY_PRICE</code>.
     */
    public void setBuyPrice(Double value) {
        set(3, value);
    }

    /**
     * Getter for <code>{prefix}rarities.BUY_PRICE</code>.
     */
    public Double getBuyPrice() {
        return (Double) get(3);
    }

    /**
     * Setter for <code>{prefix}rarities.SELL_PRICE</code>.
     */
    public void setSellPrice(Double value) {
        set(4, value);
    }

    /**
     * Getter for <code>{prefix}rarities.SELL_PRICE</code>.
     */
    public Double getSellPrice() {
        return (Double) get(4);
    }

    /**
     * Setter for <code>{prefix}rarities.CURRENCY_ID</code>.
     */
    public void setCurrencyId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>{prefix}rarities.CURRENCY_ID</code>.
     */
    public String getCurrencyId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>{prefix}rarities.CUSTOM_ORDER</code>.
     */
    public void setCustomOrder(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>{prefix}rarities.CUSTOM_ORDER</code>.
     */
    public Integer getCustomOrder() {
        return (Integer) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, String, String, Double, Double, String, Integer> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<String, String, String, Double, Double, String, Integer> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Rarities.RARITIES.RARITY_ID;
    }

    @Override
    public Field<String> field2() {
        return Rarities.RARITIES.DISPLAY_NAME;
    }

    @Override
    public Field<String> field3() {
        return Rarities.RARITIES.DEFAULT_COLOR;
    }

    @Override
    public Field<Double> field4() {
        return Rarities.RARITIES.BUY_PRICE;
    }

    @Override
    public Field<Double> field5() {
        return Rarities.RARITIES.SELL_PRICE;
    }

    @Override
    public Field<String> field6() {
        return Rarities.RARITIES.CURRENCY_ID;
    }

    @Override
    public Field<Integer> field7() {
        return Rarities.RARITIES.CUSTOM_ORDER;
    }

    @Override
    public String component1() {
        return getRarityId();
    }

    @Override
    public String component2() {
        return getDisplayName();
    }

    @Override
    public String component3() {
        return getDefaultColor();
    }

    @Override
    public Double component4() {
        return getBuyPrice();
    }

    @Override
    public Double component5() {
        return getSellPrice();
    }

    @Override
    public String component6() {
        return getCurrencyId();
    }

    @Override
    public Integer component7() {
        return getCustomOrder();
    }

    @Override
    public String value1() {
        return getRarityId();
    }

    @Override
    public String value2() {
        return getDisplayName();
    }

    @Override
    public String value3() {
        return getDefaultColor();
    }

    @Override
    public Double value4() {
        return getBuyPrice();
    }

    @Override
    public Double value5() {
        return getSellPrice();
    }

    @Override
    public String value6() {
        return getCurrencyId();
    }

    @Override
    public Integer value7() {
        return getCustomOrder();
    }

    @Override
    public RaritiesRecord value1(String value) {
        setRarityId(value);
        return this;
    }

    @Override
    public RaritiesRecord value2(String value) {
        setDisplayName(value);
        return this;
    }

    @Override
    public RaritiesRecord value3(String value) {
        setDefaultColor(value);
        return this;
    }

    @Override
    public RaritiesRecord value4(Double value) {
        setBuyPrice(value);
        return this;
    }

    @Override
    public RaritiesRecord value5(Double value) {
        setSellPrice(value);
        return this;
    }

    @Override
    public RaritiesRecord value6(String value) {
        setCurrencyId(value);
        return this;
    }

    @Override
    public RaritiesRecord value7(Integer value) {
        setCustomOrder(value);
        return this;
    }

    @Override
    public RaritiesRecord values(String value1, String value2, String value3, Double value4, Double value5, String value6, Integer value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RaritiesRecord
     */
    public RaritiesRecord() {
        super(Rarities.RARITIES);
    }

    /**
     * Create a detached, initialised RaritiesRecord
     */
    public RaritiesRecord(String rarityId, String displayName, String defaultColor, Double buyPrice, Double sellPrice, String currencyId, Integer customOrder) {
        super(Rarities.RARITIES);

        setRarityId(rarityId);
        setDisplayName(displayName);
        setDefaultColor(defaultColor);
        setBuyPrice(buyPrice);
        setSellPrice(sellPrice);
        setCurrencyId(currencyId);
        setCustomOrder(customOrder);
    }
}
