package com.github.saem.allthepi.contactbook.api.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.saem.allthepi.contactbook.api.Contact
import java.text.DateFormat

val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())
        .addMixIn(Contact::class.java, ContactMixin::class.java)
        .setDateFormat(DateFormat.getDateInstance())