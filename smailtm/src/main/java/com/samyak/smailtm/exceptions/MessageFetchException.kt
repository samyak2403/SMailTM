package com.samyak.smailtm.exceptions

/**
 * Thrown when something goes wrong while fetching messages
 */
class MessageFetchException(errorMessage: String) : Exception(errorMessage)
