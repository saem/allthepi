package com.github.saem.allthepi.contactbook.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
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
        @JsonInclude(Include.NON_EMPTY) val phone_list: List<Phone> = emptyList(),
        @JsonInclude(Include.NON_EMPTY) val email_list: List<Email> = emptyList(),
        @JsonInclude(Include.NON_EMPTY) val address_list: List<Address> = emptyList()
)

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

data class UpdateContact(
        val firstName: String,
        val lastName: String
)