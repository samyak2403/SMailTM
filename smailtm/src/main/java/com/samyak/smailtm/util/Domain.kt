package com.samyak.smailtm.util

/**
 * The Domain Class to Wrap Domains
 */
data class Domain(
    val id: String = "",
    val domain: String = "",
    val isActive: Boolean = false,
    val isPrivate: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    /**
     * Get DomainName (eg. example.com)
     */
    val domainName: String
        get() = domain
}
