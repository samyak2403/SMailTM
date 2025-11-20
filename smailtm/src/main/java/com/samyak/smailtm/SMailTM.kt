package com.samyak.smailtm

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.launchdarkly.eventsource.EventSource
import com.samyak.smailtm.adapters.TokenAdapter
import com.samyak.smailtm.callbacks.EventListener
import com.samyak.smailtm.callbacks.MessageFetchedCallback
import com.samyak.smailtm.callbacks.MessageListener
import com.samyak.smailtm.callbacks.WorkCallback
import com.samyak.smailtm.exceptions.AccountNotFoundException
import com.samyak.smailtm.exceptions.MessageFetchException
import com.samyak.smailtm.io.IO
import com.samyak.smailtm.io.IOCallback
import com.samyak.smailtm.util.Account
import com.samyak.smailtm.util.Message
import com.samyak.smailtm.util.Response
import okhttp3.Headers
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * The SMailTM Class which handles the API instance and operations.
 * This class provides methods for managing email accounts, messages, and event handling.
 */
class SMailTM(
    private var bearerToken: String,
    private val id: String
) {
    private val log = LoggerFactory.getLogger(SMailTM::class.java)
    private val gson = GsonBuilder()
        .registerTypeAdapterFactory(TokenAdapter(bearerToken))
        .create()
    
    private var pool: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Retrieves the ID of the user account.
     */
    fun getId(): String = id

    /**
     * Initializes the SMailTM instance by performing necessary setup operations.
     * Note: This is only required when using createDefault()
     */
    fun init() {
        com.samyak.smailtm.util.Domains.updateDomains()
    }

    /**
     * Retrieves the account instance of the logged-in user.
     */
    fun getSelf(): Account {
        return try {
            val response = IO.requestGET("${Config.BASEURL}/me", bearerToken)
            if (response.responseCode == 200) {
                gson.fromJson(response.response, Account::class.java)
            } else {
                Account()
            }
        } catch (e: Exception) {
            log.error(e.toString())
            Account()
        }
    }

    /**
     * Deletes the self account in a synchronous manner.
     */
    fun delete(): Boolean {
        if (getSelf().isDeleted) return true
        
        return try {
            val response = IO.requestDELETE("${Config.BASEURL}/accounts/$id", bearerToken)
            response.responseCode == 204
        } catch (e: Exception) {
            log.error(e.toString())
            false
        }
    }

    /**
     * Deletes the self account and provides a callback with the status of the operation.
     */
    fun delete(callback: WorkCallback) {
        callback.workStatus(delete())
    }

    /**
     * Deletes the self account asynchronously and provides a callback with the status.
     */
    fun asyncDelete(callback: WorkCallback) {
        thread(name = "Delete_Account_$id") {
            callback.workStatus(delete())
        }
    }

    /**
     * Initiates the deletion of the self account in a separate thread.
     */
    fun asyncDelete() {
        thread(name = "Delete_Account_$id") { delete() }
    }

    /**
     * Retrieves a user account using the specified user ID.
     *
     * @throws AccountNotFoundException if the account with the specified ID is not found or an error occurs
     */
    @Throws(AccountNotFoundException::class)
    fun getAccountById(id: String): Account {
        return try {
            val response = IO.requestGET("${Config.BASEURL}/accounts/$id", bearerToken)
            if (response.responseCode == 200) {
                gson.fromJson(response.response, Account::class.java)
            } else {
                throw AccountNotFoundException("Invalid account id. Response: ${response.response}")
            }
        } catch (e: Exception) {
            throw AccountNotFoundException(e.toString())
        }
    }

    /**
     * Retrieves the total number of messages in the user's inbox.
     */
    fun getTotalMessages(): Int {
        return try {
            val response = IO.requestGET("${Config.BASEURL}/messages", bearerToken)
            val array = JsonParser.parseString(response.response).asJsonArray
            array.size()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Retrieves a single message object using the specified message ID.
     *
     * @throws MessageFetchException if the message with the specified ID cannot be fetched
     */
    @Throws(MessageFetchException::class)
    fun getMessageById(id: String): Message {
        return try {
            val response = IO.requestGET("${Config.BASEURL}/messages/$id", bearerToken)
            if (response.responseCode == 200) {
                gson.fromJson(response.response, Message::class.java)
            } else {
                throw MessageFetchException("Invalid message id. Response: ${response.response}")
            }
        } catch (e: Exception) {
            throw MessageFetchException(e.toString())
        }
    }

    /**
     * Fetches all messages and invokes a callback with the fetched messages or an error response.
     *
     * @throws MessageFetchException if fetching messages fails
     */
    @Throws(MessageFetchException::class)
    fun fetchMessages(callback: MessageFetchedCallback) {
        try {
            val messages = mutableListOf<Message>()
            val response = IO.requestGET("${Config.BASEURL}/messages", bearerToken)
            
            if (response.responseCode == 200) {
                val array = JsonParser.parseString(response.response).asJsonArray
                array.forEach { element ->
                    val id = element.asJsonObject.get("id").asString
                    messages.add(getMessageById(id))
                }
                callback.onMessagesFetched(messages)
            } else {
                callback.onError(Response(response.responseCode, response.response))
            }
        } catch (e: MessageFetchException) {
            throw e
        } catch (e: Exception) {
            throw MessageFetchException(e.toString())
        }
    }

    /**
     * Fetches the first limit number of messages and invokes a callback with the results.
     *
     * @throws MessageFetchException if fetching messages fails
     */
    @Throws(MessageFetchException::class)
    fun fetchMessages(limit: Int, callback: MessageFetchedCallback) {
        try {
            val messages = mutableListOf<Message>()
            val response = IO.requestGET("${Config.BASEURL}/messages", bearerToken)
            
            if (response.responseCode == 200) {
                val array = JsonParser.parseString(response.response).asJsonArray
                val stop = minOf(array.size(), limit)
                
                for (i in 0 until stop) {
                    val id = array[i].asJsonObject.get("id").asString
                    messages.add(getMessageById(id))
                }
                callback.onMessagesFetched(messages)
            } else {
                callback.onError(Response(response.responseCode, response.response))
            }
        } catch (e: MessageFetchException) {
            throw e
        } catch (e: Exception) {
            throw MessageFetchException(e.toString())
        }
    }

    /**
     * (Asynchronous) Initiates the fetching of all messages and invokes a callback with the fetched messages or an error response.
     */
    fun asyncFetchMessages(callback: MessageFetchedCallback) {
        thread(name = "Fetch_Messages_$id") {
            try {
                fetchMessages(callback)
            } catch (e: MessageFetchException) {
                callback.onError(Response(90001, e.toString()))
            }
        }
    }

    /**
     * (Asynchronous) Initiates the fetching of the first limit number of messages and invokes a callback with the fetched messages or an error response.
     */
    fun asyncFetchMessages(limit: Int, callback: MessageFetchedCallback) {
        thread(name = "Fetch_Messages_$id") {
            try {
                fetchMessages(limit, callback)
            } catch (e: MessageFetchException) {
                callback.onError(Response(90001, e.toString()))
            }
        }
    }

    /**
     * (Asynchronous) Opens an event listener on a single thread to receive server-sent events (SSE).
     */
    fun openEventListener(eventListener: EventListener, retryInterval: Long) {
        if (pool.isShutdown) {
            pool = Executors.newSingleThreadExecutor()
        }
        
        val headers = Headers.Builder()
            .add("Authorization", "Bearer $bearerToken")
            .build()
        
        val sse = EventSource.Builder(
            IOCallback(eventListener, this),
            URI.create("${Config.MERCURE_URL}?topic=/accounts/$id")
        ).headers(headers)
        
        val sourceSSE = sse.build()
        pool.execute { sourceSSE.start() }
    }

    /**
     * (Asynchronous) Open's a default event listener on a single thread
     */
    fun openEventListener(eventListener: EventListener) {
        openEventListener(eventListener, 3000L)
    }

    /**
     * Closes the message listener, shutting down the thread pool used for event handling.
     */
    fun closeMessageListener() {
        pool.shutdown()
    }

    /**
     * (Asynchronous) Opens a Message Listener on a New Thread
     */
    @Deprecated("Use openEventListener instead")
    fun openMessageListener(messageListener: MessageListener, retryInterval: Long) {
        openEventListener(object : EventListener {
            override fun onReady() = messageListener.onReady()
            override fun onClose() = messageListener.onClose()
            override fun onMessageReceived(message: Message) = messageListener.onMessageReceived(message)
            override fun onError(error: String) = messageListener.onError(error)
        }, retryInterval)
    }

    /**
     * (Asynchronous) Opens a MessageListener on a New Thread Default Refresh Time 1.5 seconds
     */
    @Deprecated("Use openEventListener instead")
    fun openMessageListener(messageListener: MessageListener) {
        openMessageListener(messageListener, 3000)
    }

    @Deprecated("Use delete() instead", ReplaceWith("delete()"))
    fun deleteSync(): Boolean = delete()

    @Deprecated("Use delete(callback) instead", ReplaceWith("delete(callback)"))
    fun deleteSync(callback: WorkCallback) {
        callback.workStatus(deleteSync())
    }
}
