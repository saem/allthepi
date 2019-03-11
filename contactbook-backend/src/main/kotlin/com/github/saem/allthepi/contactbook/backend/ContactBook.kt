package com.github.saem.allthepi.contactbook.backend

import arrow.core.Try
import arrow.core.recoverWith
import com.github.saem.allthepi.contactbook.api.Contact
import com.github.saem.allthepi.contactbook.api.Phone
import com.github.saem.allthepi.contactbook.backend.database.generated.Tables
import com.github.saem.allthepi.contactbook.backend.database.generated.tables.records.ContactRecord
import org.jooq.DSLContext
import org.jooq.Select
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

    /**
     * Test this out when you get a chance.
     */
    fun listContacts(): Try<List<Contact>> =
            findContacts(jooqDsl.selectFrom(Tables.CONTACT).limit(10))

    fun findContact(id: UUID): Try<Contact?> = findContacts(
            jooqDsl.selectFrom(Tables.CONTACT)
                    .where(Tables.CONTACT.ID.eq(id)))
            .map { it.firstOrNull() }

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

    private fun findContacts(
            tempTableQuery: Select<ContactRecord>
    ): Try<List<Contact>> = Try {
        val tempContacts = name("temp_contacts")
        val tempContactsContactNoField = DSL.field(
                name(tempContacts, Tables.CONTACT.NO.unqualifiedName),
                Long::class.java)

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
        val results = jooqDsl.fetchMany(
                """
                        {0};

                        {1};

                        {2};
                        """.trimIndent(),
                /* 0 - contact(s) to store in the temp table, using this because
                 *     even though a natural order (unspecified) is fast
                 *     it's unstable across queries. Entropy strikes again!
                 */
                jooqDsl.createTemporaryTable(tempContacts)
                        .`as`(tempTableQuery),
                /* 1 - select the contact rows as a result */
                jooqDsl.selectFrom<ContactRecord>(tempContacts),
                /* 2 - select phones as a result */
                jooqDsl.selectFrom(Tables.CONTACT_PHONE)
                        .where(Tables.CONTACT_PHONE.CONTACT_NO.`in`(
                                jooqDsl.select(tempContactsContactNoField)
                                        .from(tempContacts)
                        ))
        )

        val phones = results[1].groupBy(
                { it[Tables.CONTACT_PHONE.CONTACT_NO] },
                {
                    Phone(
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
                })

        return@Try results[0].map {
            Contact(
                    id = it[Tables.CONTACT.ID],
                    firstName = it[Tables.CONTACT.FIRST_NAME],
                    lastName = it[Tables.CONTACT.LAST_NAME],
                    createdAt = it[Tables.CONTACT.CREATED_AT].toInstant(),
                    modifiedAt = it[Tables.CONTACT.MODIFIED_AT].toInstant(),
                    version = it[Tables.CONTACT.VERSION],
                    phone_list = phones[it[Tables.CONTACT.NO]] ?: emptyList()
            )
        }
    }
}