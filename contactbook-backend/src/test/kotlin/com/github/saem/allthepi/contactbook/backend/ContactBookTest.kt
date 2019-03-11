package com.github.saem.allthepi.contactbook.backend

import arrow.core.Try
import com.github.saem.allthepi.contactbook.api.Contact
import com.opentable.db.postgres.embedded.LiquibasePreparer
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension
import com.opentable.db.postgres.junit5.PreparedDbExtension
import org.jooq.DSLContext
import org.jooq.ExecuteContext
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.jooq.impl.DefaultExecuteListener
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ContactBookTest {
    companion object {
        @JvmField
        @RegisterExtension
        val pg: PreparedDbExtension = EmbeddedPostgresExtension.preparedDatabase(
                LiquibasePreparer.forClasspathLocation(
                        "db/liquibase/migrations.xml")
        )
    }

    private val jooqDsl: DSLContext = DSL.using(
            pg.testDatabase,
            SQLDialect.POSTGRES_10,
            Settings().withRenderSchema(false)
    ).also {
        it.configuration().set(object : DefaultExecuteListener() {
            override fun executeStart(ctx: ExecuteContext?) {
                val dsl = DSL.using(it.dialect(), Settings().withRenderFormatted(true))

                ctx?.also {
                    (ctx.query()?.let { q -> dsl.renderInlined(q) }
                            ?: ctx.routine()?.let { r -> dsl.renderInlined(r) })
                            .also { m -> println(m) }
                }
            }
        })
    }

    @Test
    fun addAContact() {
        val contactBook = ContactBook(jooqDsl)

        val actual = contactBook.createContact(Contact.Create(
                traceId = "foo",
                data = Contact.Create.Data(
                        firstName = "Lex",
                        lastName = "Dray"
                )
        ))

        actual.map {
            return@map when (it) {
                is Contact.Create.Result.Created -> assertTrue(true)
                is Contact.Create.Result.AlreadyExists ->
                    fail("Somehow the contact already exists", it.cause)
            }
        }

        when (actual) {
            is Try.Failure -> fail<Any>(actual.exception)
        }
    }

    @Test
    fun updateAContact() {
        val contactBook = ContactBook(jooqDsl)

        val initial = contactBook.createContact(Contact.Create(
                traceId = "foo",
                data = Contact.Create.Data(
                        firstName = "Lex",
                        lastName = "Dray"
                )
        )).let {
            when (it) {
                is Try.Failure -> fail<Contact.Create.Result.Created>(
                        "Failed to create initial contact", it.exception)
                is Try.Success -> when (val r = it.value) {
                    is Contact.Create.Result.Created -> r
                    is Contact.Create.Result.AlreadyExists ->
                        fail<Contact.Create.Result.Created>(
                                "Supposedly contact exists"
                        )
                }
            }
        }

        val actual = contactBook.updateContact(Contact.Update(
                contactReference = initial.reference,
                traceId = "foo",
                lastSeenVersion = initial.version,
                data = Contact.Update.Data("Omar", "Shaq")
        ))

        when (actual) {
            is Try.Failure -> fail<Any>("Failed to update contact",
                    actual.exception)
            is Try.Success -> when (actual.value) {
                is Contact.Update.Result.VersionOutOfDate -> fail<Any>(
                        "Version out of date")
                is Contact.Update.Result.NotFound -> fail<Any>(
                        "Contact not found"
                )
                is Contact.Update.Result.Updated -> assertTrue(true)
            }
        }
    }

    @Test
    fun listContacts() {
        val contactBook = ContactBook(jooqDsl)

        contactBook.createContact(Contact.Create("", Contact.Create.Data(
                firstName = "Firstfirst",
                lastName = "Firstlast"
        )))

        contactBook.createContact(Contact.Create("", Contact.Create.Data(
                firstName = "Secondfirst",
                lastName = "Secondlast"
        )))

        contactBook.listContacts().let {
            when (it) {
                is Try.Failure -> fail<Any>("Query failed", it.exception)
                is Try.Success -> assertEquals(3, it.value.size)
            }
        }
    }

    @Test
    fun findContact() {
        val contactBook = ContactBook(jooqDsl)

        contactBook.createContact(Contact.Create("",
                Contact.Create.Data(
                        firstName = "Firstfirst",
                        lastName = "Firstlast"
                )))
                .flatMap {
                    when (it) {
                        is Contact.Create.Result.Created -> contactBook.findContact(it.reference.id)
                        else -> fail("Contact wasn't created with result: $it")
                    }
                }
                .let {
                    when (it) {
                        is Try.Failure -> fail("Failed to get contact", it.exception)
                        is Try.Success -> assertTrue(true)
                    }
                }
    }
}