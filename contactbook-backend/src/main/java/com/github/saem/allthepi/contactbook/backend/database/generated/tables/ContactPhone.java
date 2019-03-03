/*
 * This file is generated by jOOQ.
 */
package com.github.saem.allthepi.contactbook.backend.database.generated.tables;


import com.github.saem.allthepi.contactbook.backend.database.generated.ContactBook;
import com.github.saem.allthepi.contactbook.backend.database.generated.Indexes;
import com.github.saem.allthepi.contactbook.backend.database.generated.Keys;
import com.github.saem.allthepi.contactbook.backend.database.generated.tables.records.ContactPhoneRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContactPhone extends TableImpl<ContactPhoneRecord> {

    private static final long serialVersionUID = -203388899;

    /**
     * The reference instance of <code>contact_book.contact_phone</code>
     */
    public static final ContactPhone CONTACT_PHONE = new ContactPhone();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContactPhoneRecord> getRecordType() {
        return ContactPhoneRecord.class;
    }

    /**
     * The column <code>contact_book.contact_phone.no</code>.
     */
    public final TableField<ContactPhoneRecord, Long> NO = createField("no", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('contact_book.contact_phone_no_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>contact_book.contact_phone.id</code>.
     */
    public final TableField<ContactPhoneRecord, UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>contact_book.contact_phone.contact_no</code>.
     */
    public final TableField<ContactPhoneRecord, Long> CONTACT_NO = createField("contact_no", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>contact_book.contact_phone.type</code>.
     */
    public final TableField<ContactPhoneRecord, String> TYPE = createField("type", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>contact_book.contact_phone.country_code</code>.
     */
    public final TableField<ContactPhoneRecord, Integer> COUNTRY_CODE = createField("country_code", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>contact_book.contact_phone.area_code</code>.
     */
    public final TableField<ContactPhoneRecord, String> AREA_CODE = createField("area_code", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>contact_book.contact_phone.number</code>.
     */
    public final TableField<ContactPhoneRecord, Long> NUMBER = createField("number", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>contact_book.contact_phone.extension</code>.
     */
    public final TableField<ContactPhoneRecord, String> EXTENSION = createField("extension", org.jooq.impl.SQLDataType.CLOB.nullable(false).defaultValue(org.jooq.impl.DSL.field("''::text", org.jooq.impl.SQLDataType.CLOB)), this, "");

    /**
     * The column <code>contact_book.contact_phone.raw</code>.
     */
    public final TableField<ContactPhoneRecord, String> RAW = createField("raw", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>contact_book.contact_phone.created_at</code>.
     */
    public final TableField<ContactPhoneRecord, Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>contact_book.contact_phone.modified_at</code>.
     */
    public final TableField<ContactPhoneRecord, Timestamp> MODIFIED_AT = createField("modified_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>contact_book.contact_phone.version</code>.
     */
    public final TableField<ContactPhoneRecord, Long> VERSION = createField("version", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>contact_book.contact_phone.trace_id</code>.
     */
    public final TableField<ContactPhoneRecord, String> TRACE_ID = createField("trace_id", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>contact_book.contact_phone</code> table reference
     */
    public ContactPhone() {
        this(DSL.name("contact_phone"), null);
    }

    /**
     * Create an aliased <code>contact_book.contact_phone</code> table reference
     */
    public ContactPhone(String alias) {
        this(DSL.name(alias), CONTACT_PHONE);
    }

    /**
     * Create an aliased <code>contact_book.contact_phone</code> table reference
     */
    public ContactPhone(Name alias) {
        this(alias, CONTACT_PHONE);
    }

    private ContactPhone(Name alias, Table<ContactPhoneRecord> aliased) {
        this(alias, aliased, null);
    }

    private ContactPhone(Name alias, Table<ContactPhoneRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> ContactPhone(Table<O> child, ForeignKey<O, ContactPhoneRecord> key) {
        super(child, key, CONTACT_PHONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return ContactBook.CONTACT_BOOK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CONTACT_PHONE_ID_KEY, Indexes.CONTACT_PHONE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ContactPhoneRecord, Long> getIdentity() {
        return Keys.IDENTITY_CONTACT_PHONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ContactPhoneRecord> getPrimaryKey() {
        return Keys.CONTACT_PHONE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ContactPhoneRecord>> getKeys() {
        return Arrays.<UniqueKey<ContactPhoneRecord>>asList(Keys.CONTACT_PHONE_PKEY, Keys.CONTACT_PHONE_ID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<ContactPhoneRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ContactPhoneRecord, ?>>asList(Keys.CONTACT_PHONE__CONTACT_PHONE_CONTACT_NO_FKEY);
    }

    public Contact contact() {
        return new Contact(this, Keys.CONTACT_PHONE__CONTACT_PHONE_CONTACT_NO_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhone as(String alias) {
        return new ContactPhone(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhone as(Name alias) {
        return new ContactPhone(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContactPhone rename(String name) {
        return new ContactPhone(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContactPhone rename(Name name) {
        return new ContactPhone(name, null);
    }
}
