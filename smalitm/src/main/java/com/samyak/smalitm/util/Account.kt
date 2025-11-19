package com.samyak.smalitm.util

import com.samyak.smalitm.exceptions.DateTimeParserException
import java.time.ZonedDateTime

/**
 * The Account class represents a user account in the email system.
 */
data class Account(
    val id: String = "",
    val address: String = "",
    val quota: String = "",
    val used: String = "",
    val isDisabled: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    /**
     * Gets the email address.
     */
    val email: String
        get() = address

    /**
     * Get the account creation Date/Time in ZonedDateTime format
     * @throws DateTimeParserException when fail to parse
     */
    @Throws(DateTimeParserException::class)
    fun getCreatedDateTime(): ZonedDateTime {
        return Utility.parseToDefaultTimeZone(createdAt, "yyyy-MM-dd'T'HH:mm:ss'+00:00'")
    }

    /**
     * Get the account update Date/Time in ZonedDateTime format
     * @throws DateTimeParserException when fail to parse
     */
    @Throws(DateTimeParserException::class)
    fun getUpdatedDateTime(): ZonedDateTime {
        return Utility.parseToDefaultTimeZone(updatedAt, "yyyy-MM-dd'T'HH:mm:ss'+00:00'")
    }
}
