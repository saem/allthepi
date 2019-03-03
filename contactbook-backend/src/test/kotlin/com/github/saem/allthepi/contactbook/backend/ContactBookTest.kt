package com.github.saem.allthepi.contactbook.backend

import arrow.core.Try
import com.github.saem.allthepi.contactbook.api.Contact
import com.opentable.db.postgres.embedded.LiquibasePreparer
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ContactBookTest {
    companion object {
        @JvmField
        @RegisterExtension
        val pg = EmbeddedPostgresExtension.preparedDatabase(
                LiquibasePreparer.forClasspathLocation(
                        "db/liquibase/migrations.xml")
        )
    }

    @Test
    fun addAContact() {
        val jooqDsl = DSL.using(
                pg.testDatabase,
                SQLDialect.POSTGRES_10,
                Settings().withRenderSchema(false))
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
}