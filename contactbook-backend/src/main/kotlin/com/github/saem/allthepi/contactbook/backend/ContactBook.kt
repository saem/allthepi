package com.github.saem.allthepi.contactbook.backend

import arrow.core.Try
import arrow.core.recoverWith
import com.github.saem.allthepi.contactbook.api.*
import com.github.saem.allthepi.contactbook.backend.database.generated.Tables
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.exception.DataAccessException
import java.util.*

class ContactBook(private val jooqDsl: DSLContext) {

    fun createContact(newContact: NewContact): Try<ContactCreation> {
        return Try {
            jooqDsl.insertInto(Tables.CONTACT,
                    Tables.CONTACT.ID)
                    .values(newContact.id)
                    .returning(Tables.CONTACT.ID)
                    .fetchOne()[Tables.CONTACT.ID]
                    .let { ContactCreation.Created(it) }
        }.recoverWith {
            when {
                it is DataAccessException && it.sqlState().startsWith("23") ->
                    Try.Success(ContactCreation.AlreadyExists(newContact, it))
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

    fun updateContact(update: Contact.Update) : Try<Contact.Update.Result> = Try {
        jooqDsl.update(Tables.CONTACT)
                .set(Tables.CONTACT.FIRST_NAME, update.data.firstName.trim())
                .set(Tables.CONTACT.LAST_NAME, update.data.lastName.trim())
                .where(Tables.CONTACT.ID.eq(update.contactReference.id))
                .returning()
                .let { Contact.Update.Result.Updated("") }
    }

    fun deleteContact(contactReference: Contact.Reference) : Try<ContactDeletion> {
        return Try {
            jooqDsl.deleteFrom(Tables.CONTACT)
                    .where(Tables.CONTACT.ID.eq(contactReference.id))
                    .execute()
                    .let { ContactDeletion.Deleted }
        }
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
                    modifiedAt = it[Tables.CONTACT.MODIFIED_AT].toInstant()
            )

            val phone = Phone(
                    id = it[Tables.CONTACT_PHONE.ID],
                    countryCode = it[Tables.CONTACT_PHONE.COUNTRY_CODE],
                    areaCode = it[Tables.CONTACT_PHONE.AREA_CODE],
                    number = it[Tables.CONTACT_PHONE.NUMBER],
                    extension = it[Tables.CONTACT_PHONE.EXTENSION],
                    raw = it[Tables.CONTACT_PHONE.RAW],
                    type = it[Tables.CONTACT_PHONE.TYPE],
                    createdAt = it[Tables.CONTACT.CREATED_AT].toInstant(),
                    modifiedAt = it[Tables.CONTACT.MODIFIED_AT].toInstant()
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