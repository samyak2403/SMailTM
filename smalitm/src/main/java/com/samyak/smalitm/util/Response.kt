package com.samyak.smalitm.util

/**
 * The Response class represents an HTTP response from the server.
 */
data class Response(
    val responseCode: Int,
    val response: String
)
