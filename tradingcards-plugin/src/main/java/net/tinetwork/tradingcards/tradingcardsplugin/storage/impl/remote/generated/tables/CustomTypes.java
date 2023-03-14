/*
 * This file is generated by jOOQ.
 */
package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables;


import java.util.function.Function;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.DefaultSchema;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.Keys;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.enums.CustomTypesDropType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records.CustomTypesRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
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
public class CustomTypes extends TableImpl<CustomTypesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>{prefix}custom_types</code>
     */
    public static final CustomTypes CUSTOM_TYPES = new CustomTypes();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CustomTypesRecord> getRecordType() {
        return CustomTypesRecord.class;
    }

    /**
     * The column <code>{prefix}custom_types.type_id</code>.
     */
    public final TableField<CustomTypesRecord, String> TYPE_ID = createField(DSL.name("type_id"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>{prefix}custom_types.display_name</code>.
     */
    public final TableField<CustomTypesRecord, String> DISPLAY_NAME = createField(DSL.name("display_name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>{prefix}custom_types.drop_type</code>.
     */
    public final TableField<CustomTypesRecord, CustomTypesDropType> DROP_TYPE = createField(DSL.name("drop_type"), SQLDataType.VARCHAR.asEnumDataType(net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.enums.CustomTypesDropType.class), this, "");

    private CustomTypes(Name alias, Table<CustomTypesRecord> aliased) {
        this(alias, aliased, null);
    }

    private CustomTypes(Name alias, Table<CustomTypesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>{prefix}custom_types</code> table reference
     */
    public CustomTypes(String alias) {
        this(DSL.name(alias), CUSTOM_TYPES);
    }

    /**
     * Create an aliased <code>{prefix}custom_types</code> table reference
     */
    public CustomTypes(Name alias) {
        this(alias, CUSTOM_TYPES);
    }

    /**
     * Create a <code>{prefix}custom_types</code> table reference
     */
    public CustomTypes() {
        this(DSL.name("{prefix}custom_types"), null);
    }

    public <O extends Record> CustomTypes(Table<O> child, ForeignKey<O, CustomTypesRecord> key) {
        super(child, key, CUSTOM_TYPES);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<CustomTypesRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_D;
    }

    @Override
    public CustomTypes as(String alias) {
        return new CustomTypes(DSL.name(alias), this);
    }

    @Override
    public CustomTypes as(Name alias) {
        return new CustomTypes(alias, this);
    }

    @Override
    public CustomTypes as(Table<?> alias) {
        return new CustomTypes(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CustomTypes rename(String name) {
        return new CustomTypes(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CustomTypes rename(Name name) {
        return new CustomTypes(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CustomTypes rename(Table<?> name) {
        return new CustomTypes(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, CustomTypesDropType> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super String, ? super String, ? super CustomTypesDropType, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super String, ? super String, ? super CustomTypesDropType, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}