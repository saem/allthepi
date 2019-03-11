package com.github.saem.allthepi.contactbook.backend.database

import org.jooq.codegen.GenerationTool

fun main() {
    generate()
}

fun generate(
        targetDir: String = "/home/saem/Development/allthepi/contactbook-backend/src/main/java/",
        config: String = "db/jooq/jooq-config.xml"
) {
    val input = GenerationTool::class.java.classLoader
            .getResourceAsStream(config)
    val conf = GenerationTool.load(input)

    conf.generator.target.directory = targetDir

    setupLocalPg()

    GenerationTool.generate(conf)
}