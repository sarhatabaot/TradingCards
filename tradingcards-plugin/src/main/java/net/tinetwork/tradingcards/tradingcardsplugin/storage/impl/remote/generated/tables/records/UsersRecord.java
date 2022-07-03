package net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.records;

import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated.tables.Users;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * @author sarhatabaot
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsersRecord extends UpdatableRecordImpl<UsersRecord> implements Record3<Integer, String, String> {
    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>minecraft.users.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>minecraft.users.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>minecraft.users.uuid</code>.
     */
    public void setUuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>minecraft.users.uuid</code>.
     */
    public String getUuid() {
        return (String) get(1);
    }

    /**
     * Getter for <code>minecraft.users.displayname</code>.
     */
    public String getDisplayname() {
        return (String) get(2);
    }

    /**
     * Setter for <code>minecraft.users.displayname</code>.
     */
    public void setDisplayname(String value) {
        set(2, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Users.USERS.ID;
    }

    @Override
    public Field<String> field2() {
        return Users.USERS.UUID;
    }

    @Override
    public Field<String> field3() {
        return Users.USERS.USERNAME;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getUuid();
    }

    @Override
    public String component3() {
        return getDisplayname();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getUuid();
    }

    @Override
    public String value3() {
        return getDisplayname();
    }

    @Override
    public UsersRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public UsersRecord value2(String value) {
        setUuid(value);
        return this;
    }

    @Override
    public UsersRecord value3(String value) {
        setDisplayname(value);
        return this;
    }

    @Override
    public UsersRecord values(Integer value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UsersRecord
     */
    public UsersRecord() {
        super(Users.USERS);
    }

    /**
     * Create a detached, initialised UsersRecord
     */
    public UsersRecord(Integer id, String uuid, String displayname) {
        super(Users.USERS);

        setId(id);
        setUuid(uuid);
        setDisplayname(displayname);
    }
}
