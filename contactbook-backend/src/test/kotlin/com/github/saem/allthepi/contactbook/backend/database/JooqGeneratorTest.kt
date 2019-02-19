package com.github.saem.allthepi.contactbook.backend.database

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertTrue

internal class JooqGeneratorTest {
    @Test
    fun testGenerateJooq(@TempDir tempDir: Path) {
        generate(targetDir = tempDir.toAbsolutePath().toString())
        val exists = Files.exists(tempDir.resolve("com/github/saem/allthepi/contactbook"))
        assertTrue(exists)
    }
}