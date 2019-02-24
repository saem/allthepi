package com.github.saem.allthepi.contactbook.api

import com.github.saem.allthepi.contactbook.api.json.objectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

internal class ContactTest {
    @Test fun readAContact() {
        val uuid = UUID.randomUUID()
        val created = Instant.parse("2019-02-18T23:20:59.274Z")
        val modified = Instant.parse("2019-02-19T00:20:59.317Z")
        val expected = objectMapper.readTree("""
            {
            "id": "$uuid",
            "firstName": "",
            "lastName": "",
            "createdAt": "2019-02-18T23:20:59.274Z",
            "modifiedAt": "2019-02-19T00:20:59.317Z"
            }
        """.trimIndent())

        val actual = objectMapper.writeValueAsString(Contact(
                id = uuid,
                firstName = "",
                lastName = "",
                createdAt = created,
                modifiedAt = modified
        ))

        assertEquals(expected.toString(), actual)
    }


}