package com.samyak.smalitm.util

import com.samyak.smalitm.callbacks.WorkCallback
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread

/**
 * The Attachment class represents an email attachment.
 */
data class Attachment(
    val id: String = "",
    val filename: String = "",
    val contentType: String = "",
    val disposition: String = "",
    val transferEncoding: String = "",
    val related: Boolean = false,
    val size: Long = 0,
    val downloadUrl: String = "",
    val bearerToken: String = ""
) {
    /**
     * Gets the full download URL for the attachment.
     */
    fun getFullDownloadUrl(): String = "https://api.mail.tm$downloadUrl"

    /**
     * (Synchronous) Save the Attachment on System
     */
    fun saveSync(path: String, filename: String): Boolean {
        return try {
            val attachmentUrl = URL(getFullDownloadUrl())
            val connection = attachmentUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $bearerToken")
            
            if (connection.responseCode == 200) {
                Files.copy(connection.inputStream, Paths.get(path + filename))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * (Synchronous) Save the Attachment in the Working Directory With Custom Filename
     */
    fun saveSync(filename: String): Boolean = saveSync("./", filename)

    /**
     * (Synchronous) Save the Attachment in the Working Directory
     */
    fun saveSync(): Boolean = saveSync("./", filename)

    /**
     * (Asynchronous) Save the Attachment on System
     */
    fun save(path: String, filename: String, callback: WorkCallback) {
        thread(name = "Attachment_Download_$id") {
            callback.workStatus(saveSync(path, filename))
        }
    }

    /**
     * (Asynchronous) Save the Attachment in the Working Directory
     */
    fun save() {
        save("./", filename) {}
    }

    /**
     * (Asynchronous) Save the Attachment in the Working Directory with Callback Status
     */
    fun save(callback: WorkCallback) {
        save("./", filename, callback)
    }

    /**
     * (Asynchronous) Save the Attachment in the Working Directory With Custom Filename
     */
    fun save(filename: String) {
        save("./", filename) {}
    }

    /**
     * (Asynchronous) Save the Attachment in the Working Directory With Custom Filename and Callback
     */
    fun save(filename: String, callback: WorkCallback) {
        save("./", filename, callback)
    }
}
