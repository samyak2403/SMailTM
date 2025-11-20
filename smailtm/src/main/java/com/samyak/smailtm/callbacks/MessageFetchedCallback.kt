package com.samyak.smailtm.callbacks

import com.samyak.smailtm.util.Message
import com.samyak.smailtm.util.Response

/**
 * Interface for handling callbacks when messages are fetched from a source.
 */
interface MessageFetchedCallback {
    /**
     * Called when messages are successfully fetched.
     *
     * @param messages the list of fetched messages
     */
    fun onMessagesFetched(messages: List<Message>)

    /**
     * Called when an error occurs during message fetching.
     *
     * @param error the error response indicating the reason for the failure
     */
    fun onError(error: Response)
}
