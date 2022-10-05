/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import java.util.function.Function;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.DefaultSchema;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.CardsRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function12;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row12;
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
public class Cards extends TableImpl<CardsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>{prefix}cards</code>
     */
    public static final Cards CARDS = new Cards();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CardsRecord> getRecordType() {
        return CardsRecord.class;
    }

    /**
     * The column <code>{prefix}cards.id</code>.
     */
    public final TableField<CardsRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>{prefix}cards.CARD_ID</code>.
     */
    public final TableField<CardsRecord, String> CARD_ID = createField(DSL.name("CARD_ID"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}cards.DISPLAY_NAME</code>.
     */
    public final TableField<CardsRecord, String> DISPLAY_NAME = createField(DSL.name("DISPLAY_NAME"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>{prefix}cards.RARITY_ID</code>.
     */
    public final TableField<CardsRecord, String> RARITY_ID = createField(DSL.name("RARITY_ID"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}cards.HAS_SHINY</code>.
     */
    public final TableField<CardsRecord, Boolean> HAS_SHINY = createField(DSL.name("HAS_SHINY"), SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>{prefix}cards.SERIES_ID</code>.
     */
    public final TableField<CardsRecord, String> SERIES_ID = createField(DSL.name("SERIES_ID"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}cards.INFO</code>.
     */
    public final TableField<CardsRecord, String> INFO = createField(DSL.name("INFO"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>{prefix}cards.CUSTOM_MODEL_DATA</code>.
     */
    public final TableField<CardsRecord, Integer> CUSTOM_MODEL_DATA = createField(DSL.name("CUSTOM_MODEL_DATA"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>{prefix}cards.BUY_PRICE</code>.
     */
    public final TableField<CardsRecord, Double> BUY_PRICE = createField(DSL.name("BUY_PRICE"), SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>{prefix}cards.SELL_PRICE</code>.
     */
    public final TableField<CardsRecord, Double> SELL_PRICE = createField(DSL.name("SELL_PRICE"), SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>{prefix}cards.TYPE_ID</code>.
     */
    public final TableField<CardsRecord, String> TYPE_ID = createField(DSL.name("TYPE_ID"), SQLDataType.VARCHAR(200), this, "");

    /**
     * The column <code>{prefix}cards.CURRENCY_ID</code>.
     */
    public final TableField<CardsRecord, String> CURRENCY_ID = createField(DSL.name("CURRENCY_ID"), SQLDataType.VARCHAR(30).defaultValue(DSL.field("'tc-internal-default'", SQLDataType.VARCHAR)), this, "");

    private Cards(Name alias, Table<CardsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Cards(Name alias, Table<CardsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>{prefix}cards</code> table reference
     */
    public Cards(String alias) {
        this(DSL.name(alias), CARDS);
    }

    /**
     * Create an aliased <code>{prefix}cards</code> table reference
     */
    public Cards(Name alias) {
        this(alias, CARDS);
    }

    /**
     * Create a <code>{prefix}cards</code> table reference
     */
    public Cards() {
        this(DSL.name("{prefix}cards"), null);
    }

    public <O extends Record> Cards(Table<O> child, ForeignKey<O, CardsRecord> key) {
        super(child, key, CARDS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<CardsRecord, Integer> getIdentity() {
        return (Identity<CardsRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<CardsRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_FA;
    }

    @Override
    public Cards as(String alias) {
        return new Cards(DSL.name(alias), this);
    }

    @Override
    public Cards as(Name alias) {
        return new Cards(alias, this);
    }

    @Override
    public Cards as(Table<?> alias) {
        return new Cards(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Cards rename(String name) {
        return new Cards(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Cards rename(Name name) {
        return new Cards(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Cards rename(Table<?> name) {
        return new Cards(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<Integer, String, String, String, Boolean, String, String, Integer, Double, Double, String, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function12<? super Integer, ? super String, ? super String, ? super String, ? super Boolean, ? super String, ? super String, ? super Integer, ? super Double, ? super Double, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function12<? super Integer, ? super String, ? super String, ? super String, ? super Boolean, ? super String, ? super String, ? super Integer, ? super Double, ? super Double, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
