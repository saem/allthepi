package com.github.saem.allthepi.contactbook.api

import java.time.Instant
import java.util.*

data class ContactList(
        val contact_list: List<Contact>
)

data class Contact(
        val id: UUID,
        val firstName: String,
        val lastName: String,
        val createdAt: Instant,
        val modifiedAt: Instant,
        val phone_list: List<Phone> = emptyList()
) {
    data class Reference(val id: UUID)

    data class Create(
            val id: UUID = UUID.randomUUID(),
            val firstName: String,
            val lastName: String
    ) {
        sealed class Result {
            data class Created(val reference: Contact.Reference) : Result() {
                constructor(uuid: UUID) : this(Contact.Reference(uuid))
            }

            data class AlreadyExists(
                    val contactData: Create,
                    private val cause: Throwable
            ) : Result(), Error
        }
    }

    data class Read(
            val id: UUID,
            val firstName: String,
            val lastName: String,
            val createdAt: Instant,
            val modifiedAt: Instant,
            val phone_list: List<Phone> = emptyList()
    )

    data class Update(
            val contactReference: Reference,
            val lastSeenVersion: String,
            val data: Contact.Update.Data
    ) {
        data class Data(val firstName: String, val lastName: String)

        sealed class Result {
            data class Updated(val version: String) : Result()

            data class NotFound(val contactReference: Reference) : Result()

            data class UpdatingOldVersion(val newVersion: String) : Result()
        }
    }

    data class Delete(val reference: Reference) {
        sealed class Result {
            object Deleted : Result()
        }
    }
}

data class Phone(
        val id: UUID,
        val type: String,
        val countryCode: Int,
        val areaCode: String,
        val number: Long,
        val extension: String,
        val raw: String,
        val createdAt: Instant,
        val modifiedAt: Instant
)

data class Email(
        val id: UUID,
        val type: String,
        val local: String,
        val domain: String,
        val createdAt: Instant,
        val modifiedAt: Instant
)

data class Address(
        val id: UUID,
        val type: String,
        val country: String,
        val administrativeArea: String,
        val locality: String,
        val dependentLocality: String,
        val postalCode: String,
        val sortingCode: String,
        val organization: String,
        val recipient: String,
        val normalized: String,
        val geocode: String,
        val raw: String,
        val createdAt: Instant,
        val modifiedAt: Instant
)

data class NewContact(
        val id: UUID = UUID.randomUUID(),
        val firstName: String,
        val lastName: String
)

sealed class ContactCreation {
    data class Created(val reference: Contact.Reference) : ContactCreation() {
        constructor(uuid: UUID) : this(Contact.Reference(uuid))
    }

    data class AlreadyExists(
            val contactData: NewContact,
            private val cause: Throwable
    ) : ContactCreation(), Error
}

sealed class ContactDeletion {
    object Deleted : ContactDeletion()
}

data class AddPhone(
        val id: UUID = UUID.randomUUID(),
        val type: String,
        val raw: String,
        val countryHint: String
)

data class AddEmail(
        val id: UUID = UUID.randomUUID(),
        val type: String,
        val raw: String
)

data class AddAddress(
        val id: UUID = UUID.randomUUID(),
        val type: String,
        val raw: String,
        val countryHint: String,
        val locationHint: String
)

interface Error
interface EntityReference