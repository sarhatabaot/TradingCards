/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import java.util.function.Function;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.DefaultSchema;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.RaritiesRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function7;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Rarities extends TableImpl<RaritiesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>{prefix}rarities</code>
     */
    public static final Rarities RARITIES = new Rarities();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RaritiesRecord> getRecordType() {
        return RaritiesRecord.class;
    }

    /**
     * The column <code>{prefix}rarities.rarity_id</code>.
     */
    public final TableField<RaritiesRecord, String> RARITY_ID = createField(DSL.name("rarity_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}rarities.display_name</code>.
     */
    public final TableField<RaritiesRecord, String> DISPLAY_NAME = createField(DSL.name("display_name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>{prefix}rarities.default_color</code>.
     */
    public final TableField<RaritiesRecord, String> DEFAULT_COLOR = createField(DSL.name("default_color"), SQLDataType.VARCHAR(36), this, "");

    /**
     * The column <code>{prefix}rarities.buy_price</code>.
     */
    public final TableField<RaritiesRecord, Double> BUY_PRICE = createField(DSL.name("buy_price"), SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>{prefix}rarities.sell_price</code>.
     */
    public final TableField<RaritiesRecord, Double> SELL_PRICE = createField(DSL.name("sell_price"), SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>{prefix}rarities.currency_id</code>.
     */
    public final TableField<RaritiesRecord, String> CURRENCY_ID = createField(DSL.name("currency_id"), SQLDataType.VARCHAR(30).defaultValue(DSL.field("NULL", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>{prefix}rarities.custom_order</code>.
     */
    public final TableField<RaritiesRecord, Integer> CUSTOM_ORDER = createField(DSL.name("custom_order"), SQLDataType.INTEGER.defaultValue(DSL.field("0", SQLDataType.INTEGER)), this, "");

    private Rarities(Name alias, Table<RaritiesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Rarities(Name alias, Table<RaritiesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>{prefix}rarities</code> table reference
     */
    public Rarities(String alias) {
        this(DSL.name(alias), RARITIES);
    }

    /**
     * Create an aliased <code>{prefix}rarities</code> table reference
     */
    public Rarities(Name alias) {
        this(alias, RARITIES);
    }

    /**
     * Create a <code>{prefix}rarities</code> table reference
     */
    public Rarities() {
        this(DSL.name("{prefix}rarities"), null);
    }

    public <O extends Record> Rarities(Table<O> child, ForeignKey<O, RaritiesRecord> key) {
        super(child, key, RARITIES);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<RaritiesRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_7;
    }

    @Override
    public Rarities as(String alias) {
        return new Rarities(DSL.name(alias), this);
    }

    @Override
    public Rarities as(Name alias) {
        return new Rarities(alias, this);
    }

    @Override
    public Rarities as(Table<?> alias) {
        return new Rarities(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Rarities rename(String name) {
        return new Rarities(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Rarities rename(Name name) {
        return new Rarities(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Rarities rename(Table<?> name) {
        return new Rarities(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, String, String, Double, Double, String, Integer> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function7<? super String, ? super String, ? super String, ? super Double, ? super Double, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function7<? super String, ? super String, ? super String, ? super Double, ? super Double, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}