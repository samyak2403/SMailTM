package com.samyak.smalitm.callbacks

import com.samyak.smalitm.util.Message
import com.samyak.smalitm.util.Response

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
