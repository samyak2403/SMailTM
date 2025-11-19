package com.samyak.smalitm.callbacks

import com.samyak.smalitm.util.Account
import com.samyak.smalitm.util.Message

/**
 * Interface for handling various events such as message deletion, reception, account updates, and errors.
 */
interface EventListener {
    /**
     * Invoked when the listener is ready to start receiving events.
     */
    fun onReady() {}

    /**
     * Invoked when the listener is closed or stopped.
     */
    fun onClose() {}

    /**
     * Invoked when a comment is received as part of SSE (Server-Sent Events).
     *
     * @param comment the SSE comment received
     */
    fun onSSEComment(comment: String) {}

    /**
     * Invoked when a new message is received.
     *
     * @param message the received message
     */
    fun onMessageReceived(message: Message) {}

    /**
     * Invoked when a message is deleted.
     *
     * @param id the ID of the deleted message
     */
    fun onMessageDelete(id: String) {}

    /**
     * Invoked when a message is seen by the recipient.
     *
     * @param message the seen message
     */
    fun onMessageSeen(message: Message) {}

    /**
     * Invoked when an account is deleted.
     *
     * @param account the deleted account
     */
    fun onAccountDelete(account: Account) {}

    /**
     * Invoked when an account is updated.
     *
     * @param account the updated account
     */
    fun onAccountUpdate(account: Account) {}

    /**
     * Invoked when an error occurs.
     *
     * @param error the error message
     */
    fun onError(error: String)
}
