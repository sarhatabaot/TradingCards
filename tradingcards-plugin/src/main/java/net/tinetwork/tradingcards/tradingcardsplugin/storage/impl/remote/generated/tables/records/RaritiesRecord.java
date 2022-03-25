/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records;


import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Rarities;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RaritiesRecord extends UpdatableRecordImpl<RaritiesRecord> implements Record5<String, String, String, Double, Double> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>minecraft.rarities.rarity_id</code>.
     */
    public void setRarityId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>minecraft.rarities.rarity_id</code>.
     */
    public String getRarityId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>minecraft.rarities.display_name</code>.
     */
    public void setDisplayName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>minecraft.rarities.display_name</code>.
     */
    public String getDisplayName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>minecraft.rarities.default_color</code>.
     */
    public void setDefaultColor(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>minecraft.rarities.default_color</code>.
     */
    public String getDefaultColor() {
        return (String) get(2);
    }

    /**
     * Setter for <code>minecraft.rarities.buy_price</code>.
     */
    public void setBuyPrice(Double value) {
        set(3, value);
    }

    /**
     * Getter for <code>minecraft.rarities.buy_price</code>.
     */
    public Double getBuyPrice() {
        return (Double) get(3);
    }

    /**
     * Setter for <code>minecraft.rarities.sell_price</code>.
     */
    public void setSellPrice(Double value) {
        set(4, value);
    }

    /**
     * Getter for <code>minecraft.rarities.sell_price</code>.
     */
    public Double getSellPrice() {
        return (Double) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, String, Double, Double> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<String, String, String, Double, Double> valuesRow() {
        return (Row5) super.valuesRow();
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
    public RaritiesRecord values(String value1, String value2, String value3, Double value4, Double value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
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
    public RaritiesRecord(String rarityId, String displayName, String defaultColor, Double buyPrice, Double sellPrice) {
        super(Rarities.RARITIES);

        setRarityId(rarityId);
        setDisplayName(displayName);
        setDefaultColor(defaultColor);
        setBuyPrice(buyPrice);
        setSellPrice(sellPrice);
    }
}
