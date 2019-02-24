package com.github.saem.allthepi.contactbook.backend.service

import arrow.core.Try
import com.github.saem.allthepi.contactbook.api.Contact
import com.github.saem.allthepi.contactbook.api.ContactCreation
import com.github.saem.allthepi.contactbook.api.NewContact
import com.github.saem.allthepi.contactbook.api.json.objectMapper
import com.github.saem.allthepi.contactbook.backend.ContactBook
import com.github.saem.allthepi.contactbook.backend.database.setupLocalPg
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.callIdMdc
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.JacksonConverter
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.AttributeKey
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.util.*

fun main() {
    val server = embeddedServer(
            Netty,
            8080,
            watchPaths = listOf("contactbook-backend"),
            module = Application::mainModule)
    server.start(wait = true)
}

fun Application.mainModule() {
    setupLocalPg()
    mainWithDeps(mainJdbcUrl = "jdbc:postgresql://localhost:12345/postgres?user=postgres")
}

fun Application.mainWithDeps(
        mainJdbcUrl: String
) {
    val requestIdKey = AttributeKey<String>("Call_Id")
    val mainHikariConfig = HikariConfig().apply {
        jdbcUrl = mainJdbcUrl
        // per jdbc spec, driver uses tz from the host JVM. For local dev, this
        // is lame, so we just always set UTC. This means that casting a
        // timestamp to a date (for grouping, for instance) will use UTC.
        connectionInitSql = "SET TIME ZONE 'UTC'"
    }

    val mainDs = HikariDataSource(mainHikariConfig)
    val jooqDsl = DSL.using(mainDs, SQLDialect.POSTGRES_10)
    val contactBook = ContactBook(jooqDsl)

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallId) {
        generate {
            UUID.randomUUID().toString()
        }

        verify { true }

        replyToHeader(HttpHeaders.XRequestId)
        reply { call, callId -> call.attributes.put(requestIdKey, callId) }
    }

    install(CallLogging) {
        callIdMdc("request-id")
    }

    routing {
        get("/") {
            call.respondText("{}", ContentType.Application.Json)
        }
        get("/contact_book") {
            when (val contacts = contactBook.listContacts()) {
                is Try.Success<List<Contact>> -> call.respond(contacts.value)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
        get("/contact_book/contact/{id}") {
            val id = when (val r = Try {
                UUID.fromString((call.parameters["id"] ?: ""))
            }) {
                is Try.Success<UUID> -> r.value
                else -> return@get call.respond(HttpStatusCode.BadRequest)
            }

            return@get when (val contact = contactBook.findContact(id)) {
                is Try.Success -> call.respond(contact.value)
                else -> call.respond(HttpStatusCode.BadRequest)
            }
        }
        post("/contact_book/contact") {
            val newContact = call.receiveOrNull<NewContact>()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

            return@post when (val creation = contactBook.createContact(newContact)) {
                is Try.Success<ContactCreation> -> when (val v = creation.value) {
                    is ContactCreation.Created -> call.created("/contact_book/contact/${v.reference.id}")
                    is ContactCreation.AlreadyExists -> call.respond(HttpStatusCode.Conflict)
                }
                is Try.Failure -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
        put("/contact_book/contact/{id}") {
            val id = when (val r = Try {
                UUID.fromString((call.parameters["id"] ?: ""))
            }) {
                is Try.Success<UUID> -> r.value
                else -> return@put call.respond(HttpStatusCode.BadRequest)
            }

            val updateData = call.receiveOrNull<Contact.Update.Data>()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

            return@put when (val r = contactBook.updateContact(
                    Contact.Update(Contact.Reference(id), "", updateData)
            )) {
                is Try.Failure -> call.respond(HttpStatusCode.InternalServerError)
                is Try.Success -> when(r.value) {
                    is Contact.Update.Result.Updated -> call.respond(HttpStatusCode.OK)
                    is Contact.Update.Result.UpdatingOldVersion -> call.respond(HttpStatusCode.Conflict)
                    is Contact.Update.Result.NotFound -> call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        delete("/contact_book/contact/{id}") {
            val id = when (val r = Try {
                UUID.fromString((call.parameters["id"] ?: ""))
            }) {
                is Try.Success<UUID> -> r.value
                else -> return@delete call.respond(HttpStatusCode.BadRequest)
            }

            when (contactBook.deleteContact(Contact.Reference(id))) {
                is Try.Success -> call.respond(HttpStatusCode.OK)
                is Try.Failure -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}

suspend inline fun ApplicationCall.respond(message: Any?) {
    when (message) {
        null -> response.pipeline.execute(this, HttpStatusCode.NotFound)
        else -> respond(message)
    }
}

suspend fun ApplicationCall.created(url: String) {
    response.headers.append(HttpHeaders.Location, url)
    respond(HttpStatusCode.Created)
}