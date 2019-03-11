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
        val version: Version,
        val phone_list: List<Phone> = emptyList()
) {
    data class Reference(val id: UUID)

    data class Create(
            val traceId: TraceId,
            val data: Create.Data
    ) {
        data class Data (
                val id: UUID = UUID.randomUUID(),
                val firstName: String,
                val lastName: String
        )

        sealed class Result {
            data class Created(
                    val reference: Contact.Reference,
                    val version: Version
            ) : Result() {
                constructor(uuid: UUID, version: Version) :
                        this(Contact.Reference(uuid), version)
            }

            data class AlreadyExists(
                    val contactData: Create,
                    val cause: Throwable
            ) : Result(), Error
        }
    }

    data class Update(
            val contactReference: Reference,
            val lastSeenVersion: Version,
            val traceId: TraceId,
            val data: Contact.Update.Data
    ) {
        data class Data(val firstName: String, val lastName: String)

        sealed class Result {
            data class Updated(val version: Version) : Result()

            data class NotFound(val contactReference: Reference) : Result()

            data class VersionOutOfDate(val newVersion: String) : Result() {
                constructor(newVersion: Long) : this(newVersion.toString())
            }
        }
    }

    data class Delete(
            val reference: Reference,
            val lastSeenVersion: Version,
            val traceId: TraceId
    ) {
        constructor(uuid: UUID, lastSeenVersion: Version, traceId: TraceId) :
                this(Contact.Reference(uuid), lastSeenVersion, traceId)

        sealed class Result {
            object Deleted : Result()
            object VersionOutOfDate : Result()
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
        val modifiedAt: Instant,
        val version: Version
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
typealias Version = Long
typealias TraceId = String