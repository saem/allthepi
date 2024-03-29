/*
 * This file is generated by jOOQ.
 */
package com.github.saem.allthepi.contactbook.backend.database.generated;


import javax.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in contact_book
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>contact_book.contact_no_seq</code>
     */
    public static final Sequence<Long> CONTACT_NO_SEQ = new SequenceImpl<Long>("contact_no_seq", ContactBook.CONTACT_BOOK, org.jooq.impl.SQLDataType.BIGINT.nullable(false));

    /**
     * The sequence <code>contact_book.contact_phone_no_seq</code>
     */
    public static final Sequence<Long> CONTACT_PHONE_NO_SEQ = new SequenceImpl<Long>("contact_phone_no_seq", ContactBook.CONTACT_BOOK, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
