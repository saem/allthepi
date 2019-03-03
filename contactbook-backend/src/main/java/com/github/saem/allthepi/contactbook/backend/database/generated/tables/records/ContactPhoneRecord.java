/*
 * This file is generated by jOOQ.
 */
package com.github.saem.allthepi.contactbook.backend.database.generated.tables.records;


import com.github.saem.allthepi.contactbook.backend.database.generated.tables.ContactPhone;

import java.sql.Timestamp;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


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
public class ContactPhoneRecord extends UpdatableRecordImpl<ContactPhoneRecord> implements Record13<Long, UUID, Long, String, Integer, String, Long, String, String, Timestamp, Timestamp, Long, String> {

    private static final long serialVersionUID = -1911657069;

    /**
     * Setter for <code>contact_book.contact_phone.no</code>.
     */
    public void setNo(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.no</code>.
     */
    public Long getNo() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>contact_book.contact_phone.id</code>.
     */
    public void setId(UUID value) {
        set(1, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.id</code>.
     */
    public UUID getId() {
        return (UUID) get(1);
    }

    /**
     * Setter for <code>contact_book.contact_phone.contact_no</code>.
     */
    public void setContactNo(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.contact_no</code>.
     */
    public Long getContactNo() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>contact_book.contact_phone.type</code>.
     */
    public void setType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.type</code>.
     */
    public String getType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>contact_book.contact_phone.country_code</code>.
     */
    public void setCountryCode(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.country_code</code>.
     */
    public Integer getCountryCode() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>contact_book.contact_phone.area_code</code>.
     */
    public void setAreaCode(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.area_code</code>.
     */
    public String getAreaCode() {
        return (String) get(5);
    }

    /**
     * Setter for <code>contact_book.contact_phone.number</code>.
     */
    public void setNumber(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.number</code>.
     */
    public Long getNumber() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>contact_book.contact_phone.extension</code>.
     */
    public void setExtension(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.extension</code>.
     */
    public String getExtension() {
        return (String) get(7);
    }

    /**
     * Setter for <code>contact_book.contact_phone.raw</code>.
     */
    public void setRaw(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.raw</code>.
     */
    public String getRaw() {
        return (String) get(8);
    }

    /**
     * Setter for <code>contact_book.contact_phone.created_at</code>.
     */
    public void setCreatedAt(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.created_at</code>.
     */
    public Timestamp getCreatedAt() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>contact_book.contact_phone.modified_at</code>.
     */
    public void setModifiedAt(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.modified_at</code>.
     */
    public Timestamp getModifiedAt() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>contact_book.contact_phone.version</code>.
     */
    public void setVersion(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.version</code>.
     */
    public Long getVersion() {
        return (Long) get(11);
    }

    /**
     * Setter for <code>contact_book.contact_phone.trace_id</code>.
     */
    public void setTraceId(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>contact_book.contact_phone.trace_id</code>.
     */
    public String getTraceId() {
        return (String) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<Long, UUID, Long, String, Integer, String, Long, String, String, Timestamp, Timestamp, Long, String> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<Long, UUID, Long, String, Integer, String, Long, String, String, Timestamp, Timestamp, Long, String> valuesRow() {
        return (Row13) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return ContactPhone.CONTACT_PHONE.NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UUID> field2() {
        return ContactPhone.CONTACT_PHONE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field3() {
        return ContactPhone.CONTACT_PHONE.CONTACT_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return ContactPhone.CONTACT_PHONE.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return ContactPhone.CONTACT_PHONE.COUNTRY_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return ContactPhone.CONTACT_PHONE.AREA_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return ContactPhone.CONTACT_PHONE.NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return ContactPhone.CONTACT_PHONE.EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return ContactPhone.CONTACT_PHONE.RAW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return ContactPhone.CONTACT_PHONE.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return ContactPhone.CONTACT_PHONE.MODIFIED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field12() {
        return ContactPhone.CONTACT_PHONE.VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return ContactPhone.CONTACT_PHONE.TRACE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID component2() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component3() {
        return getContactNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getCountryCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getAreaCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component7() {
        return getNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getExtension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getRaw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component10() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component11() {
        return getModifiedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component12() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component13() {
        return getTraceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID value2() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value3() {
        return getContactNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getCountryCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getAreaCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value7() {
        return getNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getExtension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getRaw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value10() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value11() {
        return getModifiedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value12() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getTraceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value1(Long value) {
        setNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value2(UUID value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value3(Long value) {
        setContactNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value4(String value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value5(Integer value) {
        setCountryCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value6(String value) {
        setAreaCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value7(Long value) {
        setNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value8(String value) {
        setExtension(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value9(String value) {
        setRaw(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value10(Timestamp value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value11(Timestamp value) {
        setModifiedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value12(Long value) {
        setVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord value13(String value) {
        setTraceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactPhoneRecord values(Long value1, UUID value2, Long value3, String value4, Integer value5, String value6, Long value7, String value8, String value9, Timestamp value10, Timestamp value11, Long value12, String value13) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ContactPhoneRecord
     */
    public ContactPhoneRecord() {
        super(ContactPhone.CONTACT_PHONE);
    }

    /**
     * Create a detached, initialised ContactPhoneRecord
     */
    public ContactPhoneRecord(Long no, UUID id, Long contactNo, String type, Integer countryCode, String areaCode, Long number, String extension, String raw, Timestamp createdAt, Timestamp modifiedAt, Long version, String traceId) {
        super(ContactPhone.CONTACT_PHONE);

        set(0, no);
        set(1, id);
        set(2, contactNo);
        set(3, type);
        set(4, countryCode);
        set(5, areaCode);
        set(6, number);
        set(7, extension);
        set(8, raw);
        set(9, createdAt);
        set(10, modifiedAt);
        set(11, version);
        set(12, traceId);
    }
}
