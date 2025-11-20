package com.samyak.smalitm.util

import com.samyak.smalitm.exceptions.DateTimeParserException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * The Utility class provides various helper methods for common operations.
 */
object Utility {
    private const val REGEX = "abcdefghijklmnopqrstuvwxyz0123456789"

    /**
     * Generates a random string of specified length using alphanumeric characters.
     *
     * @param length the desired length of the random string
     * @return a randomly generated string containing only lowercase letters and numbers
     */
    fun createRandomString(length: Int): String {
        return (1..length)
            .map { REGEX[Random.nextInt(REGEX.length)] }
            .joinToString("")
    }

    /**
     * Parses a date string to a ZonedDateTime object using the specified pattern.
     *
     * @param dateTime the date string to parse
     * @param pattern the pattern to use for parsing the date
     * @return a ZonedDateTime object representing the parsed date
     * @throws DateTimeParserException if the date string cannot be parsed using the given pattern
     */
    @Throws(DateTimeParserException::class)
    fun parseToDefaultTimeZone(dateTime: String, pattern: String): ZonedDateTime {
        return try {
            LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern))
                .atZone(ZoneId.systemDefault())
        } catch (ex: Exception) {
            throw DateTimeParserException("Unable to parse Date for: $dateTime With Pattern $pattern")
        }
    }

    /**
     * Safely evaluates a supplier that might throw a NullPointerException.
     *
     * @param supplier the supplier to evaluate
     * @return the result of the supplier, or null if a NullPointerException occurs
     */
    fun <T> safeEval(supplier: () -> T): T? {
        return try {
            supplier()
        } catch (ex: NullPointerException) {
            null
        }
    }
}
