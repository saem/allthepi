package com.github.saem.allthepi.contactbook.backend.service

import com.github.saem.allthepi.contactbook.api.Contact
import com.github.saem.allthepi.contactbook.api.ContactList
import com.github.saem.allthepi.contactbook.api.Phone
import com.github.saem.allthepi.contactbook.backend.database.setupLocalPg
import com.github.saem.allthepi.contactbook.api.objectMapper
import com.github.saem.allthepi.contactbook.backend.database.generated.Tables
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.callIdMdc
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.jackson.JacksonConverter
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
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
            jooqDsl.select(
                    *Tables.CONTACT.fields(),
                    *Tables.CONTACT_PHONE.fields())
                    .from(Tables.CONTACT)
                    .leftJoin(Tables.CONTACT_PHONE)
                    .on(Tables.CONTACT.NO.eq(Tables.CONTACT_PHONE.CONTACT_NO))
                    .limit(10)
                    .fetch()
                    .let { rs ->
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

                        contacts.map {
                            it.value.copy(phone_list = phones[it.key]
                                    ?.toList()
                                    ?: emptyList())
                        }
                    }

            call.respond(ContactList(emptyList()))
        }
    }
}