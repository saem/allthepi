package com.github.saem.allthepi.contactbook.api

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

internal class JacksonTest {
    @Test
    fun snakeCaseEnabledSerialization() {
        val actual = objectMapper.readTree(objectMapper.writeValueAsString(
                ShouldSnakeCase("foo", "bar")))
        val expected = objectMapper.readTree("""
            { "property_to_snake": "foo", "property_is_snake": "bar" }
        """.trimIndent())

        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun snakeCaseEnabledDeserialization() {
        val jsonString = """
            { "property_to_snake": "foo", "property_is_snake": "bar" }
        """.trimIndent()
        val actual = objectMapper.readValue(jsonString,
                ShouldSnakeCase::class.javaObjectType)

        assertEquals(ShouldSnakeCase("foo", "bar"), actual)
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

internal data class ShouldSnakeCase(
        val propertyToSnake: String,
        val property_is_snake: String
)

internal data class DateThing(val date: Instant)