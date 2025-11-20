package com.samyak.smailtm.exceptions

/**
 * Thrown When the Domain is Not Found or Does not Exists
 */
class DomainNotFoundException(errorMessage: String) : Exception(errorMessage)
