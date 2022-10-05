/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import java.util.function.Function;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.DefaultSchema;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.PacksContentRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function6;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row6;
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
public class PacksContent extends TableImpl<PacksContentRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>{prefix}packs_content</code>
     */
    public static final PacksContent PACKS_CONTENT = new PacksContent();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PacksContentRecord> getRecordType() {
        return PacksContentRecord.class;
    }

    /**
     * The column <code>{prefix}packs_content.id</code>.
     */
    public final TableField<PacksContentRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>{prefix}packs_content.line_number</code>.
     */
    public final TableField<PacksContentRecord, Integer> LINE_NUMBER = createField(DSL.name("line_number"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>{prefix}packs_content.pack_id</code>.
     */
    public final TableField<PacksContentRecord, String> PACK_ID = createField(DSL.name("pack_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}packs_content.rarity_id</code>.
     */
    public final TableField<PacksContentRecord, String> RARITY_ID = createField(DSL.name("rarity_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}packs_content.card_amount</code>.
     */
    public final TableField<PacksContentRecord, String> CARD_AMOUNT = createField(DSL.name("card_amount"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}packs_content.series_id</code>.
     */
    public final TableField<PacksContentRecord, String> SERIES_ID = createField(DSL.name("series_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    private PacksContent(Name alias, Table<PacksContentRecord> aliased) {
        this(alias, aliased, null);
    }

    private PacksContent(Name alias, Table<PacksContentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>{prefix}packs_content</code> table reference
     */
    public PacksContent(String alias) {
        this(DSL.name(alias), PACKS_CONTENT);
    }

    /**
     * Create an aliased <code>{prefix}packs_content</code> table reference
     */
    public PacksContent(Name alias) {
        this(alias, PACKS_CONTENT);
    }

    /**
     * Create a <code>{prefix}packs_content</code> table reference
     */
    public PacksContent() {
        this(DSL.name("{prefix}packs_content"), null);
    }

    public <O extends Record> PacksContent(Table<O> child, ForeignKey<O, PacksContentRecord> key) {
        super(child, key, PACKS_CONTENT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<PacksContentRecord, Integer> getIdentity() {
        return (Identity<PacksContentRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<PacksContentRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_E;
    }

    @Override
    public PacksContent as(String alias) {
        return new PacksContent(DSL.name(alias), this);
    }

    @Override
    public PacksContent as(Name alias) {
        return new PacksContent(alias, this);
    }

    @Override
    public PacksContent as(Table<?> alias) {
        return new PacksContent(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public PacksContent rename(String name) {
        return new PacksContent(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PacksContent rename(Name name) {
        return new PacksContent(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public PacksContent rename(Table<?> name) {
        return new PacksContent(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, Integer, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function6<? super Integer, ? super Integer, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function6<? super Integer, ? super Integer, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
