package com.github.saem.allthepi.contactbook.backend.database

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.opentable.db.postgres.embedded.LiquibasePreparer
import org.jooq.codegen.GenerationTool
import kotlin.concurrent.thread

fun main() {
    generate()
}

fun generate(
        targetDir: String = "/home/saem/Development/allthepi/contactbook-backend/src/main/java/",
        config: String = "db/jooq/jooq-config.xml",
        pgStart: () -> EmbeddedPostgres = ::pgStart
) {
    val input = GenerationTool::class.java.classLoader
            .getResourceAsStream(config)
    val conf = GenerationTool.load(input)

    conf.generator.target.directory = targetDir

    val postgresProcess = pgStart()
    createSchema(postgresProcess)
    runMigrations(postgresProcess)

    GenerationTool.generate(conf)
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