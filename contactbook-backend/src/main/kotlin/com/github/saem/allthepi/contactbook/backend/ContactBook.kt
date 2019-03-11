package com.github.saem.allthepi.contactbook.backend

import arrow.core.Try
import arrow.core.recoverWith
import com.github.saem.allthepi.contactbook.api.Contact
import com.github.saem.allthepi.contactbook.api.Phone
import com.github.saem.allthepi.contactbook.backend.database.generated.Tables
import org.jooq.*
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import java.util.*

class ContactBook(private val jooqDsl: DSLContext) {

    fun createContact(newContact: Contact.Create): Try<Contact.Create.Result> {
        return Try {
            jooqDsl.insertInto(Tables.CONTACT,
                    Tables.CONTACT.ID,
                    Tables.CONTACT.FIRST_NAME,
                    Tables.CONTACT.LAST_NAME,
                    Tables.CONTACT.TRACE_ID)
                    .values(
                            newContact.data.id,
                            newContact.data.firstName,
                            newContact.data.lastName,
                            newContact.traceId
                    )
                    .returning(Tables.CONTACT.ID, Tables.CONTACT.VERSION)
                    .fetchOne()
                    .let {
                        Contact.Create.Result.Created(
                                it[Tables.CONTACT.ID],
                                it[Tables.CONTACT.VERSION])
                    }
        }.recoverWith {
            when {
                it is DataAccessException && it.sqlState().startsWith("23") ->
                    Try.Success(Contact.Create.Result.AlreadyExists(newContact, it))
                else -> Try.Failure(it)
            }
        }
    }

    fun listContacts(): Try<List<Contact>> = Try {
        jooqDsl.select(
                *Tables.CONTACT.fields(),
                *Tables.CONTACT_PHONE.fields())
                .from(Tables.CONTACT)
                .leftJoin(Tables.CONTACT_PHONE)
                .on(Tables.CONTACT.NO.eq(Tables.CONTACT_PHONE.CONTACT_NO))
                .where(Tables.CONTACT.NO.`in`(
                        jooqDsl.selectFrom(Tables.CONTACT)
                                .limit(10)
                                .fetch(Tables.CONTACT.NO))
                )
                .fetch()
                .let(this::resultSetToContact)
    }

    /**
     * Test this out when you get a chance.
     */
    fun listContacts2(): Try<List<Contact>> = Try {
        val results = jooqDsl.transactionResult { it: Configuration ->
            val dsl = DSL.using(it)
            val tenContact = name("ten_contact")

            /*
             * Instead of:
             * - firing many queries, each for a chance of a latency
             *   spike and carry the same overhead over and over
             * - firing one query which joins many rows and computes a
             *   potentially unnecessarily large data set and then sends it
             *   across
             *
             * Select it all at once and return a multi-result set, yay!
             */
            return@transactionResult dsl.fetchMany(
                    """
                        CREATE LOCAL UNLOGGED TABLE {0}
                        ON COMMIT DROP
                        AS {1};

                        {2};

                        {3};
                        """.trimIndent(),
                    /* 0 - temp table name */
                    tenContact,
                    /* 1 - the 10 contacts in the temp table, using this because
                     *     even though a natural order (unspecified) is fast
                     *     it's unstable across queries. Entropy strikes again!
                     */
                    dsl.selectFrom(Tables.CONTACT).limit(10),
                    /* 2 - actually select the 10 contact rows as a result */
                    dsl.select(*Tables.CONTACT.fields()).from(tenContact),
                    /* 3 - select phones as a result */
                    dsl.selectFrom(Tables.CONTACT_PHONE)
                            .where(Tables.CONTACT_PHONE.CONTACT_NO.`in`(
                                    dsl.select(Tables.CONTACT.NO)
                                            .from(tenContact)
                            ))
            )
        }

        val size = results.size

        return@Try emptyList<Contact>()
    }

    fun findContact(id: UUID): Try<Contact?> = Try {
        jooqDsl.select(
                *Tables.CONTACT.fields(),
                *Tables.CONTACT_PHONE.fields())
                .from(Tables.CONTACT)
                .leftJoin(Tables.CONTACT_PHONE)
                .on(Tables.CONTACT.NO.eq(Tables.CONTACT_PHONE.CONTACT_NO))
                .where(Tables.CONTACT.ID.eq(id))
                .fetch()
                .let(this::resultSetToContact)
                .firstOrNull()
    }

    fun updateContact(update: Contact.Update): Try<Contact.Update.Result> = Try {
        val updateQueryCteName = "contactUpdate"
        val updateQuery = jooqDsl.update(Tables.CONTACT)
                .set(Tables.CONTACT.FIRST_NAME, update.data.firstName.trim())
                .set(Tables.CONTACT.LAST_NAME, update.data.lastName.trim())
                .where(Tables.CONTACT.ID.eq(update.contactReference.id))
                .and(Tables.CONTACT.VERSION.eq(update.lastSeenVersion))
                .returning(
                        Tables.CONTACT.NO,
                        Tables.CONTACT.ID,
                        Tables.CONTACT.VERSION)

        val updateQueryContactNo = DSL.field(
                name(updateQueryCteName, Tables.CONTACT.NO.name),
                Long::class.java)
        val updateQueryContactId = DSL.field(
                name(updateQueryCteName, Tables.CONTACT.ID.name),
                UUID::class.java)

        val existenceUpdatedAndNewVersionQuery = jooqDsl
                .select(Tables.CONTACT.VERSION, updateQueryContactNo)
                .from(Tables.CONTACT)
                .leftJoin(name(updateQueryCteName))
                .on(Tables.CONTACT.ID.eq(updateQueryContactId))
                .where(Tables.CONTACT.ID.eq(updateQueryContactId))

        jooqDsl.resultQuery("""
            WITH "$updateQueryCteName" AS ({0})
            {1}
        """.trimIndent(),
                updateQuery,
                existenceUpdatedAndNewVersionQuery)
                .fetchOne()
                .let {
                    when {
                        it == null -> Contact.Update.Result.NotFound(update.contactReference)
                        it[1] != null -> Contact.Update.Result.Updated(it[1] as Long)
                        else -> Contact.Update.Result.VersionOutOfDate(it[0] as Long)
                    }
                }
    }

    fun deleteContact(delete: Contact.Delete): Try<Contact.Delete.Result> = Try {
        val contactCte = name("contact").fields("no", "versionMatch")
                .`as`(jooqDsl.select(
                        Tables.CONTACT.NO,
                        field(Tables.CONTACT.VERSION
                                .eq(delete.lastSeenVersion)))
                        .from(Tables.CONTACT)
                        .where(Tables.CONTACT.ID.eq(delete.reference.id))
                )

        val noField = contactCte.field("contact", Long::class.java)
        val versionMatch = contactCte.field("versionMatch",
                Boolean::class.java)

        val result = jooqDsl.with(contactCte)
                .delete(Tables.CONTACT)
                .where(Tables.CONTACT.NO.eq(noField))
                .and(versionMatch.isTrue)
                .returningResult(versionMatch)
                .fetchOptional()

        result.map {
            when (it.value1()) {
                false -> Contact.Delete.Result.VersionOutOfDate
                else -> Contact.Delete.Result.Deleted
            }
        }.orElse(Contact.Delete.Result.Deleted)
    }

    private fun resultSetToContact(rs: Result<Record>): List<Contact> {
        val contacts = mutableMapOf<Long, Contact>()
        val phones = mutableMapOf<Long, Set<Phone>>()

        rs.map {
            contacts[it[Tables.CONTACT.NO]] = Contact(
                    id = it[Tables.CONTACT.ID],
                    firstName = it[Tables.CONTACT.FIRST_NAME],
                    lastName = it[Tables.CONTACT.LAST_NAME],
                    createdAt = it[Tables.CONTACT.CREATED_AT].toInstant(),
                    modifiedAt = it[Tables.CONTACT.MODIFIED_AT].toInstant(),
                    version = it[Tables.CONTACT.VERSION]
            )

            val phone = Phone(
                    id = it[Tables.CONTACT_PHONE.ID],
                    countryCode = it[Tables.CONTACT_PHONE.COUNTRY_CODE],
                    areaCode = it[Tables.CONTACT_PHONE.AREA_CODE],
                    number = it[Tables.CONTACT_PHONE.NUMBER],
                    extension = it[Tables.CONTACT_PHONE.EXTENSION],
                    raw = it[Tables.CONTACT_PHONE.RAW],
                    type = it[Tables.CONTACT_PHONE.TYPE],
                    createdAt = it[Tables.CONTACT_PHONE.CREATED_AT].toInstant(),
                    modifiedAt = it[Tables.CONTACT_PHONE.MODIFIED_AT].toInstant(),
                    version = it[Tables.CONTACT_PHONE.VERSION]
            )
            phones.merge(
                    it[Tables.CONTACT_PHONE.CONTACT_NO],
                    setOf(phone)
            ) { a, b -> a + b }
        }

        return contacts.map {
            it.value.copy(phone_list = phones[it.key]
                    ?.toList()
                    ?: emptyList())
        }
    }
}