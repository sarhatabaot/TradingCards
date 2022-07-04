/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Minecraft;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.PacksRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Row5;
import org.jooq.Schema;
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
public class Packs extends TableImpl<PacksRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>minecraft.packs</code>
     */
    public static final Packs PACKS = new Packs();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PacksRecord> getRecordType() {
        return PacksRecord.class;
    }

    /**
     * The column <code>minecraft.packs.pack_id</code>.
     */
    public final TableField<PacksRecord, String> PACK_ID = createField(DSL.name("pack_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>minecraft.packs.display_name</code>.
     */
    public final TableField<PacksRecord, String> DISPLAY_NAME = createField(DSL.name("display_name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>minecraft.packs.buy_price</code>.
     */
    public final TableField<PacksRecord, Double> BUY_PRICE = createField(DSL.name("buy_price"), SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>minecraft.packs.permission</code>.
     */
    public final TableField<PacksRecord, String> PERMISSION = createField(DSL.name("permission"), SQLDataType.VARCHAR(200), this, "");

    public final TableField<PacksRecord, String> CURRENCY_ID = createField(DSL.name("currency_id"), SQLDataType.VARCHAR(30), this, "");

    private Packs(Name alias, Table<PacksRecord> aliased) {
        this(alias, aliased, null);
    }

    private Packs(Name alias, Table<PacksRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>minecraft.packs</code> table reference
     */
    public Packs(String alias) {
        this(DSL.name(alias), PACKS);
    }

    /**
     * Create an aliased <code>minecraft.packs</code> table reference
     */
    public Packs(Name alias) {
        this(alias, PACKS);
    }

    /**
     * Create a <code>minecraft.packs</code> table reference
     */
    public Packs() {
        this(DSL.name("packs"), null);
    }

    public <O extends Record> Packs(Table<O> child, ForeignKey<O, PacksRecord> key) {
        super(child, key, PACKS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Minecraft.MINECRAFT;
    }

    @Override
    public UniqueKey<PacksRecord> getPrimaryKey() {
        return Keys.KEY_PACKS_PRIMARY;
    }

    @Override
    public Packs as(String alias) {
        return new Packs(DSL.name(alias), this);
    }

    @Override
    public Packs as(Name alias) {
        return new Packs(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Packs rename(String name) {
        return new Packs(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Packs rename(Name name) {
        return new Packs(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, Double, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
