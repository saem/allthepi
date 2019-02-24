package com.github.saem.allthepi.contactbook.api.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.github.saem.allthepi.contactbook.api.Phone

/**
 * Temporarily using a Mixin until I can figure out specifying different
 * protocols from an abstract one.
 *
 * Contact.kt should define the abstract one, while this should define the JSON
 * one.
 */
interface ContactMixin {
    @get:JsonInclude(Include.NON_EMPTY) val phone_list: List<Phone>
}
