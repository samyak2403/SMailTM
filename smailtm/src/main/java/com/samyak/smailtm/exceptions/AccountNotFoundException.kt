package com.samyak.smailtm.exceptions

/**
 * Thrown When an Account is not found during login
 */
class AccountNotFoundException(errorMessage: String) : Exception(errorMessage)
