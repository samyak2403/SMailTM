package com.samyak.smalitm.util

import com.google.gson.Gson
import com.samyak.smalitm.Config
import com.samyak.smalitm.callbacks.WorkCallback
import com.samyak.smalitm.exceptions.DateTimeParserException
import com.samyak.smalitm.io.IO
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import kotlin.concurrent.thread

/**
 * The Message class represents an email message in the system.
 */
data class Message(
    val id: String = "",
    val msgid: String = "",
    val from: Sender = Sender(),
    val to: Any? = null,
    val subject: String = "",
    val text: String = "",
    val seen: Boolean = false,
    val flagged: Boolean = false,
    val isDeleted: Boolean = false,
    val retention: Boolean = false,
    val retentionDate: String = "",
    val html: List<String> = emptyList(),
    val hasAttachments: Boolean = false,
    val attachments: List<Attachment> = emptyList(),
    val size: Long = 0,
    val downloadUrl: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val bearerToken: String = ""
) {
    private val log = LoggerFactory.getLogger(Message::class.java)

    /**
     * Gets the sender's email address.
     */
    val senderAddress: String
        get() = from.address

    /**
     * Gets the sender's display name.
     */
    val senderName: String
        get() = from.name

    /**
     * Gets the plain text content of the email.
     */
    val content: String
        get() = text

    /**
     * Gets the list of recipients.
     */
    fun getReceivers(): List<Receiver> {
        val receivers = mutableListOf<Receiver>()
        
        when (to) {
            is List<*> -> {
                to.forEach { item ->
                    when (item) {
                        is String -> receivers.add(Receiver(item, ""))
                        is Map<*, *> -> {
                            val address = item["address"] as? String ?: ""
                            val name = item["name"] as? String ?: ""
                            receivers.add(Receiver(address, name))
                        }
                    }
                }
            }
        }
        
        return receivers
    }

    /**
     * Gets the HTML content of the email.
     */
    fun getRawHTML(): String = html.toString()

    /**
     * Get the Message Received Date/Time in ZonedDateTime format
     * @throws DateTimeParserException when fail to parse
     */
    @Throws(DateTimeParserException::class)
    fun getCreatedDateTime(): ZonedDateTime {
        return Utility.parseToDefaultTimeZone(createdAt, "yyyy-MM-dd'T'HH:mm:ss'+00:00'")
    }

    /**
     * Get the Message Update Date/Time in ZonedDateTime format
     * @throws DateTimeParserException when fail to parse
     */
    @Throws(DateTimeParserException::class)
    fun getUpdatedDateTime(): ZonedDateTime {
        return Utility.parseToDefaultTimeZone(updatedAt, "yyyy-MM-dd'T'HH:mm:ss'+00:00'")
    }

    /**
     * (Synchronous) Deletes the Message
     */
    fun delete(): Boolean {
        if (isDeleted) return true
        
        return try {
            IO.requestDELETE("${Config.BASEURL}/messages/$id", bearerToken).responseCode == 204
        } catch (e: Exception) {
            log.warn("Failed to Delete message", e)
            false
        }
    }

    /**
     * (Synchronous) Deletes the Message with a Callback
     */
    fun delete(callback: WorkCallback) {
        callback.workStatus(delete())
    }

    /**
     * (Asynchronous) Silently Deletes the Message with no response
     */
    fun asyncDelete() {
        thread(name = "Delete_Message_$id") { delete() }
    }

    /**
     * (Asynchronous) Deletes the Message with a Callback
     */
    fun asyncDelete(callback: WorkCallback) {
        thread(name = "Delete_Message_$id") {
            callback.workStatus(delete())
        }
    }

    /**
     * (Synchronous) Marks the Message/Email as Read
     */
    fun markAsRead(): Boolean {
        if (seen) return true
        
        return try {
            IO.requestPATCH("${Config.BASEURL}/messages/$id", bearerToken).responseCode == 200
        } catch (e: Exception) {
            log.warn("Failed to mark message as read", e)
            false
        }
    }

    /**
     * (Sync) Marks the Message/Email asRead with a Callback
     */
    fun markAsRead(callback: WorkCallback) {
        callback.workStatus(markAsRead())
    }

    /**
     * (Async) Silently Marks the Message/Email asRead with no response
     */
    fun asyncMarkAsRead() {
        thread(name = "Mark_Message_As_Read_$id") { markAsRead() }
    }

    /**
     * (Async) Marks the Message/Email asRead with a Callback
     */
    fun asyncMarkAsRead(callback: WorkCallback) {
        thread(name = "Mark_Message_As_Read_$id") {
            markAsRead(callback)
        }
    }

    /**
     * Get the Raw JSON Response For Message
     */
    fun getRawJson(): String = Gson().toJson(this)

    @Deprecated("Use delete() instead", ReplaceWith("delete()"))
    fun deleteSync(): Boolean = delete()

    @Deprecated("Use delete(callback) instead", ReplaceWith("delete(callback)"))
    fun deleteSync(callback: WorkCallback) {
        callback.workStatus(deleteSync())
    }

    @Deprecated("Use markAsRead() instead", ReplaceWith("markAsRead()"))
    fun markAsReadSync(): Boolean = markAsRead()

    @Deprecated("Use markAsRead(callback) instead", ReplaceWith("markAsRead(callback)"))
    fun markAsReadSync(callback: WorkCallback) {
        callback.workStatus(markAsReadSync())
    }
}
