package com.github.saem.allthepi.contactbook.backend.database

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.opentable.db.postgres.embedded.LiquibasePreparer
import kotlin.concurrent.thread

fun main() {
    setupLocalPg()

    while (true) {}
}

fun setupLocalPg() {
    val postgresProcess = pgStart()
    createSchema(postgresProcess)
    runMigrations(postgresProcess)
}

fun pgStart(): EmbeddedPostgres {
    val postgresProcess = EmbeddedPostgres.builder()
            .setPort(12345)
            .start()
    Runtime.getRuntime().addShutdownHook(thread(false) {
        postgresProcess.close()
    })

    return postgresProcess
}

fun createSchema(postgresProcess: EmbeddedPostgres) {
    val conn = postgresProcess.postgresDatabase.connection
    val stmt = conn.createStatement()
    stmt.execute("CREATE SCHEMA contact_book")
    stmt.close()
}

fun runMigrations(postgresProcess: EmbeddedPostgres) {
    val ds = postgresProcess.getPostgresDatabase(mutableMapOf(
            "currentSchema" to "contact_book"))
    LiquibasePreparer.forClasspathLocation("db/liquibase/migrations.xml")
            .prepare(ds)
}