package com.samyak.smalitm.exceptions

/**
 * Thrown When the Domain is Not Found or Does not Exists
 */
class DomainNotFoundException(errorMessage: String) : Exception(errorMessage)
