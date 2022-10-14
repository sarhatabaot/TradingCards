/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import java.util.function.Function;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.DefaultSchema;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.UpgradesRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function1;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row1;
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
public class Upgrades extends TableImpl<UpgradesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>{prefix}upgrades</code>
     */
    public static final Upgrades UPGRADES = new Upgrades();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UpgradesRecord> getRecordType() {
        return UpgradesRecord.class;
    }

    /**
     * The column <code>{prefix}upgrades.upgrade_id</code>.
     */
    public final TableField<UpgradesRecord, String> UPGRADE_ID = createField(DSL.name("upgrade_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    private Upgrades(Name alias, Table<UpgradesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Upgrades(Name alias, Table<UpgradesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>{prefix}upgrades</code> table reference
     */
    public Upgrades(String alias) {
        this(DSL.name(alias), UPGRADES);
    }

    /**
     * Create an aliased <code>{prefix}upgrades</code> table reference
     */
    public Upgrades(Name alias) {
        this(alias, UPGRADES);
    }

    /**
     * Create a <code>{prefix}upgrades</code> table reference
     */
    public Upgrades() {
        this(DSL.name("{prefix}upgrades"), null);
    }

    public <O extends Record> Upgrades(Table<O> child, ForeignKey<O, UpgradesRecord> key) {
        super(child, key, UPGRADES);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<UpgradesRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_B;
    }

    @Override
    public Upgrades as(String alias) {
        return new Upgrades(DSL.name(alias), this);
    }

    @Override
    public Upgrades as(Name alias) {
        return new Upgrades(alias, this);
    }

    @Override
    public Upgrades as(Table<?> alias) {
        return new Upgrades(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Upgrades rename(String name) {
        return new Upgrades(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Upgrades rename(Name name) {
        return new Upgrades(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Upgrades rename(Table<?> name) {
        return new Upgrades(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row1 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row1<String> fieldsRow() {
        return (Row1) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function1<? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function1<? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
