package com.github.saem.allthepi.contactbook.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.text.DateFormat

val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())
        .setDateFormat(DateFormat.getDateInstance())