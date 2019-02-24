package com.github.saem.allthepi.contactbook.api

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

internal class JacksonTest {
    @Test
    fun snakeCaseEnabledSerialization() {
        val actual = objectMapper.readTree(objectMapper.writeValueAsString(
                ShouldNotSnakeCase("foo", "bar")))
        val expected = objectMapper.readTree("""
            { "camelCase": "foo", "property_is_snake": "bar" }
        """.trimIndent())

        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun snakeCaseDisabledDeserialization() {
        val jsonString = """
            { "camelCase": "foo", "property_is_snake": "bar" }
        """.trimIndent()
        val actual = objectMapper.readValue(jsonString,
                ShouldNotSnakeCase::class.javaObjectType)

        assertEquals(ShouldNotSnakeCase("foo", "bar"), actual)
    }

    @Test
    fun dateFormatIsSetToIso() {
        val dateString = "2019-01-02T01:19:52.064Z"
        val date = Instant.parse(dateString)

        val expected = objectMapper.readTree("""{"date": "$dateString"}""")
        val actual = objectMapper.readTree(
                objectMapper.writeValueAsString(DateThing(date)))

        assertEquals(expected, actual)
    }

    @Test
    fun kotlinModuleLoaded() {
        assertTrue(objectMapper.registeredModuleIds
                .contains(KotlinModule().typeId),
                "KotlinModule was not registered")
    }
}

internal data class ShouldNotSnakeCase(
        val camelCase: String,
        val property_is_snake: String
)

internal data class DateThing(val date: Instant)